package de.atennert.connector;

import de.atennert.connector.distribution.IEventListener;
import de.atennert.connector.distribution.IPacketListener;
import de.atennert.connector.packets.IPacketFactory;
import de.atennert.connector.packets.Packet;
import de.atennert.connector.reader.ComConnector;

import java.util.List;

/**
 * Interface for accessing the functions of the EnOceanConnector.
 */
public interface IEnOceanConnector
{
    /**
     * Start to read EnOcean messages and distribute them to packet listeners
     * and start the sending of messages.
     */
    void startDataAcquisition();

    /**
     * Stop the reading and sending of EnOcean messages.
     */
    void stopDataAcquisition();

    /**
     * Add a packet listener to the distributor. This listener will receive
     * EnOcean data packets.
     *
     * @param packetListener the packet listener to add
     */
    void addPacketListener( IPacketListener packetListener );


    /**
     * Remove a packet listener from the distributor.
     *
     * @param packetListener the packet listener to remove
     */
    void removePacketListener( IPacketListener packetListener );

    /**
     * Add a port listener to the connector. This listener will repeatedly
     * receive updates for available serial ports (names in form of strings)
     * whenever the data acquisition is <em>NOT</em> active.
     *
     * @param portListener The port listener to add
     */
    void addPortListener( IEventListener< List< String >> portListener );

    /**
     * Remove a port listener from the connector.
     *
     * @param portListener the port listener to remove
     */
    void removePortListener( IEventListener< List< String >> portListener );

    /**
     * Add a connection listener to the connector. This listener will receive
     * updates for status changes from the connector.
     *
     * @param connectionListener the connectionListener listener to add
     */
    void addConnectionListener( IEventListener<ComConnector.ConnectionStatus> connectionListener );

    /**
     * Remove a connectionListener listener from the connector.
     *
     * @param connectionListener the connectionListener listener to remove
     */
    void removeConnectionListener( IEventListener<ComConnector.ConnectionStatus> connectionListener );

    /**
     * Set a serial port to use, which is the one where the EnOcean transceiver
     * is connected. The port must be set <em>before</em> starting the data
     * acquisition.
     *
     * @param port The port of the transceiver
     */
    void setPort( String port );

    /**
     * Add a packet factory that extends the main packet factory. Those
     * factories can be used to create more useful instances of {@link Packet}
     * for custom types of data packets. Specific types for all EnOcean defined
     * messages are covered by the main factory.
     *
     * @param factory the factory to add to the main factory.
     */
    void addPacketFactory( IPacketFactory factory );

    /**
     * Remove a packet factory from the main packet factory.
     *
     * @param factory the factory to remove
     */
    void removePacketFactory( IPacketFactory factory );

    /**
     * Send a data packet to the EnOcean network.
     * @param packet The data packet
     */
    void sendDataPacket( Packet packet);
}
