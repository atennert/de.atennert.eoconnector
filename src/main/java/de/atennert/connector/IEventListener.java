package de.atennert.connector;

/**
 * General interface for event listeners.
 * 
 * @param <T> Type of the event
 * @author Andreas Tennert
 */
public interface IEventListener< T > {
    /**
     * Always called when an event occurs.
     * 
     * @param event The event, that occurred
     */
    void onEvent( T event );
}
