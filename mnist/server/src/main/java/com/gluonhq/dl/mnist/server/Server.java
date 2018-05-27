package com.gluonhq.dl.mnist.server;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("server")
public class Server extends ResourceConfig {
    
    public Server() {
        packages(true, "com.gluonhq.dl.minst.server");
    }
    
}
