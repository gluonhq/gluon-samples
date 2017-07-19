package com.gluonhq.javaee.motd.server.service;

import com.gluonhq.cloudlink.enterprise.sdk.base.CloudLinkClient;
import com.gluonhq.cloudlink.enterprise.sdk.javaee.CloudLinkConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GluonService {

    private static final String GLUON_SERVER_KEY = "your-key-from-gcl-dashboard";

    private static final String MOTD = "javaee-motd-v1";

    @Inject
    @CloudLinkConfig(serverKey = GLUON_SERVER_KEY)
    private CloudLinkClient gclClient;

    public String getMotd() {
        String motd = gclClient.getObject(MOTD, String.class);

        if (motd == null) {
            // instantiate with initial motd
            motd = gclClient.addObject(MOTD, "Initial Message of the Day!");
        }

        return motd;
    }

    public void setMotd(String motd) {
        gclClient.updateObject(MOTD, motd);
    }
}
