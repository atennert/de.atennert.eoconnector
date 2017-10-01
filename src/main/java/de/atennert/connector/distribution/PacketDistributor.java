package de.atennert.connector.distribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.atennert.connector.distribution.PacketListenerObservable.ListenerActions;
import de.atennert.connector.packets.IPacketConstants;
import de.atennert.connector.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the distribution of packets to packet listeners. It owns a list of
 * packet listeners, that are selected for use, as well as their properties. The
 * listeners will be initialized on activation and then receive packets. When
 * the evaluation of packets stops, the listeners will be closed, but remain
 * selected.
 * 
 * @author Andreas Tennert
 */
public class PacketDistributor {

    private static final Logger log = LoggerFactory.getLogger( PacketDistributor.class );

    /**
     * Map with selected packet listeners. They will be initialized and receive
     * packets.
     */
    private final Map< String, IPacketListener > selectedListeners = new HashMap<>();

    /**
     * This flag contains if the packet evaluation is running or not which is
     * mapped to listeners being activated or not. It secures that a
     * deactivation follows every activation and the other way around.
     */
    private boolean active = false;

    private final ExecutorService executor = Executors.newFixedThreadPool( 3 );

    /**
     * Distribute a packet to all active listeners.
     * 
     * @param packet
     */
    public synchronized void distributePacket( Packet packet ) {
        if( active && !selectedListeners.isEmpty() ) {
            executor.execute( new DistributionHandler( packet, new HashSet<>( selectedListeners
                    .values() ) ) );
        }
    }

    /**
     * Adds a packet listener to the list of active listeners.
     * 
     * @param id name (ID) of the packet listener
     * @param listener the packet listener to add
     */
    public synchronized void addListener( String id, IPacketListener listener ) {
        if( !active ) {
            selectedListeners.put( id, listener );
        }
    }

    /**
     * Remove a packet listener from the list of active listeners.
     * 
     * @param id name (ID) of the packet listener
     */
    public synchronized void removeListener( String id ) {
        if( !active ) {
            selectedListeners.remove( id );
        }
    }

    /**
     * Remove all packet listeners from the list of active listeners and remove
     * their properties.
     */
    public synchronized void clear() {
        selectedListeners.clear();
    }

    /**
     * This handler distributes a packet to the selected listeners. An instance
     * of it will be created for each incoming packet and it is given to an
     * ExecutorService for execution.
     */
    private class DistributionHandler implements Runnable {
        private final Packet packet;
        private final Set< IPacketListener > listeners;

        private DistributionHandler( Packet packet, Set< IPacketListener > listeners ) {
            this.packet = packet;
            this.listeners = listeners;
        }

        @Override
        public void run() {
            for( final IPacketListener listener : listeners ) {
                try {
                    for( final int packetType : listener.getSupportedPackets() ) {
                        if( packetType == IPacketConstants.TYPE_ANY || packetType == packet.type ) {
                            listener.receivePacket( packet );
                            break;
                        }
                    }
                }
                catch( final Exception e ) {
                    /*
                     * This may for instance happen if getSupportedPackets()
                     * returns null.
                     */
                    log.warn( "Failed to distribute a packet to a listener!" );
                    e.printStackTrace();
                }
            }
        }
    }
}
