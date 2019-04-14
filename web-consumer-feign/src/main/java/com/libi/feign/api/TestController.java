package com.libi.feign.api;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author libi
 */
@RestController
public class TestController {
    @Autowired
    private TestRemoteApi testRemoteApi;
    @RequestMapping("/test")
    public String test() {
        return testRemoteApi.getMessage();
    }
}
