/*
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
package com.gluonhq.cloudlink.sample.whiteboard.mobile.handler;

import com.gluonhq.cloudlink.sample.whiteboard.mobile.model.Item;
import com.gluonhq.cloudlink.sample.whiteboard.mobile.service.CloudLinkService;
import com.gluonhq.cloudlink.sample.whiteboard.mobile.service.WhiteboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final CloudLinkService cloudLinkService;
    private final WhiteboardService service;

    @Autowired
    public FrontHandler(WhiteboardService service, CloudLinkService cloudLinkService) {
        this.service = service;
        this.cloudLinkService = cloudLinkService;
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
    public String whiteboardCreateItem(@RequestParam("title") String title) {
        Item item = service.createItem(title);
        cloudLinkService.addItem(item);
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

    @RequestMapping(value = "whiteboard/{id}", method = RequestMethod.DELETE)
    public void whiteboardRemoveItem(@PathVariable("id") String id) {
        service.removeItem(id);
        cloudLinkService.removeItem(id);
    }
}
