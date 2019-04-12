package com.libi.clouddemo.provider.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author libi
 */
@RestController
public class TestController {
    @RequestMapping("/test")
    public String getMessage() {
        return "{\"message\":\"testMessage\"}";
    }
}
