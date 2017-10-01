package de.atennert.connector.facade;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.atennert.connector.IEventListener;
import de.atennert.connector.distribution.IPacketListener;
import de.atennert.connector.distribution.PacketDistributor;
import de.atennert.connector.facade.FacadeSM.State;
import de.atennert.connector.packets.IPacketFactory;
import de.atennert.connector.packets.Packet;
import de.atennert.connector.packets.PacketFactory;
import de.atennert.connector.reader.ComConnector;
import de.atennert.connector.reader.ComConnector.ConnectionStatus;
import de.atennert.connector.reader.PacketDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a facade for applications that use the EOC as a library. It
 * initializes the required resources and offers methods for the addition and
 * removal of packet listeners, port listeners and factories. Furthermore it has
 * methods to start and stop the data acquisition.<br>
 * <br>
 * The Facade doesn't use a packet listener model and a packet listener observer
 * because the applications should know which packet listeners are added.
 * 
 * @author Andreas Tennert
 */
public class ConnectorFacade {

    private static final Logger log = LoggerFactory.getLogger( ConnectorFacade.class );
    private FacadeSM.State state;

    private final ComConnector connector;
    private final PacketDecoder consumer;
    private final PacketFactory mainFactory;
    private final PacketDistributor distributor;

    /**
     * Initialize the EnOcean framework. This will set up the message queues,
     * the main factory and the classes for connecting to the EnOcean
     * transceiver and message distribution.
     */
    public ConnectorFacade() {
        final BlockingQueue< Integer > receiveByteQueue = new LinkedBlockingQueue< Integer >();
        final BlockingQueue< Packet > sendPacketQueue = new LinkedBlockingQueue< Packet >();
        this.mainFactory = new PacketFactory();
        this.distributor = new PacketDistributor( null, sendPacketQueue );
        this.connector = new ComConnector( receiveByteQueue, sendPacketQueue );
        this.consumer = new PacketDecoder( receiveByteQueue, distributor, mainFactory );

        state = State.ENTRY;
        initialize();
    }

    /**
     * Set the new current state. This must only be used from the state machine.
     * 
     * @param newState the new state to set
     */
    void setState( State newState ) {
        state = newState;
    }

    /**
     * General method to trigger a transition from one state to the next.
     * 
     * @param tm model that holds data needed to make the transition
     */
    private synchronized void makeTransition( AbstractTransitionModel tm ) {
        if( !state.handle( tm ) ) {
            log.warn( "Transition failed / action could not be executed!" );
        }
    }

    /**
     * Make first transition to get into a defined state.
     */
    private void initialize() {
        makeTransition( new InitializeModel( this ) );
    }

    /**
     * Start to read EnOcean messages and distribute them to packet listeners
     * and start the sending of messages.
     */
    public void startAcquisition() {
        makeTransition( new AcquisitionModel( this, connector, consumer, distributor, AcquisitionModel.START ) );
    }

    /**
     * Stop the reading and sending of EnOcean messages.
     */
    public void stopAcquisition() {
        makeTransition( new AcquisitionModel( this, connector, consumer, distributor, AcquisitionModel.STOP ) );
    }

    /**
     * Add a packet listener to the distributor. This listener will receive
     * EnOcean data packets.
     * 
     * @param packetListener the packet listener to add
     * @param properties the properties for the packet listener
     */
    public void addPacketListener( IPacketListener packetListener, Properties properties ) {
        makeTransition( new PacketListenerModel( this, distributor, packetListener.toString(), packetListener,
                properties, PacketListenerModel.ADD ) );
    }

    /**
     * Add a packet listener to the distributor. This listener will receive
     * EnOcean data packets.
     * 
     * @param id the id of the packet listener
     * @param packetListener the packet listener to add
     * @param properties the properties for the packet listener
     */
    public void addPacketListener( String id, IPacketListener packetListener, Properties properties ) {
        makeTransition( new PacketListenerModel( this, distributor, id, packetListener, properties,
                PacketListenerModel.ADD ) );
    }

    /**
     * Remove a packet listener from the distributor.
     * 
     * @param packetListener the packet listener to remove
     */
    public void removePacketListener( IPacketListener packetListener ) {
        makeTransition( new PacketListenerModel( this, distributor, packetListener.toString(), null, null,
                PacketListenerModel.REMOVE ) );
    }

    /**
     * Add a port listener to the connector. This listener will repeatedly
     * receive updates for available serial ports (names in form of strings)
     * whenever the data acquisition is <em>NOT</em> active.
     * 
     * @param portListener The port listener to add
     */
    public void addPortListener( IEventListener< List< String >> portListener ) {
        makeTransition( new PortListenerModel( this, connector.getPortUpdater(), portListener, PortListenerModel.ADD ) );
    }

    /**
     * Remove a port listener from the connector.
     * 
     * @param portListener the port listener to remove
     */
    public void removePortListener( IEventListener< List< String >> portListener ) {
        makeTransition( new PortListenerModel( this, connector.getPortUpdater(), portListener, PortListenerModel.REMOVE ) );
    }

    /**
     * Add a connection listener to the connector. This listener will receive
     * updates for status changes from the connector.
     * 
     * @param connectionListener the connectionListener listener to add
     */
    public void addConnectionListener( IEventListener< ConnectionStatus > connectionListener ) {
        makeTransition( new ConnectionListenerModel( this, connector, connectionListener, PortListenerModel.ADD ) );
    }

    /**
     * Remove a connectionListener listener from the connector.
     * 
     * @param connectionListener the connectionListener listener to remove
     */
    public void removeConnectionListener( IEventListener< ConnectionStatus > connectionListener ) {
        makeTransition( new ConnectionListenerModel( this, connector, connectionListener, PortListenerModel.REMOVE ) );
    }

    /**
     * Set a serial port to use, which is the one where the EnOcean transceiver
     * is connected. The port must be set <em>before</em> starting the data
     * acquisition.
     * 
     * @param port
     */
    public void setPort( String port ) {
        makeTransition( new SetPortModel( this, connector, port ) );
    }

    /**
     * Add a packet factory that extends the main packet factory. Those
     * factories can be used to create more useful instances of {@link Packet}
     * for custom types of data packets. Specific types for all EnOcean defined
     * messages are covered by the main factory.
     * 
     * @param factory the factory to add to the main factory.
     */
    public void addPacketFactory( IPacketFactory factory ) {
        makeTransition( new PacketFactoryModel( this, mainFactory, factory, PacketFactoryModel.ADD ) );
    }

    /**
     * Remove a packet factory from the main packet factory.
     * 
     * @param factory the factory to remove
     */
    public void removePacketFactory( IPacketFactory factory ) {
        makeTransition( new PacketFactoryModel( this, mainFactory, factory, PacketFactoryModel.REMOVE ) );
    }
}
