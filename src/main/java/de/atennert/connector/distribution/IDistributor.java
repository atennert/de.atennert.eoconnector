package de.atennert.connector.distribution;

/**
 * General interface for all event and state distributors.
 *
 * @author Andreas Tennert
 * @param <T> type of elements to distribute
 */
public interface IDistributor< T > {
    /**
     * Add a new listener to the distributor.
     *
     * @param listener new listener for mediator updates
     */
    void addListener( IEventListener< T > listener );

    /**
     * Remove a listener from the distributor.
     *
     * @param listener the listener to remove
     */
    void removeListener( IEventListener< T > listener );
}
