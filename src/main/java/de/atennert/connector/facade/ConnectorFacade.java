package de.atennert.connector.facade;

import com.sun.istack.internal.NotNull;
import de.atennert.connector.IEnOceanConnector;
import de.atennert.connector.distribution.IEventListener;
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

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * This class is a facade for applications that use the EOC as a library. It
 * initializes the required resources and offers methods for the addition and
 * removal of packet listeners, port listeners and factories. Furthermore it has
 * methods to start and stop the data acquisition.<br>
 * <br>
 * The Facade doesn't use a packet listener model and a packet listener observer
 * because the applications should know which packet listeners are added.
 */
public class ConnectorFacade implements IEnOceanConnector {

    private static final Logger log = LoggerFactory.getLogger( ConnectorFacade.class );
    private FacadeSM.State state;

    private final ComConnector comConnector;
    private final PacketDecoder packetDecoder;
    private final PacketFactory packetFactory;
    private final PacketDistributor packetDistributor;
    private final BlockingQueue<Packet> sendPacketQueue;

    /**
     * Initialize the EnOcean framework. This will set up the message queues,
     * the main factory and the classes for connecting to the EnOcean
     * transceiver and message distribution.
     */
    public ConnectorFacade( @NotNull final PacketFactory packetFactory,
                            @NotNull final PacketDistributor packetDistributor,
                            @NotNull final ComConnector comConnector,
                            @NotNull final PacketDecoder packetDecoder,
                            @NotNull final BlockingQueue<Packet> sendPacketQueue ) {
        this.packetFactory = packetFactory;
        this.packetDistributor = packetDistributor;
        this.comConnector = comConnector;
        this.packetDecoder = packetDecoder;
        this.sendPacketQueue = sendPacketQueue;

        state = State.ENTRY;
        makeTransition( new InitializeModel( this ) );
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
        if (!state.handle( tm )) {
            log.warn( "Transition failed / action could not be executed!" );
        }
    }

    @Override
    public void startDataAcquisition() {
        makeTransition( new AcquisitionModel( this, comConnector, packetDecoder, packetDistributor, AcquisitionModel.START ) );
    }

    @Override
    public void stopDataAcquisition() {
        makeTransition( new AcquisitionModel( this, comConnector, packetDecoder, packetDistributor, AcquisitionModel.STOP ) );
    }

    @Override
    public void addPacketListener( IPacketListener packetListener ) {
        makeTransition( new PacketListenerModel( this, packetDistributor, packetListener.toString(), packetListener,
                PacketListenerModel.ADD ) );
    }

    @Override
    public void removePacketListener( IPacketListener packetListener ) {
        makeTransition( new PacketListenerModel( this, packetDistributor, packetListener.toString(), null,
                PacketListenerModel.REMOVE ) );
    }

    @Override
    public void addPortListener( IEventListener<List<String>> portListener ) {
        makeTransition( new PortListenerModel( this, comConnector.getPortUpdater(), portListener, PortListenerModel.ADD
        ) );
    }

    @Override
    public void removePortListener( IEventListener<List<String>> portListener ) {
        makeTransition( new PortListenerModel( this, comConnector.getPortUpdater(), portListener, PortListenerModel
                .REMOVE ) );
    }

    @Override
    public void addConnectionListener( IEventListener<ConnectionStatus> connectionListener ) {
        makeTransition( new ConnectionListenerModel( this, comConnector, connectionListener, PortListenerModel.ADD ) );
    }

    @Override
    public void removeConnectionListener( IEventListener<ConnectionStatus> connectionListener ) {
        makeTransition( new ConnectionListenerModel( this, comConnector, connectionListener, PortListenerModel.REMOVE ) );
    }

    @Override
    public void setPort( String port ) {
        makeTransition( new SetPortModel( this, comConnector, port ) );
    }

    @Override
    public void addPacketFactory( IPacketFactory factory ) {
        makeTransition( new PacketFactoryModel( this, packetFactory, factory, PacketFactoryModel.ADD ) );
    }

    @Override
    public void removePacketFactory( IPacketFactory factory ) {
        makeTransition( new PacketFactoryModel( this, packetFactory, factory, PacketFactoryModel.REMOVE ) );
    }

    @Override
    public void sendDataPacket( Packet packet ) {
        makeTransition( new SendMessageModel( this, sendPacketQueue, packet ) );
    }
}
