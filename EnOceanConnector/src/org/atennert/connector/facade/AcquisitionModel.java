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

package org.atennert.connector.facade;

import org.atennert.connector.distribution.PacketDistributor;
import org.atennert.connector.reader.ComConnector;
import org.atennert.connector.reader.PacketDecoder;

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
