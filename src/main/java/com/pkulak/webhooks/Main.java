package com.pkulak.webhooks;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8888/";

    public static HttpServer startServer() {
        ResourceConfig rc = new ResourceConfig().packages("com.pkulak.webhooks");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = startServer();

        System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl", BASE_URI));

        try {
            Thread.currentThread().join();
        } catch (Throwable t) {
            System.err.println(t);
        } finally {
            server.shutdownNow();
        }
    }
}
