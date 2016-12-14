package com.gluonhq.cloudlink.sample.whiteboard.mobile.config;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.sql.DataSource;

@Configuration
@EnableAsync
@Profile({"mysql-cloud", "postgres-cloud", "oracle-cloud"})
public class RelationalCloudDataSourceConfig extends AbstractCloudConfig {

    @Bean
    public DataSource dataSource() {
        return connectionFactory().dataSource();
    }

}
