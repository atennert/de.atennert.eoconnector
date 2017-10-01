package de.atennert.connector;

import de.atennert.connector.distribution.PacketDistributor;
import de.atennert.connector.facade.ConnectorFacade;
import de.atennert.connector.packets.Packet;
import de.atennert.connector.packets.PacketFactory;
import de.atennert.connector.reader.ComConnector;
import de.atennert.connector.reader.PacketDecoder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Factory for creating an IEnOceanConnector instance, through which the
 * EnOceanConnector library should be accessed.
 */
public final class ConnectorFactory {

    private static IEnOceanConnector connectorInstance = null;

    public static synchronized IEnOceanConnector createConnector() {
        if (connectorInstance == null) {
            final BlockingQueue<Integer> receiveByteQueue = new LinkedBlockingQueue<>();
            final BlockingQueue<Packet> sendPacketQueue = new LinkedBlockingQueue<>();
            final PacketFactory packetFactory = new PacketFactory();
            final PacketDistributor packetDistributor = new PacketDistributor();

            connectorInstance = new ConnectorFacade( packetFactory,
                    packetDistributor,
                    new ComConnector( receiveByteQueue, sendPacketQueue ),
                    new PacketDecoder( receiveByteQueue, packetDistributor, packetFactory ),
                    sendPacketQueue);
        }
        return connectorInstance;
    }

    // Don't allow creating an instance
    private ConnectorFactory() {}
}
