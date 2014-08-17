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

package org.atennert.connector.packets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.atennert.connector.packets.enocean.CommonCommandPacket;
import org.atennert.connector.packets.enocean.EventPacket;
import org.atennert.connector.packets.enocean.RadioAdvancedPacket;
import org.atennert.connector.packets.enocean.RadioPacket;
import org.atennert.connector.packets.enocean.RadioSubTelPacket;
import org.atennert.connector.packets.enocean.RemoteManCommandPacket;
import org.atennert.connector.packets.enocean.ResponsePacket;
import org.atennert.connector.packets.enocean.SmartAckCommandPacket;

/**
 * This factory class creates {@link Packet}s for EnOcean messages. If the
 * message type is a standard EnOcean message, it uses an extension of the
 * packet class. Otherwise, the factory will use the default packet type. <br>
 * <br>
 * It is possible to extend an instance of this factory with additional custom
 * factories (extension factories), that use custom extensions of {@link Packet}
 * for non-standard messages. If an additional library does not cover a
 * requested message type it has to return <code>null</code>! In the current
 * implementation, this class always creates packets itself if it knows the
 * packet type, rather then using an extension factory, that has its own
 * implementation for this type. So the order trying to create a packet is as
 * follows:
 * <ol>
 * <li>Try to create specific packet by itself</li>
 * <li>Try additional factories in order of addition until the return value !=
 * <code>null</code></li>
 * <li>Create an instance of the default class {@link Packet}</li>
 * </ol>
 * 
 * @author Andreas Tennert
 */
public class PacketFactory implements IPacketFactory {

    private final List< IPacketFactory > factories = new ArrayList< IPacketFactory >();

    @Override
    public Packet createPacket( int type, int[] data, int[] optional, Date timestamp, boolean isValid ) {
        switch( type ) {
            case IPacketConstants.TYPE_RADIO:
                return new RadioPacket( data, optional, timestamp, isValid );
            case IPacketConstants.TYPE_RESPONSE:
                return new ResponsePacket( data, timestamp, isValid );
            case IPacketConstants.TYPE_RADIO_SUB_TEL:
                return new RadioSubTelPacket( data, optional, timestamp, isValid );
            case IPacketConstants.TYPE_EVENT:
                return new EventPacket( data, timestamp, isValid );
            case IPacketConstants.TYPE_COMMON_COMMAND:
                return new CommonCommandPacket( data, optional, timestamp, isValid );
            case IPacketConstants.TYPE_SMART_ACK_COMMAND:
                return new SmartAckCommandPacket( data, timestamp, isValid );
            case IPacketConstants.TYPE_REMOTE_MAN_COMMAND:
                return new RemoteManCommandPacket( data, optional, timestamp, isValid );
            case IPacketConstants.TYPE_RADIO_ADVANCED:
                return new RadioAdvancedPacket( data, optional, timestamp, isValid );
            default:
                // try with external factory
                Packet externalPacket;
                synchronized( this ) {
                    for( final IPacketFactory factory : factories ) {
                        externalPacket = factory.createPacket( type, data, optional, timestamp, isValid );
                        if( externalPacket != null ) {
                            return externalPacket;
                        }
                    }
                }
                // no factory for packet type available -> make default packet
                return new Packet( type, data, optional, timestamp, isValid );
        }
    }

    /**
     * Add a factory to the list of extension factories.
     * 
     * @param factory an additional extension factory
     * @return <code>true</code> if the factory was added, <code>false</code>
     *         otherwise
     */
    public synchronized boolean addFactory( IPacketFactory factory ) {
        return factories.add( factory );
    }

    /**
     * Remove a factory from the list of extension factories.
     * 
     * @param factory the extension factory to remove
     * @return <code>true</code> if the factory was removed from the list,
     *         <code>false</code> otherwise
     */
    public synchronized boolean removeFactory( IPacketFactory factory ) {
        return factories.remove( factory );
    }

    /**
     * Remove all factories from the list of extension factories.
     */
    public synchronized void clearFactories() {
        factories.clear();
    }
}
