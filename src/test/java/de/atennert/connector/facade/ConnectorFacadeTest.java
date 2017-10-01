package de.atennert.connector.facade;

import de.atennert.connector.IEnOceanConnector;

import de.atennert.connector.distribution.PacketDistributor;
import de.atennert.connector.packets.Packet;
import de.atennert.connector.packets.PacketFactory;
import de.atennert.connector.reader.ComConnector;
import de.atennert.connector.reader.PacketDecoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.BlockingQueue;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorFacadeTest {

    private IEnOceanConnector connector;

    @Mock
    private PacketFactory packetFactory;

    @Mock
    private PacketDistributor packetDistributor;

    @Mock
    private ComConnector comConnector;

    @Mock
    private PacketDecoder packetDecoder;

    @Mock
    private BlockingQueue<Packet> sendPacketQueue;

    @Before
    public void setup() {
        connector = new ConnectorFacade( packetFactory, packetDistributor, comConnector, packetDecoder,
                sendPacketQueue );
    }

    @Test
    public void connectorExists() {
        Assert.assertNotNull( connector );
    }
}
