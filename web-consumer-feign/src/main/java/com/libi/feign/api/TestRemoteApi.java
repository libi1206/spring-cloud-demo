package com.libi.feign.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author libi
 */
/**下面的就是web提供者应用名*/
@FeignClient(name = "web-provider")
public interface TestRemoteApi {
    @RequestMapping("/test")
    public String getMessage();
}
