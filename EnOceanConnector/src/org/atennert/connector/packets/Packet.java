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
 * This class is the basic representation of an EnOcean data packet. It holds
 * the required and optional data parts as well as the packet type, the time
 * when the data packet was received and flag that describes whether the data is
 * correct or not, based on the CRC algorithm that has been chosen EnOcean.
 * 
 * @author Andreas Tennert
 */
public class Packet {
    public final int type;

    protected final int[] data;
    protected final int[] optional;

    public final Date timestamp;

    public final boolean isValid;

    /**
     * @param type the type of the EnOcean data packet, the general EnOcean
     *            defined type can be found in {@link IPacketConstants} (don't
     *            use TYPE_ANY!)
     * @param data the required data part
     * @param optional the optional data part
     * @param timestamp the time when the packet was received
     * @param isValid <code>true</code> if the packet data is correct by the CRC
     *            algorithm, used by EnOcean
     */
    public Packet( int type, int[] data, int[] optional, Date timestamp, boolean isValid ) {
        this.type = type;
        this.data = data;
        this.optional = optional;
        this.timestamp = timestamp;
        this.isValid = isValid;
    }

    /**
     * @return a clone of the required data
     */
    public int[] getData() {
        return data.clone();
    }

    /**
     * @return a clone of the optional data
     */
    public int[] getOptional() {
        return optional.clone();
    }
}
