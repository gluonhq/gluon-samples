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
