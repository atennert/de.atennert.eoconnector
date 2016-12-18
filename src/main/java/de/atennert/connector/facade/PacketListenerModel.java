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

import java.util.Properties;

import org.atennert.connector.distribution.IPacketListener;
import org.atennert.connector.distribution.PacketDistributor;

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
    final Properties properties;

    PacketListenerModel( ConnectorFacade facade, PacketDistributor distributor, String id, IPacketListener listener,
            Properties properties, int action ) {
        super( facade );
        this.action = action;
        this.distributor = distributor;
        this.id = id;
        this.listener = listener;
        this.properties = properties;
    }

}
