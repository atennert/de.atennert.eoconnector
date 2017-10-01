package de.atennert.connector.facade;

import java.util.List;

import de.atennert.connector.distribution.IDistributor;
import de.atennert.connector.distribution.IEventListener;

/**
 * This model class is used to add or remove a serial port listener.
 * 
 * @author Andreas Tennert
 */
class PortListenerModel extends AbstractTransitionModel {

    final static int ADD = 0;
    final static int REMOVE = 1;

    final int action;
    final IDistributor< List< String > > distributor;
    final IEventListener< List< String >> listener;

    PortListenerModel( ConnectorFacade facade, IDistributor< List< String > > distributor,
            IEventListener< List< String >> listener, int action ) {
        super( facade );
        this.action = action;
        this.distributor = distributor;
        this.listener = listener;
    }

}
