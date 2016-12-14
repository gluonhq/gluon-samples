package com.gluonhq.cloudlink.sample.whiteboard.mobile.gluoncloudlink;

import com.gluonhq.cloudlink.sample.whiteboard.mobile.service.WhiteboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/cloudlink/list/{listIdentifier}")
public class ListHandler {

    private final WhiteboardService service;

    @Autowired
    public ListHandler(WhiteboardService service) {
        this.service = service;
    }

    @RequestMapping(value = "/add/{objectIdentifier}", method = RequestMethod.POST)
    public void addItem(@PathVariable("listIdentifier") String listIdentifier,
                        @PathVariable("objectIdentifier") String objectIdentifier,
                        @RequestBody String payload) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(payload))) {
            JsonObject jsonObject = jsonReader.readObject();
            String title = jsonObject.containsKey("title") ? jsonObject.getString("title") : "";
            long creationDate = jsonObject.containsKey("creationDate") ? jsonObject.getJsonNumber("creationDate").longValue() : LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            service.createItem(objectIdentifier, title, creationDate);
        }
    }

    @RequestMapping(value = "/update/{objectIdentifier}", method = RequestMethod.POST)
    public void updateItem(@PathVariable("listIdentifier") String listIdentifier,
                           @PathVariable("objectIdentifier") String objectIdentifier,
                           @RequestBody String payload) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(payload))) {
            JsonObject jsonObject = jsonReader.readObject();
            String title = jsonObject.containsKey("title") ? jsonObject.getString("title") : "";
            long creationDate = jsonObject.containsKey("creationDate") ? jsonObject.getJsonNumber("creationDate").longValue() : LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            service.updateItem(objectIdentifier, title, creationDate);
        }
    }

    @RequestMapping(value = "/remove/{objectIdentifier}", method = RequestMethod.POST)
    public void removeItem(@PathVariable("listIdentifier") String listIdentifier,
                           @PathVariable("objectIdentifier") String objectIdentifier) {
        service.removeItem(objectIdentifier);
    }
}
