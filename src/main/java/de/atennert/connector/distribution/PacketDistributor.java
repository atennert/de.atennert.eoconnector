/*******************************************************************************
 * Copyright (C) 2014 Andreas Tennert. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *******************************************************************************/

package org.atennert.connector.distribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.atennert.connector.distribution.PacketListenerObservable.ListenerActions;
import org.atennert.connector.packets.IPacketConstants;
import org.atennert.connector.packets.Packet;
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
    private final Map< String, IPacketListener > selectedListeners = new HashMap< String, IPacketListener >();

    /** Map with plug-in properties. */
    private final Map< String, Properties > properties = new HashMap< String, Properties >();

    /**
     * This flag contains if the packet evaluation is running or not which is
     * mapped to listeners being activated or not. It secures that a
     * deactivation follows every activation and the other way around.
     */
    private boolean active = false;

    private final ExecutorService executor = Executors.newFixedThreadPool( 3 );

    private final PacketListenerObservable model;
    private final BlockingQueue< Packet > sendMessageQueue;

    public PacketDistributor( PacketListenerObservable model, BlockingQueue< Packet > sendMessageQueue ) {
        this.model = model;
        this.sendMessageQueue = sendMessageQueue;
    }

    /**
     * Distribute a packet to all active listeners.
     * 
     * @param packet
     */
    public synchronized void distributePacket( Packet packet ) {
        if( active && !selectedListeners.isEmpty() ) {
            executor.execute( new DistributionHandler( packet, new HashSet< IPacketListener >( selectedListeners
                    .values() ) ) );
        }
    }

    /**
     * Initialize all listeners that are marked as active.
     */
    public synchronized void activateListeners() {
        if( !active ) {
            for( final String name : selectedListeners.keySet() ) {
                selectedListeners.get( name ).initialize( properties.get( name ), sendMessageQueue );
            }
            active = true;
            log.debug( "Activated listeners." );
        }
    }

    /**
     * Close all active listeners.
     */
    public synchronized void closeListeners() {
        if( active ) {
            for( final IPacketListener l : selectedListeners.values() ) {
                l.close();
            }
            active = false;
            log.debug( "Stopped listeners." );
        }
    }

    /**
     * Adds a packet listener to the list of active listeners.
     * 
     * @param id name (ID) of the packet listener
     * @param listener the packet listener to add
     * @param properties the properties of the packet listener
     */
    public synchronized void addListener( String id, IPacketListener listener, Properties properties ) {
        if( !active ) {
            selectedListeners.put( id, listener );
            this.properties.put( id, properties );

            if( model != null ) {
                model.listenerEvent( id, ListenerActions.USE );
            }
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
            properties.remove( id );

            if( model != null ) {
                model.listenerEvent( id, ListenerActions.UNUSE );
            }
        }
    }

    /**
     * Remove all packet listeners from the list of active listeners and remove
     * their properties.
     */
    public synchronized void clear() {
        final Set< String > listeners = selectedListeners.keySet();
        selectedListeners.clear();
        properties.clear();

        if( model != null ) {
            for( final String id : listeners ) {
                model.listenerEvent( id, ListenerActions.UNUSE );
            }
        }
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
                }
            }
        }
    }
}
