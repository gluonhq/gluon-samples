package com.gluonhq.javaee.motd.server.handler;

import com.gluonhq.javaee.motd.server.service.GluonService;
import org.glassfish.jersey.server.mvc.Template;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("front")
public class FrontHandler {

    @Inject
    private GluonService service;

    @GET
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    @Template(name = "/index.ftl")
    public String get() {
        return service.getMotd();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN + "; charset=UTF-8")
    public Response set(@FormParam("motd") String motd) {
        service.setMotd(motd);
        return Response.ok(motd).build();
    }
}
