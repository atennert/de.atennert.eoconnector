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

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.atennert.connector.packets.IPacketConstants;
import org.atennert.connector.packets.Packet;

/**
 * Interface for packet listeners. The listeners will be initialized by the
 * framework and receive the EnOcean data packets. It will be closed after the
 * operation is finished. The listener needs to hand over the packet types it
 * supports. The types can be found in {@link IPacketConstants}.
 * 
 * @author Andreas Tennert
 */
public interface IPacketListener {

    /**
     * Receive new data packets.
     * 
     * @param packet EnOcean data packet
     */
    public void receivePacket( Packet packet );

    /**
     * Initialize the listener. This can be used to set up the plugin back-end.
     * 
     * @param properties Available properties for listener, may be
     *            <code>null</code>
     * @param sendMessageQueue Queue for sending EnOcean data packets
     */
    public void initialize( Properties properties,
            BlockingQueue< Packet > sendMessageQueue );

    /**
     * Close the listener. Tear down the plugin back-end.
     */
    public void close();

    /**
     * @return the supported packet types of the listener or
     *         {@link IPacketConstants#TYPE_ANY} if the listener supports all
     *         packet types.
     */
    public int[] getSupportedPackets();
}
