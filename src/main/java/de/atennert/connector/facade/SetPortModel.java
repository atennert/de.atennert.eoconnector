package de.atennert.connector.facade;

import de.atennert.connector.reader.ComConnector;

/**
 * This model is used to set the port to which the EnOcean transceiver is
 * connected.
 * 
 * @author Andreas Tennert
 */
class SetPortModel extends AbstractTransitionModel {

    final String port;
    final ComConnector connector;

    SetPortModel( ConnectorFacade facade, ComConnector connector, String port ) {
        super( facade );
        this.port = port;
        this.connector = connector;
    }

}
