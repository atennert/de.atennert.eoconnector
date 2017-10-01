package de.atennert.connector.distribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class contains the available packet listeners and their properties if
 * there are some.
 * 
 * @author Andreas Tennert
 */
public class PacketListenerModel {

    /** Map with plug-ins. */
    private final Map< String, IPacketListener > packetListeners = new HashMap<>();

    /** Map with plug-in properties. */
    private final Map< String, Properties > properties = new HashMap<>();

    /**
     * Observable for packet listener actions, in this case it is adding and
     * removing them.
     */
    private final PacketListenerObservable listenerObservable;

    public PacketListenerModel( PacketListenerObservable observable ) {
        this.listenerObservable = observable;
    }

    /**
     * Adds a listener for packets.
     * 
     * @param name name (ID) of the packet listener
     * @param listener the packet listener
     * @param properties the properties of the packet listener
     */
    public synchronized void addListener( String name,
            IPacketListener listener, Properties properties ) {
        this.packetListeners.put( name, listener );
        this.properties.put( name, properties );

        listenerObservable.listenerEvent( name,
                PacketListenerObservable.ListenerActions.ADD );
    }

    /**
     * @param name name (ID) of the packet listener to return
     * @return the packet listener for the given name, or <code>null</code> if
     *         there is none
     */
    public synchronized IPacketListener getListener( String name ) {
        return packetListeners.get( name );
    }

    /**
     * @param name name (ID) of the packet listener of the properties to return
     * @return the packet listener properties for the given name, or
     *         <code>null</code> if there are none
     */
    public synchronized Properties getProperties( String name ) {
        return properties.get( name );
    }

    /**
     * Remove a packet listener and its properties.
     * 
     * @param name name (ID) of the packet listener to remove
     */
    public synchronized void removeListener( String name ) {
        this.packetListeners.remove( name );
        this.properties.remove( name );

        listenerObservable.listenerEvent( name,
                PacketListenerObservable.ListenerActions.REMOVE );
    }

    /**
     * @return names (IDs) of all available listeners.
     */
    public synchronized Set< String > getListenerNames() {
        return packetListeners.keySet();
    }

}
