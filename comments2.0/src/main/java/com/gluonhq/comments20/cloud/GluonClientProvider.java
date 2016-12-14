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
package com.gluonhq.comments20.cloud;

import com.gluonhq.connect.gluoncloud.AuthenticationMode;
import com.gluonhq.connect.gluoncloud.GluonClient;
import com.gluonhq.connect.gluoncloud.GluonClientBuilder;
import com.gluonhq.connect.gluoncloud.GluonCredentials;

/** To get a valid GluonClient it is required signing in here 
 * http://gluonhq.com/products/cloud/register/
 * with a valid email address and the name of the application.
 * 
 * A valid password will be provided.
 * 
 * With the email address and this password, 
 * login here http://portal.gluonhq.com/rest/login
 * to see the pair key/secret strings
 */
public class GluonClientProvider {

    /* 
        Important: Provide your valid key and secret strings
    */
    private static final String APPKEY =   "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX";
    private static final String APPSECRET =   "XXXXXXXX-XXXX-XXXX-XXXX-ZZXXXXXXXXXX";

    private static GluonClient gluonClient;

    public static GluonClient getGluonClient() {
        if (gluonClient == null) {
            gluonClient = GluonClientBuilder.create()
                    .credentials(new GluonCredentials(APPKEY, APPSECRET))
                    .authenticationMode(AuthenticationMode.USER)
                    .build();
        }
        return gluonClient;
    }

}
