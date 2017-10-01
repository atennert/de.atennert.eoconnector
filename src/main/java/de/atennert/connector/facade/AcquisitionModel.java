package de.atennert.connector.facade;

import de.atennert.connector.distribution.PacketDistributor;
import de.atennert.connector.reader.ComConnector;
import de.atennert.connector.reader.PacketDecoder;

/**
 * This model is used to trigger the start and the stop of data acquisition.
 * 
 * @author Andreas Tennert
 */
class AcquisitionModel extends AbstractTransitionModel {

    /** start data acquisition */
    final static int START = 0;
    /** stop data acquisition */
    final static int STOP = 1;

    /** start or stop data acquisition */
    final int action;

    final ComConnector connector;
    final PacketDecoder consumer;
    final PacketDistributor distributor;

    /**
     * @param facade the application facade
     * @param connector the connector to interact with the EnOcean transceiver
     * @param consumer the message byte code consumer
     * @param distributor the packet distributor
     * @param action {@link AcquisitionModel#START} or
     *            {@link AcquisitionModel#STOP}
     */
    AcquisitionModel( ConnectorFacade facade, ComConnector connector, PacketDecoder consumer,
            PacketDistributor distributor, int action ) {
        super( facade );
        this.action = action;
        this.connector = connector;
        this.consumer = consumer;
        this.distributor = distributor;
    }

}
