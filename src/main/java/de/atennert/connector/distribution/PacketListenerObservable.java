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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.atennert.connector.IDistributor;
import org.atennert.connector.IEventListener;
import org.atennert.connector.distribution.PacketListenerObservable.ListenerAction;

/**
 * This class is used to distribute events of packet listeners to registered
 * observers. The events are not necessarily distributed in order of appearance
 * because the broadcast is done using an {@link ExecutorService}. <br>
 * <br>
 * Listeners have to implement the interface {@link IEventListener}&lt;
 * {@link ListenerAction}&gt;. Possible actions are defined by the enumeration
 * {@link ListenerActions}.
 * 
 * @author Andreas Tennert
 */
public class PacketListenerObservable implements IDistributor< ListenerAction > {

    private final ExecutorService executor = Executors.newFixedThreadPool( 2 );

    /**
     * Enumeration that defines the available actions for packet listeners.
     */
    public enum ListenerActions {
        /** A listener was added to the set of available listeners. */
        ADD,
        /** A listener was removed from the set of available listeners. */
        REMOVE,
        /**
         * An available listener is supposed to be used. When the evaluation
         * starts it will be initialized and it will receive packets.
         */
        USE,
        /**
         * A listener that was registered as used will not be used anymore. It
         * will NOT receive any packets and NOT be initialized.
         */
        UNUSE
    }

    /**
     * This class defines the event type for actions with packet listeners. It
     * contains the name (ID) of the listener and the action that was performed.
     */
    public class ListenerAction {
        public final String name;
        public final ListenerActions action;

        private ListenerAction( String name, ListenerActions action ) {
            this.name = name;
            this.action = action;
        }
    }

    /** List of observers */
    private final List< IEventListener< ListenerAction >> observers = new ArrayList< IEventListener< ListenerAction >>();

    /**
     * Add an observer for packet listener actions.
     * 
     * @param observer
     */
    public void addListener( IEventListener< ListenerAction > observer ) {
        synchronized( observers ) {
            observers.add( observer );
        }
    }

    /**
     * Remove observer for packet listener actions.
     * 
     * @param observer
     */
    @Override
    public void removeListener( IEventListener< ListenerAction > observer ) {
        synchronized( observers ) {
            observers.remove( observer );
        }
    }

    /**
     * Distribute a packet listener action event.
     * 
     * @param name Name (ID) of the packet listener
     * @param action Packet listener action
     */
    void listenerEvent( String name, ListenerActions action ) {
        final ListenerAction newAction = new ListenerAction( name, action );
        synchronized( observers ) {
            executor.execute( new Runnable() {
                @Override
                public void run() {
                    for( final IEventListener< ListenerAction > l : observers ) {
                        l.onEvent( newAction );
                    }
                }
            } );
        }
    }
}
