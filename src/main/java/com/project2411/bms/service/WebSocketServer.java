package com.project2411.bms.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;

public class WebSocketServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(2411);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Initialize the WebSocket container
        JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.addEndpoint(BMSServer.class);
        });

        server.start();
        System.out.println("WebSocket server started");
        server.join();
    }
}