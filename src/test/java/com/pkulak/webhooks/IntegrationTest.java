package com.pkulak.webhooks;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class IntegrationTest {
    private HttpServer server;
    private Client client = JerseyClientBuilder.createClient();

    @BeforeClass
    public void startServer() {
        server = Main.startServer();
    }

    @AfterClass
    public void stopServer() {
        server.shutdownNow();
    }

    protected WebTarget target() {
        return client.target(Main.BASE_URI);
    }
}
