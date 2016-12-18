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

package org.atennert.connector.reader;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;

import org.atennert.connector.IDistributor;
import org.atennert.connector.IEventListener;
import org.atennert.connector.packets.Packet;
import org.atennert.connector.reader.ComConnector.ConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the connection to the EnOcean transceiver. It uses RXTX to
 * read and send messages.<br>
 * <br>
 * While reading and writing is not active, it listens for updates of the serial
 * ports and forwards changes to registered port listeners. The received bytes
 * are changed to integers and put in a queue. The messages to send are taken
 * from a queue in form of {@link Packet} instances.
 *
 * @author Andreas Tennert
 */
public class ComConnector implements Runnable, IDistributor<ConnectionStatus>
{
    /**
     * Values for informing listeners about current connection status.
     */
    public enum ConnectionStatus
    {
        OPENED, CLOSED, OPEN_FAILED
    }

    private static final int READ_WRITE_MODE_WAIT_TIME = 500;

    private static CommPortIdentifier serialPortId;
    @SuppressWarnings("rawtypes")
    private static Enumeration enumComm;
    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Boolean serialPortOpen = false;
    private final int baudrate = 57600;
    private final int dataBits = SerialPort.DATABITS_8;
    private final int stopBits = SerialPort.STOPBITS_1;
    private final int parity = SerialPort.PARITY_NONE;
    private String portName = null;

    /** read/write mode is supposed to be active */
    private volatile boolean doRun;

    private static final Logger log = LoggerFactory.getLogger(ComConnector.class);

    private final List<IEventListener<ConnectionStatus>> statusListeners = new ArrayList<IEventListener<ConnectionStatus>>();
    private ConnectionStatus status;

    private final BlockingQueue<Integer> messageByteQueue;
    private final BlockingQueue<Packet> sendPacketQueue;

    private final PortUpdater portUpdater;

    /**
     * @param messageByteQueue queue for forwarding of message parts
     * @param sendPacketQueue queue for packets to send away
     */
    public ComConnector(BlockingQueue<Integer> messageByteQueue, BlockingQueue<Packet> sendPacketQueue)
    {
        this.messageByteQueue = messageByteQueue;
        this.sendPacketQueue = sendPacketQueue;

        // start port updater for continuous updates on serial port changes
        portUpdater = new PortUpdater();
        portUpdater.start();
        status = ConnectionStatus.CLOSED;
    }

    @Override
    public void addListener(IEventListener<ConnectionStatus> listener)
    {
        synchronized ( statusListeners )
        {
            statusListeners.add(listener);
            listener.onEvent(status);
        }
    }

    @Override
    public void removeListener(IEventListener<ConnectionStatus> listener)
    {
        synchronized ( statusListeners )
        {
            statusListeners.remove(listener);
        }
    }

    private void updateListeners(ConnectionStatus status)
    {
        synchronized ( statusListeners )
        {
            this.status = status;
            for ( final IEventListener<ConnectionStatus> listener : statusListeners )
            {
                listener.onEvent(status);
            }
        }
    }

    /**
     * Stop the read/write mode (ComConnector thread).
     */
    public void stopThread()
    {
        doRun = false;
    }

    /**
     * @return the port updater, that sends updates on changes of available
     *         serial ports
     */
    public IDistributor<List<String>> getPortUpdater()
    {
        return portUpdater;
    }

    /**
     * Thread loop for managing the read/write mode.
     */
    @Override
    public void run()
    {
        log.debug("Starting connector thread.");

        // stop serial port updates and activate read/write mode
        portUpdater.interrupt();

        messageByteQueue.clear();

        doRun = true;

        if ( openPort(portName) == true )
        {
            log.debug("Connector thread initialized.");
            updateListeners(ConnectionStatus.OPENED);

            // read/write mode is activated, wait for stop request
            while ( doRun )
            {
                try
                {
                    Thread.sleep(READ_WRITE_MODE_WAIT_TIME);
                }
                catch ( final InterruptedException e )
                {
                }
            }

            // stop read/write mode and start port change updates
            closePort();
            updateListeners(ConnectionStatus.CLOSED);
        }
        else
        {
            updateListeners(ConnectionStatus.OPEN_FAILED);
        }

        portUpdater.start();

        log.debug("Connector thread stopped.");
    }

    /**
     * @return all currently available serial ports
     */
    private List<String> getPortNames()
    {
        final List<String> portNames = new ArrayList<String>();

        enumComm = CommPortIdentifier.getPortIdentifiers();
        while ( enumComm.hasMoreElements() )
        {
            serialPortId = (CommPortIdentifier)enumComm.nextElement();
            portNames.add(serialPortId.getName());
        }

        return portNames;
    }

    /**
     * Set the serial port to which the EnOcean transceiver is connected.
     *
     * @param portName new serial port to use
     * @return <code>true</code> if the given port was valid and therefore set,
     *         <code>false</code> otherwise
     */
    public boolean setSerialPort(String portName)
    {
        if ( portName != null && !doRun && getPortNames().contains(portName) )
        {
            this.portName = portName;
            return true;
        }
        log.error("Unable to set port " + portName + "!");
        return false;
    }

