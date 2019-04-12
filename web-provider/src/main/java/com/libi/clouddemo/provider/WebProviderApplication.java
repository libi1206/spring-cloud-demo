package com.libi.clouddemo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author libi
 */
@SpringBootApplication
@EnableEurekaClient
public class WebProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebProviderApplication.class, args);
    }
}
