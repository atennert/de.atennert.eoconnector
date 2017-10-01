package de.atennert.connector.facade;

import de.atennert.connector.distribution.IDistributor;
import de.atennert.connector.distribution.IEventListener;
import de.atennert.connector.reader.ComConnector.ConnectionStatus;

/**
 * This model class is used to add or remove a connection listener.
 */
class ConnectionListenerModel extends AbstractTransitionModel {

    final static int ADD = 0;
    final static int REMOVE = 1;

    final int action;
    final IDistributor< ConnectionStatus > distributor;
    final IEventListener< ConnectionStatus > listener;

    ConnectionListenerModel( ConnectorFacade facade, IDistributor< ConnectionStatus > distributor,
            IEventListener< ConnectionStatus > listener, int action ) {
        super( facade );
        this.action = action;
        this.distributor = distributor;
        this.listener = listener;
    }

}
