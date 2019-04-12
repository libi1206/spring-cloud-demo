package com.libi.clouddemo.euerka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author surface
 */
@SpringBootApplication
//开启注册中心
@EnableEurekaServer
public class EuerkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuerkaApplication.class, args);
    }

}
