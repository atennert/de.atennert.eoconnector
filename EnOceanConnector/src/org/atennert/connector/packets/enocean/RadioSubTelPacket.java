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
 * This class implements the EnOcean defined radio sub telegram packet (type:
 * {@link IPacketConstants#TYPE_RADIO_SUB_TEL}). Radio sub telegram packets are
 * used EnOcean internally for diagnosis and statistics purposes. They contain
 * required and optional data. The complete data structure is as follows:
 * <ul>
 * <li>required data part (may differ in some cases)
 * <ul>
 * <li>R-ORG value</li>
 * <li>variable user data</li>
 * <li>sender ID</li>
 * <li>status</li>
 * </ul>
 * </li>
 * <li>optional data part
 * <ul>
 * <li>sub telegram number</li>
 * <li>destination ID</li>
 * <li>RSSI value (<code>0xFF</code> when sending a packet)</li>
 * <li>security level</li>
 * <li>time stamp</li>
 * <li>[
 * <ul>
 * <li>tick sub telegram</li>
 * <li>RSSI value of sub telegram</li>
 * <li>status of sub telegram</li>
 * </ul>
 * ]</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Andreas Tennert
 */
public class RadioSubTelPacket extends Packet {

    /**
     * General constructor with required and optional data parts.
     * 
     * @param data required data
     * @param optional optional data
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioSubTelPacket( int[] data, int[] optional, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO_SUB_TEL, data, optional, timestamp, isValid );
    }

    /**
     * Constructor with required data separated optional data parts.
     * 
     * @param data the required data part
     * @param subTelNum the sub telegram number
     * @param destinationID the destination ID
     * @param dBm the RSSI value
     * @param securityLevel the security level
     * @param msgTimestamp time stamp of first sub telegram, system timer tick
     * @param tickSubTel relative time of sub telegrams in relation to time
     *            stamp
     * @param dBmSubTel RSSI values of sub telegrams
     * @param statusSubTel telegram control bits of each sub telegram
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioSubTelPacket( int[] data, int subTelNum, int[] destinationID, int dBm, int securityLevel,
            int[] msgTimestamp, int[] tickSubTel, int[] dBmSubTel, int[] statusSubTel, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO_SUB_TEL, data, new int[9 + ( tickSubTel.length * 3 )], timestamp, isValid );

        this.optional[0] = subTelNum;
        System.arraycopy( destinationID, 0, optional, 1, 4 );
        this.optional[5] = dBm;
        this.optional[6] = securityLevel;
        System.arraycopy( timestamp, 0, optional, 7, 2 );

        for( int i = 0, count = 9; i < tickSubTel.length; i++ ) {
            this.optional[count++] = tickSubTel[i];
            this.optional[count++] = dBmSubTel[i];
            this.optional[count++] = statusSubTel[i];
        }
    }

    /**
     * Constructor with separated required and optional data.
     * 
     * @param rOrg the R-ORG value
     * @param userData the user data
     * @param senderID the sender ID
     * @param status the status
     * @param subTelNum the sub telegram number
     * @param destinationID the destination ID
     * @param dBm the RSSI value
     * @param securityLevel the security level
     * @param msgTimestamp time stamp of first sub telegram, system timer tick
     * @param tickSubTel relative time of sub telegrams in relation to the time
     *            stamp
     * @param dBmSubTel RSSI values of sub telegrams
     * @param statusSubTel telegram control bits of each sub telegram
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioSubTelPacket( int rOrg, int[] userData, int[] senderID, int status, int subTelNum, int[] destinationID,
            int dBm, int securityLevel, int[] msgTimestap, int[] tickSubTel, int[] dBmSubTel, int[] statusSubTel,
            Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO_SUB_TEL, new int[6 + userData.length],
                new int[9 + ( tickSubTel.length * 3 )], timestamp, isValid );

        this.data[0] = rOrg;
        System.arraycopy( userData, 0, this.data, 1, userData.length );
        System.arraycopy( senderID, 0, data, userData.length, senderID.length );
        this.data[userData.length + senderID.length] = status;

        this.optional[0] = subTelNum;
        System.arraycopy( destinationID, 0, optional, 1, 4 );
        this.optional[5] = dBm;
        this.optional[6] = securityLevel;
        System.arraycopy( timestamp, 0, optional, 7, 2 );

        for( int i = 0, count = 9; i < tickSubTel.length; i++ ) {
            this.optional[count++] = tickSubTel[i];
            this.optional[count++] = dBmSubTel[i];
            this.optional[count++] = statusSubTel[i];
        }
    }
}
