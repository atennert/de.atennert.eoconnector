package de.atennert.connector.facade;

import de.atennert.connector.facade.FacadeSM.State;

/**
 * The AbstractTransitionModel holds data for transitions of the facade state
 * machine.
 * 
 * @author Andreas Tennert
 */
abstract class AbstractTransitionModel {
    private final ConnectorFacade facade;

    protected AbstractTransitionModel( ConnectorFacade facade ) {
        this.facade = facade;
    }

    /**
     * Set the next state in the state machine.
     * 
     * @param newState the new state
     */
    protected void setState( State newState ) {
        facade.setState( newState );
    }
}
