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

import java.util.List;

import org.atennert.connector.IDistributor;
import org.atennert.connector.IEventListener;

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
