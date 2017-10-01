# EnOceanConnector

## Status
__master__: [![Build Status](https://travis-ci.org/atennert/de.atennert.eoconnector.svg?branch=master)](https://travis-ci.org/atennert/de.atennert.eoconnector)
[![Release](https://img.shields.io/github/release/atennert/de.atennert.eoconnector.svg)](https://github.com/atennert/de.atennert.eoconnector/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

__devel__: [![Build Status](https://travis-ci.org/atennert/de.atennert.eoconnector.svg?branch=devel)](https://travis-ci.org/atennert/de.atennert.eoconnector)

## Overview

The EnOceanConnector is a library as well as an application framework for Java projects in which EnOcean messages have to be evaluated in order to communicate with components of an EnOcean-based automation system (http://www.enocean.com). The EnOceanConnector supports the EnOcean Serial Protocol 3 (ESP3). Compatible hardware, which can be used to have access to an EnOcean network, is for instance the USB 300 gateway USB stick. It can be purchased by itself or for instance together with the starter kit ESK 300.

The EnOceanConnector converts received data packets using so called factories in packet objects, that can be further evaluated as needed. The packet objects differ according to the message type and are distributed to listeners by a distributor instance. Listeners can be plugins, that represent sub applications and are selectable after the start of the EnOceanConnector application. It is also possible to use the EnOceanConnector as a library in an own application. Packets can also be sent via a queue from applications as well as from the plugins. The receiving an sending of messages is handled by the connector. The following image shows the general communication scheme.

## Use of the library

Instead of creating sub application and factory plugins, it is possible to use the EnOceanConnector as a library. In this case, all necessary functions are available via the class `ConnectorFacade`. This class initializes all EnOceanConnector resources during its instantiation and offers methods to provide access to them. The methods are as follows:

```java
public void addPacketListener( IPacketListener packetListener, Properties properties )
public void addPacketListener( String id, IPacketListener packetListener, Properties properties )
public void removePacketListener( IPacketListener packetListener )
public void addPortListener( IEventListener< List< String >> portListener )
public void removePortListener( IEventListener< List< String >> portListener )
public void setPort( String port )
public void addConnectionListener( IEventListener< ConnectionStatus > connectionListener )
public void removeConnectionListener( IEventListener< ConnectionStatus > connectionListener )
public void addPacketFactory( IPacketFactory factory )
public void removePacketFactory( IPacketFactory factory )
public void startAcquisition()
public void stopAcquisition()
```
In contrast to the framework use case there are a few more possibilities. First of all, instances of `IPacketListener` can be added and removed. If no ID is specified when such a listener is added, the EnOceanConnector will fall back to using its toString-Method. Packet factories can be used here as well. They can be added and removed via the corresponding methods.

The port listeners belong to the set of new listeners. They listen to changes of available serial ports, that are handed over as a list of strings. That way the application gets an update for instance when the EnOcean transceiver gets connected at program runtime. There are only updates while the sending and receiving of data is deactivated.

The serial port to which the transceiver is connected has to be set with the method `setPort`.

An application can get the status of the connection for receiving and sending data by using a connection listener. The status can have the following values:

* Closed
* Opened
* Open failed

The status closed means the connection to the transceiver is inactive. No data will be sent or received. In the status opened there is an active connection and EnOcean messages are exchanged between EnOceanConnector and tranceiver. The EnOceanConnector enteres the status failed when the activation of the connection failed. As with the status closed, there is no connection to the transceiver.

With `startAcquisition` the processing of EnOcean messages can be activated. `stopAcquisition` ends the message processing.
