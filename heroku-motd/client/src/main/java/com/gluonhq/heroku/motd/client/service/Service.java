/**
 * Copyright (c) 2018, Gluon
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
package com.gluonhq.heroku.motd.client.service;

import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.cloudlink.client.data.RemoteFunctionObject;
import com.gluonhq.connect.GluonObservableObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javafx.beans.property.SimpleObjectProperty;
import javax.annotation.PostConstruct;

public class Service {

    private static final String MOTD = "heroku-motd-v1";

    private DataClient dataClient;

    @PostConstruct
    public void postConstruct() {
        dataClient = DataClientBuilder.create()
            .operationMode(OperationMode.CLOUD_FIRST)
            .build();
    }

    public SimpleObjectProperty<String> retrieveMOTD() {
        return retrieveMOTDGCL();
    }

    private SimpleObjectProperty<String> retrieveMOTDURL() {
        String motd = "Our first local message";
        try {
            URL url = new URL("https://motdserver.herokuapp.com/rest/mymotd");
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
            motd = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleObjectProperty<String> ssp = new SimpleObjectProperty(motd);
        return ssp;
    }

    private SimpleObjectProperty<String> retrieveMOTDGCL() {
        RemoteFunctionObject f = RemoteFunctionBuilder.create("motd").object();
        GluonObservableObject<String> answer = f.call(String.class);
        return answer;
    }

    private SimpleObjectProperty<String> retrieveMOTDGCLData() {
        //        GluonObservableObject<String> motd = DataProvider
//                .retrieveObject(dataClient.createObjectDataReader(MOTD, String.class, SyncFlag.OBJECT_READ_THROUGH));
//        motd.initializedProperty().addListener((obs, ov, nv) -> {
//            if (nv && motd.get() == null) {
//                motd.set("This is the first Message of the Day!");
//            }
//        });
//        return motd;
        return null;
    }

}
