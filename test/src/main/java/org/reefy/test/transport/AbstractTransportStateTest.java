package org.reefy.test.transport;

import org.junit.Test;
import org.reefy.transportrest.api.AppServerHandler;
import org.reefy.transportrest.api.Key;
import org.reefy.transportrest.api.RawKey;
import org.reefy.transportrest.api.RawValue;
import org.reefy.transportrest.api.Value;
import org.reefy.transportrest.api.transport.Contact;
import org.reefy.transportrest.api.transport.TransportClient;
import org.reefy.transportrest.api.transport.TransportServer;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Paul Kernfeld - pk@knewton.com
 */
public abstract class AbstractTransportStateTest<C extends Contact> {
    private final TransportFactory<C> transportFactory;

    private final Key testKey = RawKey.pseudorandom();
    private final Value testValue = RawValue.pseudorandom(5);

    protected AbstractTransportStateTest(TransportFactory<C> transportFactory) {
        this.transportFactory = transportFactory;
    }

    @Test(expected=IllegalStateException.class)
    public void testClientNotStartedPut() {
        final TransportClient<C> client = transportFactory.buildClient();

        client.put(transportFactory.buildMockContact(), testKey, testValue,
                   mock(TransportClient.PutCallback.class));
    }

    @Test(expected=IllegalStateException.class)
    public void testClientNotStartedGet() {
        final TransportClient<C> client = transportFactory.buildClient();

        client.get(transportFactory.buildMockContact(), testKey,
                   mock(TransportClient.GetCallback.class));
    }

    @Test
    public void testClientStarts() throws ExecutionException, InterruptedException {
        final TransportClient<C> client = transportFactory.buildClient();

        assertFalse(client.isRunning());
        client.start().get();
        assertTrue(client.isRunning());
        client.stop().get();
        assertFalse(client.isRunning());
    }

    @Test
    public void testServerStarts() throws ExecutionException, InterruptedException {
        @SuppressWarnings("unchecked")
        final AppServerHandler<C> mock = mock(AppServerHandler.class);
        final TransportServer<C> server = transportFactory.buildServer(mock).getServer();

        assertFalse(server.isRunning());
        server.start().get();
        assertTrue(server.isRunning());
        server.stop().get();
        assertFalse(server.isRunning());
    }

    // TODO: test server states too
}
