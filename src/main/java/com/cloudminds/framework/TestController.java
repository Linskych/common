package com.cloudminds.framework;

import com.cloudminds.framework.redis.StringRedisService;
import com.cloudminds.framework.response.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    StringRedisService stringRedisService;

    @GetMapping("/test/lang")
    public R test() {
        Integer.parseInt("kk");
        log.info("/test/lang");
        return R.ok();
    }
}
