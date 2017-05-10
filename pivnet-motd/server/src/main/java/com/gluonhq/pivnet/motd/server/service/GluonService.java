/*
 * Copyright (c) 2017, Gluon
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
package com.gluonhq.pivnet.motd.server.service;

import java.io.StringReader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Service
public class GluonService {

    private String gclUrl;
    private String gclServerKey;

    @PostConstruct
    public void postConstruct() {
        String vcapServices = System.getenv("VCAP_SERVICES");
        if (vcapServices == null) {
            throw new RuntimeException("Bind your application with a Gluon Application first.");
        }

        try (JsonReader jsonReader = Json.createReader(new StringReader(vcapServices))) {
            JsonObject root = jsonReader.readObject();
            if (!root.containsKey("gluon-cloudlink-service-broker")) {
                throw new RuntimeException("Bind your application with a Gluon Application first.");
            }

            JsonArray gclService = root.getJsonArray("gluon-cloudlink-service-broker");
            JsonObject gclService1 = gclService.getJsonObject(0);
            JsonObject gclCredentials = gclService1.getJsonObject("credentials");

            gclUrl = gclCredentials.getString("hostname");
            gclServerKey = gclCredentials.getString("serverKey");
        }

    }
    
    @Async
    public String getMessage(String objectIdentifier) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> exchange = restTemplate.exchange(gclUrl + "/3/data/enterprise/object/" + objectIdentifier,
                HttpMethod.GET, getHeaders(null), String.class);

        String response = exchange.getBody();
        try (JsonReader jsonReader = Json.createReader(new StringReader(response))) {
            JsonObject jsonObject = jsonReader.readObject();
            if (!jsonObject.containsKey("uid")) {
                // create object
                return addMessage(objectIdentifier);
            }
        }
        
        return exchange.getBody();
    }
    
    @Async
    public String updateMessage(String objectIdentifier, JsonObject payload) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(gclUrl + "/3/data/enterprise/object/" + objectIdentifier + "/update",
                    getHeaders(payload.toString()), String.class);
    }
    
    private String addMessage(String objectIdentifier) {
        JsonObject payload = Json.createObjectBuilder()
                .add("v", "")
                .build();
        
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(gclUrl + "/3/data/enterprise/object/" + objectIdentifier + "/add",
                getHeaders(payload.toString()), String.class);
    }

    private HttpEntity<String> getHeaders(String payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Gluon " + gclServerKey);
        
        if (payload != null) {
            return new HttpEntity<>(payload, headers);
        }
        
        return new HttpEntity<>(headers);
    }
    
}
