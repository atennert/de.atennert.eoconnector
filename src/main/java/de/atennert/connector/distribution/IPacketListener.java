package de.atennert.connector.distribution;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import de.atennert.connector.packets.IPacketConstants;
import de.atennert.connector.packets.Packet;

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
    void receivePacket( Packet packet );

    /**
     * @return the supported packet types of the listener or
     *         {@link IPacketConstants#TYPE_ANY} if the listener supports all
     *         packet types.
     */
    int[] getSupportedPackets();
}
