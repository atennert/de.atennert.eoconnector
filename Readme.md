# EnOceanConnector

## Status
__master__:<br>
[![Build Status](https://travis-ci.org/atennert/de.atennert.eoconnector.svg?branch=master)](https://travis-ci.org/atennert/de.atennert.eoconnector)
[![Release](https://img.shields.io/github/release/atennert/de.atennert.eoconnector.svg)](https://github.com/atennert/de.atennert.eoconnector/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

__devel__:<br>
[![Build Status](https://travis-ci.org/atennert/de.atennert.eoconnector.svg?branch=devel)](https://travis-ci.org/atennert/de.atennert.eoconnector)

## Overview

The EnOceanConnector is a library for Java projects in which EnOcean messages have to be evaluated in order to communicate with components of an EnOcean-based automation system (http://www.enocean.com). The EnOceanConnector supports the EnOcean Serial Protocol 3 (ESP3). Compatible hardware, which can be used to have access to an EnOcean network, is for instance the USB 300 gateway USB stick. It can be purchased by itself or for instance together with the starter kit ESK 300.

The EnOceanConnector converts received data packets using so called factories in packet objects, that can be further evaluated as needed. The packet objects differ according to the message type and are distributed to listeners by a distributor instance. Packets can be sent to the EnOcean network. The receiving an sending of messages is handled by the connector.

## Use of the library

To use the functions of the EnOceanConnector library, an instance of `IEnOceanConnector` needs to be created. The instance can be retrieved by calling `ConnectorFactory.createConnector()`. This will initialize all EnOceanConnector resources. The returned class provides the functionality to use those resources. The methods are as follows:

```java
// add and remove listeners for incoming messages (required for receiving messages)
public void addPacketListener( IPacketListener packetListener )
public void removePacketListener( IPacketListener packetListener )

// add and remove listeners for the detected serial device ports (not necessary if the port is known)
public void addPortListener( IEventListener< List< String >> portListener )
public void removePortListener( IEventListener< List< String >> portListener )

// set the device port of the connected ESP3 gateway
public void setPort( String port )

// add and remove listeners for the connection status (optional, good for diagnosis in error cases)
public void addConnectionListener( IEventListener< ConnectionStatus > connectionListener )
public void removeConnectionListener( IEventListener< ConnectionStatus > connectionListener )

// add and remove additional packet factories (optional, might be useful in case of non-standard messages)
public void addPacketFactory( IPacketFactory factory )
public void removePacketFactory( IPacketFactory factory )

// open the connection for receiving and sending (required)
public void startDataAcquisition()

// close the connection (required, ends the used background threads)
public void stopDataAcquisition()

// used to send packets to the EnOcean network
public void sendDataPacket( Packet packet )
```
First of all, instances of `IPacketListener` can be added and removed. Instances of `IPacketListener` will receive the received data from the EnOcean network. They have to provide the message type, which they want to receive. `IPacketConstants.TYPE_ANY` can be used, to get all messages.

The port listeners listen to changes of available serial ports, that are handed over as a list of strings. That way the application gets an update for instance when the EnOcean transceiver gets connected at program runtime. There are only port updates while the sending and receiving of data is deactivated.

The serial port to which the transceiver is connected has to be set with the method `setPort`. The String to provide should be one of the list, that is submitted to port listeners.

An application can get the status of the connection for receiving and sending data by using a connection listener. The status can have the following values:

* Closed
* Opened
* Open failed

The status closed means the connection to the transceiver is inactive. No data will be sent or received. In the status opened there is an active connection and EnOcean messages are exchanged between EnOceanConnector and tranceiver. The EnOceanConnector enteres the status failed when the activation of the connection failed. As with the status closed, there is no connection to the transceiver.

The default packet factory can be extended by adding additional factories. The packet factory transforms the received bytes to instances of `Packet`.

With `startDataAcquisition` the processing of EnOcean messages can be activated. `stopDataAcquisition` ends the message processing.

## Packets

`Packet`s are representations of the EnOcean packet data, that is sent through the network. The library contains `Packet` implementations for all message packets from the ESP3 specification:

* CommonCommandPacket
* EventPacket
* RadioPacket
* RadioAdvancedPacket
* RadioSubTelPacket
* RemoteManCommandPacket
* ResponsePacket
* SmartAckCommandPacket

Every `Packet` contains the type (`type::int`), a timestamp (`timestamp::Date`), a field that says whether the data is valid or not (`isValid::boolean`) and the mandatory data (`getData()::byte[]`). It might contain optional data (`getOptional()::byte[]`). The different `Packet` implementations offer more methods to access specific parts of the data by there specified designation, for instance `getDestinationID()` from the `RadioPacket`.
