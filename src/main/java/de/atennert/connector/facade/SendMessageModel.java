package de.atennert.connector.facade;

import de.atennert.connector.packets.Packet;

import java.util.concurrent.BlockingQueue;

/**
 * This model is used to send data to the EnOcean network.
 */
class SendMessageModel extends AbstractTransitionModel {

    final BlockingQueue<Packet> sendMessageQueue;
    final Packet packet;

    SendMessageModel( final ConnectorFacade facade,
                                final BlockingQueue<Packet> sendMessageQueue,
                                final Packet packet ) {
        super( facade );
        this.sendMessageQueue = sendMessageQueue;
        this.packet = packet;
    }
}
