package com.gluonhq.dl.mnist.server;

import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

@Path("handler")
public class Handler {

    @Inject
    private ModelService service;
    private static final Logger LOGGER = Logger.getLogger(Handler.class.getName());
  
    @GET
    @Path("model")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] retrieveModel() throws IOException {
        return service.getModel();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("classifyImage")
    public String classifyImage(@HeaderParam("authorization") String authorizationHeader, byte[] rawData) {
        try {
            String answer = service.predict(rawData);
            return answer;
        } catch (Exception e) {
            System.out.println("oops ");
            e.printStackTrace();
        }
        return null;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("correctImage/{label}")
    public String correctImage(byte[] rawData, @PathParam("label") String label) {
        String answer = service.correctImage(rawData, label);
        return answer;
    }
    
    @POST
    @Path("publishGradient")
    public void publishGradient(byte[] clientGradient) throws IOException {
        System.out.println("publish gradient");
        service.publishGradient(clientGradient);
    }

}
