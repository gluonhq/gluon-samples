package com.gluonhq.cloudlink.sample.whiteboard.base.config;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.MysqlServiceInfo;
import org.springframework.cloud.service.common.OracleServiceInfo;
import org.springframework.cloud.service.common.PostgresqlServiceInfo;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOGGER = Logger.getLogger(ContextInitializer.class.getName());

    private static final String IN_MEMORY_PROFILE = "in-memory";
    private static final Map<Class<? extends ServiceInfo>, String> serviceTypeToProfileName = new HashMap<>();

    static {
        serviceTypeToProfileName.put(PostgresqlServiceInfo.class, "postgres");
        serviceTypeToProfileName.put(MysqlServiceInfo.class, "mysql");
        serviceTypeToProfileName.put(OracleServiceInfo.class, "oracle");
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Cloud cloud = getCloud();

        ConfigurableEnvironment appEnvironment = applicationContext.getEnvironment();

        String[] persistenceProfiles = getCloudProfiles(cloud);
        if (persistenceProfiles == null) {
            persistenceProfiles = new String[] { IN_MEMORY_PROFILE };
        }

        for (String persistenceProfile : persistenceProfiles) {
            appEnvironment.addActiveProfile(persistenceProfile);
        }
    }

    private Cloud getCloud() {
        try {
            CloudFactory cloudFactory = new CloudFactory();
            return cloudFactory.getCloud();
        } catch (CloudException e) {
            return null;
        }
    }

    private String[] getCloudProfiles(Cloud cloud) {
        if (cloud == null) {
            return null;
        }

        List<String> profiles = new ArrayList<>();

        List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        LOGGER.info("Found serviceInfos: " + StringUtils.collectionToCommaDelimitedString(serviceInfos));

        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceTypeToProfileName.containsKey(serviceInfo.getClass())) {
                profiles.add(serviceTypeToProfileName.get(serviceInfo.getClass()));
            }
        }

        if (profiles.size() > 1) {
            throw new IllegalStateException(
                    "Only one service of the following types may be bound to this application: " +
                            serviceTypeToProfileName.values().toString() + ". " +
                            "These services are bound to the application: [" +
                            StringUtils.collectionToCommaDelimitedString(profiles) + "]");
        }

        if (profiles.size() > 0) {
            return createProfileNames(profiles.get(0), "cloud");
        }

        return null;
    }

    private String[] createProfileNames(String baseName, String suffix) {
        String[] profileNames = { baseName, baseName + "-" + suffix };
        LOGGER.log(Level.INFO, "Setting profile names: " + Arrays.toString(profileNames));
        return profileNames;
    }
}
