package de.atennert.connector.facade;

import de.atennert.connector.packets.IPacketFactory;
import de.atennert.connector.packets.Packet;
import de.atennert.connector.packets.PacketFactory;

/**
 * This model class is used to add or remove packet factories. Packet factories
 * are used to create {@link Packet} instances from received EnOcean message raw
 * data.
 * 
 * @author Andreas Tennert
 */
class PacketFactoryModel extends AbstractTransitionModel {

    final static int ADD = 0;
    final static int REMOVE = 1;

    final int action;
    final IPacketFactory factory;
    final PacketFactory mainFactory;

    PacketFactoryModel( ConnectorFacade facade, PacketFactory mainFactory, IPacketFactory factory, int action ) {
        super( facade );
        this.factory = factory;
        this.action = action;
        this.mainFactory = mainFactory;
    }

}
