/**
 * Copyright (c) 2016, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.samples.cloudlink.connector.rest.handler;

import com.gluonhq.samples.cloudlink.connector.rest.model.Note;
import com.gluonhq.samples.cloudlink.connector.rest.service.NoteService;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The REST handler that is able to handle incoming REST HTTP requests coming from Gluon CloudLink pertaining lists.
 *
 * The full path in the application is for example: http://HOSTNAME:PORT/CONTEXTROOT/rest/list/abcd
 */
@Path("/list/{listIdentifier}")
public class ListHandler {

    @EJB
    private NoteService noteService;

    private static final Logger LOG = Logger.getLogger(ListHandler.class.getName());

    private static final String CHARSET = "charset=UTF-8";

    /**
     * Called by Gluon CloudLink when a list with the specified identifier is retrieved for the first time from the
     * client application, but does noet yet exist in Gluon CloudLink. This implementation will return all the Notes
     * from the database to Gluon CloudLink as a JSON Array in the correct format.
     *
     * The format that Gluon CloudLink expects, is a JSON Array where each element is a JSON Object with the following
     * two keys:
     * <ul>
     *     <li><code>id</code>: the identifier of the Note</li>
     *     <li><code>payload</code>: the JSON payload of the Note object, as a String</li>
     * </ul>
     *
     * @param listIdentifier the identifier of the list that is being retrieved from Gluon CloudLink
     * @return a string representation of the constructed JSON Array
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    public String getList(@PathParam("listIdentifier") String listIdentifier) {
        LOG.log(Level.INFO, "Return list " + listIdentifier);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        List<Note> notes = noteService.findAll();
        notes.stream().map(note -> Json.createObjectBuilder()
                .add("id", note.getId())
                .add("payload", Json.createObjectBuilder()
                    .add("title", note.getTitle())
                    .add("text", note.getText())
                    .add("creationDate", note.getCreationDate())
                    .build().toString())
                .build())
            .forEach(jsonArrayBuilder::add);
        return jsonArrayBuilder.build().toString();
    }

    /**
     * Called from Gluon CloudLink when a new object is added to a list. This implementation will add the object in the
     * database.
     *
     * @param listIdentifier the identifier of the list where the object is added to
     * @param objectIdentifier the identifier of the object that is added
     * @param payload the raw JSON payload of the object that is added
     * @return an empty response, as the response is ignored by Gluon CloudLink
     */
    @POST
    @Path("/add/{objectIdentifier}")
    @Consumes(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    public String addItem(@PathParam("listIdentifier") String listIdentifier,
                          @PathParam("objectIdentifier") String objectIdentifier,
                          String payload) {
        LOG.log(Level.INFO, "Added item with id " + objectIdentifier + " to list " + listIdentifier + ": " + payload);
        try (JsonReader jsonReader = Json.createReader(new StringReader(payload))) {
            JsonObject jsonObject = jsonReader.readObject();
            String title = jsonObject.containsKey("title") ? jsonObject.getString("title") : "";
            String text = jsonObject.containsKey("text") ? jsonObject.getString("text") : "";
            long creationDate = jsonObject.containsKey("creationDate") ? jsonObject.getJsonNumber("creationDate").longValue() : LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            noteService.create(objectIdentifier, title, text, creationDate);
            jsonObject.getString("title");
        }
        return "{}";
    }

    /**
     * Called from Gluon CloudLink when an existing object is updated in a list. This implementation will update the
     * object in the database.
     *
     * @param listIdentifier the identifier of the list where the object is updated in
     * @param objectIdentifier the identifier of the object that is updated
     * @param payload the raw JSON payload of the object that is updated
     * @return an empty response, as the response is ignored by Gluon CloudLink
     */
    @POST
    @Path("/update/{objectIdentifier}")
    @Consumes(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    public String updateItem(@PathParam("listIdentifier") String listIdentifier,
                             @PathParam("objectIdentifier") String objectIdentifier,
                             String payload) {
        LOG.log(Level.INFO, "Updated item with id " + objectIdentifier + " in list " + listIdentifier + ": " + payload);
        try (JsonReader jsonReader = Json.createReader(new StringReader(payload))) {
            JsonObject jsonObject = jsonReader.readObject();
            String title = jsonObject.containsKey("title") ? jsonObject.getString("title") : "";
            String text = jsonObject.containsKey("text") ? jsonObject.getString("text") : "";
            noteService.update(objectIdentifier, title, text);
        }
        return "{}";
    }

    /**
     * Called from Gluon CloudLink when an existing object is removed from a list. This implementation will remove the
     * object from the database.
     *
     * @param listIdentifier the identifier of the list where the object is removed from
     * @param objectIdentifier the identifier of the object that is removed
     * @return an empty response, as the response is ignored by Gluon CloudLink
     */
    @POST
    @Path("/remove/{objectIdentifier}")
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    public String removeItem(@PathParam("listIdentifier") String listIdentifier,
                             @PathParam("objectIdentifier") String objectIdentifier) {
        LOG.log(Level.INFO, "Removed item with id " + objectIdentifier + " from list " + listIdentifier);
        noteService.remove(objectIdentifier);
        return "{}";
    }

}
