package com.libi.springcloud.consumer.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * @author libi
 */
@RestController
public class ConsumerController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/test")
    public String test() {
        String providerUrl = "http://web-provider/test";
        return restTemplate.getForObject(providerUrl, String.class);
    }
}
