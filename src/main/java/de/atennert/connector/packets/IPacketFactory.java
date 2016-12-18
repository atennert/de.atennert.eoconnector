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

import java.util.Date;

/**
 * This is the general interface for packet factories. Packet factories are used
 * to create packets from the received EnOcean message raw data. <br>
 * <br>
 * Factories, that are added to an instance of {@link PacketFactory} (extension
 * factories), must return <code>null</code> when a requested data packet type
 * a.k.a. message type is not covered!
 * 
 * @author Andreas Tennert
 */
public interface IPacketFactory {
    /**
     * Create a new {@link Packet} from raw data.
     * 
     * @param type the data packet type. The types for EnOcean messages can be
     *            found in {@link IPacketConstants}. Do not use TYPE_ANY!
     * @param data the data part of the EnOcean message (obligatory data)
     * @param optional the optional part of the EnOcean message (optional data)
     * @param timestamp the time when the EnOcean message was received by the
     *            EOC
     * @param isValid <code>true</code> if the calculated message checksum
     *            matched the attached checksum from the message
     * @return a packet instance that contains the raw data or <code>null</code>
     *         if the implementation is an extension factory for
     *         {@link PacketFactory} and the given type is not covered
     */
    public Packet createPacket( int type, int[] data, int[] optional, Date timestamp, boolean isValid );
}
