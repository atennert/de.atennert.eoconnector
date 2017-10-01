package de.atennert.connector;

import de.atennert.connector.ConnectorFactory;
import de.atennert.connector.IEnOceanConnector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FacadeCreatorTest
{
    @Test
    public void createConnectorInstance() {
        IEnOceanConnector connector = ConnectorFactory.createConnector();

        Assert.assertNotNull(connector);
    }

    @Test
    public void createOnlyOneConnectorInstance() {
        IEnOceanConnector connector1 = ConnectorFactory.createConnector(),
                connector2 = ConnectorFactory.createConnector();

        Assert.assertSame( connector1, connector2 );
    }
}
