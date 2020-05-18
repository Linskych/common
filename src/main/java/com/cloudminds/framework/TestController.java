package com.cloudminds.framework;

import com.cloudminds.framework.redis.StringRedisService;
import com.cloudminds.framework.response.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    StringRedisService stringRedisService;

    @GetMapping("/test/lang")
    public R test() {
        stringRedisService.set("teslang", "en");
        return R.ok();
    }
}
