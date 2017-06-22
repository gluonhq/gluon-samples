package com.gluonhq.javaee.motd.server;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/jaxrs")
public class MotdApplication extends ResourceConfig {

    public MotdApplication() {
        // scan com.gluonhq package for jax-rs classes
        packages(true, "com.gluonhq");

        // everything inside /webjars should not be handled by the jax-rs application
        property(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "/webjars/.*");

        // use freemarker for web templating
        register(FreemarkerMvcFeature.class);
        property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "freemarker");
    }
}
