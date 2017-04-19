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
package com.gluonhq.cloudlink.sample.whiteboard.mobile.service;

import com.gluonhq.cloudlink.sample.whiteboard.mobile.model.Item;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;

@Service
public class CloudLinkService {

    private static final String CLOUDLINK_ENDPOINT = "https://cloud.gluonhq.com/3/data/enterprise";

    private static final String CLOUDLINK_APPLICATION_KEY = "GLUON_APPLICATION_KEY";
    private static final String CLOUDLINK_APPLICATION_SECRET = "GLUON_APPLICATION_SECRET";

    private BaseProtectedResourceDetails resourceDetails;

    @PostConstruct
    public void postConstruct() {
        resourceDetails =  new BaseProtectedResourceDetails();
        resourceDetails.setConsumerKey(CLOUDLINK_APPLICATION_KEY);
        resourceDetails.setSharedSecret(new SharedConsumerSecretImpl(CLOUDLINK_APPLICATION_SECRET));
    }

    @Async
    public void addItem(Item item) {
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = Json.createGenerator(writer)) {
            generator.writeStartObject()
                    .write("title", item.getTitle())
                    .write("creationDate", item.getCreationDate())
                    .writeEnd();
        }
        String payload = writer.toString();

        System.out.println("Creating Item: " + item.getId() + " with payload " + payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        OAuthRestTemplate restTemplate = new OAuthRestTemplate(resourceDetails);
        String response = restTemplate.postForObject(CLOUDLINK_ENDPOINT + "/list/items/add/" + item.getId(),
                entity, String.class);
        System.out.println("CloudLink Response: " + response);
    }

    @Async
    public void removeItem(String id) {
        OAuthRestTemplate restTemplate = new OAuthRestTemplate(resourceDetails);
        String response = restTemplate.postForObject(CLOUDLINK_ENDPOINT + "/list/items/remove/" + id,
                null, String.class);
        System.out.println("CloudLink Response: " + response);
    }

}
