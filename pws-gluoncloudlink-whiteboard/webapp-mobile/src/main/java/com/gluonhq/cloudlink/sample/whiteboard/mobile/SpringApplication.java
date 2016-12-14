package com.gluonhq.cloudlink.sample.whiteboard.mobile;

import com.gluonhq.cloudlink.sample.whiteboard.mobile.config.ContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class SpringApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringApplication.class)
                .initializers(new ContextInitializer())
                .application()
                .run(args);
    }
}
