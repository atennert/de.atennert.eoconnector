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

package org.atennert.connector.reader;

import org.atennert.connector.packets.Packet;

/**
 * This class provides methods to encode instances of {@link Packet} into byte
 * messages for sending them away using an EnOcean transceiver.
 * 
 * @author Andreas Tennert
 */
public final class PacketEncoder {

    /**
     * @param packet a packet representing an EnOcean message
     * @return the byte code for the given packet
     */
    public static byte[] encodePacket( Packet packet ) {
        final int[] data = packet.getData();
        final int[] optional = packet.getOptional();
        final int type = packet.type;

        final byte[] packetMessage = new byte[7 + data.length + optional.length];
        packetMessage[0] = 0x55; // synchronization byte

        /* create message header */
        packetMessage[1] = (byte) ( ( data.length & 0xFF00 ) >> 2 ); // data
                                                                     // length
                                                                     // part 1
        packetMessage[2] = (byte) ( data.length & 0xFF ); // data length part 2
        packetMessage[3] = (byte) ( optional.length & 0xFF ); // optional length
        packetMessage[4] = (byte) ( type & 0xFF ); // message type
        int checksum = 0;
        for( int i = 1; i < 5; i++ ) {
            checksum = CodingHelper.processCRC8( checksum, packetMessage[i] );
        }
        packetMessage[5] = (byte) ( checksum & 0xFF ); // header checksum

        /* set message payload */
        int i = 6;
        for( final int value : data ) {
            packetMessage[i++] = (byte) ( value & 0xFF );
        }
        for( final int value : optional ) {
            packetMessage[i++] = (byte) ( value & 0xFF );
        }
        packetMessage[packetMessage.length - 1] = (byte) ( CodingHelper.calculatePayloadChecksum( data, optional ) & 0xFF );

        return packetMessage;
    }

    private PacketEncoder() {
        // create instances of this class
    }
}
