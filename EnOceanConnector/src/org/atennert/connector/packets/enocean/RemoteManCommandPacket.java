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
 * This class implements the EnOcean defined remote management command packet
 * (type: {@link IPacketConstants#TYPE_REMOTE_MAN_COMMAND}). Remote management
 * command packets contain required data and optional data. The complete data
 * structure is as follows:
 * <ul>
 * <li>required data part
 * <ul>
 * <li>function number</li>
 * <li>manufacturer ID</li>
 * <li>message ID</li>
 * </ul>
 * </li>
 * <li>optional data part
 * <ul>
 * <li>destination ID</li>
 * <li>source ID</li>
 * <li>RSSI value (<code>0xFF</code> when sending a packet)</li>
 * <li>send with delay</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Andreas Tennert
 */
public class RemoteManCommandPacket extends Packet {

    /**
     * General constructor with required and optional data parts.
     * 
     * @param data required data
     * @param optional optional data
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RemoteManCommandPacket( int[] data, int[] optional, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_REMOTE_MAN_COMMAND, data, optional, timestamp, isValid );
    }

    /**
     * Constructor with separated required and optional data parts.
     * 
     * @param functionNumber Range 0x0000 ... 0x0FFF
     * @param manufacturerID Range 0x0000 ... 0x07FF
     * @param messageData 0 ... 511 bytes
     * @param destinationID Destination ID (Broadcast ID: 0xFFFFFFFF)
     * @param sourceID Receive case: Source ID of sender; send case: 0x00000000
     * @param dBm Send case 0xFF; Receive case: Best RSSI value of all received
     *            sub telegrams
     * @param sendWithDelay 1: if the first message has to be sent with random
     *            delay. When answering to broadcast message this has to be 1;
     *            otherwise 0
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RemoteManCommandPacket( int[] functionNumber, int[] manufacturerID, int[] messageData, int[] destinationID,
            int[] sourceID, int dBm, int sendWithDelay, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_REMOTE_MAN_COMMAND, new int[functionNumber.length + manufacturerID.length
                + messageData.length], new int[10], timestamp, isValid );
        // fill data
        System.arraycopy( functionNumber, 0, data, 0, 2 );
        System.arraycopy( manufacturerID, 0, data, 2, 2 );
        System.arraycopy( messageData, 0, data, 6, messageData.length );

        // fill optional
        System.arraycopy( destinationID, 0, optional, 0, 4 );
        System.arraycopy( sourceID, 0, optional, 4, 4 );
        this.optional[8] = dBm;
        this.optional[9] = sendWithDelay;
    }

    /**
     * @return the function number
     */
    public int[] getFunctionNumber() {
        final int[] functionNumber = new int[2];
        System.arraycopy( data, 0, functionNumber, 0, 2 );
        return functionNumber;
    }

    /**
     * @return the manufacturer ID
     */
    public int[] getManufacturerID() {
        final int[] manufacturerID = new int[2];
        System.arraycopy( data, 2, manufacturerID, 0, 4 );
        return manufacturerID;
    }

    /**
     * @return the message data
     */
    public int[] getMessageData() {
        final int[] messageData = new int[data.length - 4];
        System.arraycopy( data, 4, messageData, 0, messageData.length );
        return messageData;
    }

    /**
     * @return the destination ID
     */
    public int[] getDestinationID() {
        final int[] destinationID = new int[4];
        System.arraycopy( optional, 0, destinationID, 0, 4 );
        return destinationID;
    }

    /**
     * @return the source ID
     */
    public int[] getSourceID() {
        final int[] sourceID = new int[4];
        System.arraycopy( optional, 4, sourceID, 0, 4 );
        return sourceID;
    }

    /**
     * @return the RSSI value
     */
    public int getDBm() {
        return optional[8];
    }

    /**
     * @return the send with delay value (1 = true, 0 = false)
     */
    public int getSendWithDelay() {
        return optional[9];
    }
}
