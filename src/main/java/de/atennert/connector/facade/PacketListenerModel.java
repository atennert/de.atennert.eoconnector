package de.atennert.connector.facade;

import java.util.Properties;

import de.atennert.connector.distribution.IPacketListener;
import de.atennert.connector.distribution.PacketDistributor;

/**
 * This model class is used to add or remove packet listeners.
 * 
 * @author Andreas Tennert
 */
class PacketListenerModel extends AbstractTransitionModel {

    final static int ADD = 0;
    final static int REMOVE = 1;

    final int action;
    final PacketDistributor distributor;
    final String id;
    final IPacketListener listener;

    PacketListenerModel( ConnectorFacade facade, PacketDistributor distributor, String id, IPacketListener listener,
            int action ) {
        super( facade );
        this.action = action;
        this.distributor = distributor;
        this.id = id;
        this.listener = listener;
    }

}