    /**
     * Open the serial port connection.
     *
     * @param portName
     * @return serial port open
     */
    private boolean openPort(String portName)
    {
        if ( portName == null )
        {
            return false;
        }

        Boolean foundPort = false;
        if ( serialPortOpen != false )
        {
            log.error("Serial port already opened!");
            return false;
        }

        log.debug("Opening serial port.");
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while ( enumComm.hasMoreElements() )
        {
            serialPortId = (CommPortIdentifier)enumComm.nextElement();
            if ( portName.contentEquals(serialPortId.getName()) )
            {
                foundPort = true;
                break;
            }
        }
        if ( foundPort != true )
        {
            log.error("Could not find serial port: " + portName);
            return false;
        }
        try
        {
            serialPort = (SerialPort)serialPortId.open("Open and send", 100);

            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            ( new Thread(new SerialPortWriter()) ).start();

            serialPort.addEventListener(new SerialPortListener());

            serialPort.notifyOnDataAvailable(true);

            serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
        }
        catch ( final PortInUseException e )
        {
            log.error("Port is in use!");
        }
        catch ( final IOException e )
        {
            log.error("No access to InputStream!");
        }
        catch ( final TooManyListenersException e )
        {
            log.error("TooManyListenersException for serial port!");
        }
        catch ( final UnsupportedCommOperationException e )
        {
            log.error("Unable to set interface parameters!");
        }

        log.debug("Opened port " + portName + ".");

        serialPortOpen = true;
        return true;
    }

    /**
     * Close the serial port
     */
    private void closePort()
    {
        if ( serialPortOpen == true )
        {
            log.debug("Closing serial port.");
            try
            {
                inputStream.close();
            }
            catch ( final IOException e )
            {
                log.error(e.getMessage());
            }
            serialPort.close();
            serialPortOpen = false;
        }
        else
        {
            log.error("Serial port already closed.");
        }
    }

    /**
     * Read data from the serial port, change the read bytes to integers and put
     * them in the queue for received data.
     */
    private void readData()
    {
        try
        {
            final byte[] data = new byte[150];
            int num;
            while ( inputStream.available() > 0 )
            {
                num = inputStream.read(data, 0, data.length);
                for ( int i = 0; i < num; i++ )
                {
                    messageByteQueue.add(new Integer( ( data[i] & 0x7F ) + ( data[i] < 0 ? 128 : 0 )));
                }
            }
        }
        catch ( final IOException e )
        {
            log.error("Error while reading incoming data!");
        }
    }

    /**
     * This event listener gets notified about incoming data.
     */
    private class SerialPortListener implements SerialPortEventListener
    {
        @Override
        public void serialEvent(SerialPortEvent event)
        {
            if ( event.getEventType() == SerialPortEvent.DATA_AVAILABLE )
            {
                readData();
            }
        }
    }

    /**
     * This class takes packets from the send queue, transforms them to byte
     * messages and sends them away via the EnOcean transceiver.
     */
    private class SerialPortWriter implements Runnable
    {

        @Override
        public void run()
        {
            Packet packet;
            while ( doRun )
            {
                try
                {
                    Thread.sleep(READ_WRITE_MODE_WAIT_TIME);
                }
                catch ( final InterruptedException e )
                {
                }

                while ( ( packet = sendPacketQueue.poll() ) != null )
                {
                    try
                    {
                        outputStream.write(PacketEncoder.encodePacket(packet));
                    }
                    catch ( final IOException e )
                    {
                        log.error("Error while sending packet: " + packet);
                    }
                }
            }
        }
    }

    /**
     * This class repeatedly checks for the available serial ports. If one or
     * more ports changed it sends an update to all registered port listeners.
     */
    private class PortUpdater extends Thread implements IDistributor<List<String>>
    {

        /** list of currently available ports */
        private List<String> ports = new ArrayList<String>();

        /** list of port listeners */
        private final List<IEventListener<List<String>>> listeners = new ArrayList<IEventListener<List<String>>>();

        @Override
        public void addListener(IEventListener<List<String>> listener)
        {
            synchronized ( ports )
            {
                listeners.add(listener);
                listener.onEvent(ports);
            }
        }

        @Override
        public void removeListener(IEventListener<List<String>> listener)
        {
            synchronized ( ports )
            {
                listeners.remove(listener);
            }
        }

        /**
         * Send the a list of serial ports to all registered listeners.
         *
         * @param ports new list of serial ports
         */
        private void distributeEvent(List<String> ports)
        {
            for ( final IEventListener<List<String>> l : listeners )
            {
                l.onEvent(ports);
            }
        }

        @Override
        public void run()
        {
            log.debug("PortUpdater thread started.");

            while ( !isInterrupted() )
            {
                synchronized ( ports )
                {
                    final List<String> ports = getPortNames();
                    if ( ports.size() != this.ports.size() || !this.ports.containsAll(ports) )
                    {
                        this.ports = ports;
                        distributeEvent(this.ports);
                    }
                }

                try
                {
                    // update list every second
                    sleep(1000);
                }
                catch ( final InterruptedException e )
                {
                    interrupt();
                }
            }

            log.debug("PortUpdater thread stopped.");
        }
    }
}
