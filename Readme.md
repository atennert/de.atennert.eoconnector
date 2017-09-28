# EnOceanConnector

## Status
__master__: [![Build Status](https://travis-ci.org/atennert/de.atennert.eoconnector.svg?branch=master)](https://travis-ci.org/atennert/de.atennert.eoconnector) 
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

## Copyright

The following copyright license is effective for all resources of the EnOceanConnector software, unless there is a different copyright information in or for the resource. If a resource has it's own copyright information, then the resources license statement replaces the license statement below, for this particular resource.

Copyright 2016 Andreas Tennert

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Release notes

**Version 1.0.0**

* added support for upload to jcenter in build.gradle

**Version 1.0.0**

* switched from Maven to Gradle
* changed project path structure
* updated package name "de.atennert..."

**Version 0.2.0**

* changed compiler compliance to 1.7
* tons of comments :)
* removed PluginClassLoader
* removed JarFilenameFilter
* removed ConfigFilenameFilter
* added IInstanceFilter
* added IDistibutor
* added ConnectionListenerModel
* changed IListener to IEventListener
* changed ModelListener to PacketListenerObservable
* refactored Main
* refactored PluginLoader
* refactored PacketListenerObservable
* refactored PacketListenerModel
* refactored EventPacket
* refactored CommonCommandPacket
* refactored RadioPacket
* refactored RemoteManCommandPacket
* refactored RadioSubTelPacket
* refactored SmartAckCommandPacket
* refactored ComConnector
* added break to DistributionHandler in PacketDistributor
* FacadeSM changed to final and instantiation prohibited
* changed general constructor of ResponsePacket
* added constructor to ResponsePacket
* changed getReturnCode to getRepsonseCode in ResponsePacket
* added missing constant CO_WR_MODE to CommonCommandPacket
* fixed error with filling optional data in RadioSubTelPacket
* added constructor to RadioSubTelPacket
* removed message logging from ComConnector
* moved port listener management from ComConnector to PortUpdater
* added status distribution to ComConnector, with extension in ConnectorFacade and FacadeSM

**Version 0.1.0**

Feature complete base version
