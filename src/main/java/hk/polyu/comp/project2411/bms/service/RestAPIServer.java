package hk.polyu.comp.project2411.bms.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class RestAPIServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(2411);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Register the Jersey ServletContainer
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitOrder(0);

        // Configure Jersey to scan the specified package for resources
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "hk.polyu.comp.project2411.bms..service");

        server.start();
        System.out.println("REST API server started.");
        server.join();
    }
}
