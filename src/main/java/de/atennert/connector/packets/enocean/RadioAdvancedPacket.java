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

package org.atennert.connector.packets.enocean;

import java.util.Date;

import org.atennert.connector.packets.IPacketConstants;
import org.atennert.connector.packets.Packet;

/**
 * This class implements the EnOcean defined radio advanced packet (type:
 * {@link IPacketConstants#TYPE_RADIO_ADVANCED}). Radio advanced packets contain
 * raw data in the required data part and the sub telegram number as well as the
 * RSSI value in the optional data part. If this packet is used to send a
 * message, the RSSI value has to be <code>0xFF</code>. If it is received, it
 * will be the best RSSI value of all received sub telegrams.
 * 
 * @author Andreas Tennert
 */
public class RadioAdvancedPacket extends Packet {

    /**
     * General constructor.
     * 
     * @param data the required data part, which contains raw data
     * @param optional optional data, which contains the sub telegram number and
     *            the RSSI value
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioAdvancedPacket( int[] data, int[] optional, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO_ADVANCED, data, optional, timestamp, isValid );
    }

    /**
     * Constructor with separated raw data, sub telegram number and RSSI value.
     * 
     * @param data the raw data
     * @param subTelNum the sub telegram number
     * @param dBm the RSSI value
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioAdvancedPacket( int[] data, int subTelNum, int dBm, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO_ADVANCED, data, new int[] { subTelNum, dBm }, timestamp, isValid );
    }

    /**
     * @return the sub telegram number
     */
    public int getSubTelNum() {
        if( optional.length > 0 ) {
            return optional[0];
        }
        else {
            return -1;
        }
    }

    /**
     * @return the RSSI value
     */
    public int getDBm() {
        if( optional.length > 0 ) {
            return optional[1];
        }
        else {
            return -1;
        }
    }
}
