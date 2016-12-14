package com.gluonhq.cloudlink.sample.whiteboard.base.handler;

import com.gluonhq.cloudlink.sample.whiteboard.base.model.Item;
import com.gluonhq.cloudlink.sample.whiteboard.base.service.WhiteboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.IOException;
import java.io.StringWriter;

@RestController
@RequestMapping("front")
public class FrontHandler {

    private static final String CHARSET = "charset=UTF-8";

    private final WhiteboardService service;

    @Autowired
    public FrontHandler(WhiteboardService service) {
        this.service = service;
    }

    @RequestMapping(value = "whiteboard", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + "; " + CHARSET)
    public String whiteboard() {
        try (StringWriter stringWriter = new StringWriter(); JsonGenerator jsonGenerator = Json.createGenerator(stringWriter)) {
            jsonGenerator.writeStartArray();
            service.listItems().stream()
                    .map(item -> Json.createObjectBuilder()
                            .add("id", item.getId())
                            .add("title", item.getTitle())
                            .add("creationDate", item.getCreationDate())
                            .build())
                    .forEach(jsonGenerator::write);
            jsonGenerator.writeEnd();
            jsonGenerator.close();
            return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "[]";
    }

    @RequestMapping(value = "whiteboard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE + "; " + CHARSET)
    public String whiteboard(@RequestParam("title") String title) {
        Item item = service.createItem(title);
        try (StringWriter stringWriter = new StringWriter(); JsonGenerator jsonGenerator = Json.createGenerator(stringWriter)) {
            jsonGenerator.writeStartObject();
            jsonGenerator.write("id", item.getId());
            jsonGenerator.write("title", item.getTitle());
            jsonGenerator.write("creationDate", item.getCreationDate());
            jsonGenerator.writeEnd();
            jsonGenerator.close();
            return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{}";
    }
}
