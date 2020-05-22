package com.cloudminds;

import com.cloudminds.framework.json.JacksonUtil;
import com.cloudminds.framework.repo.cache.redis.StringRedisService;
import com.cloudminds.framework.response.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    StringRedisService stringRedisService;

    @GetMapping("/test/lang")
    public R test() {
        R r = R.ok().setMsg("18:11:52.781 [http-nio-8080-exec-1] {} INFO  org.springframework.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'");
        log.error(JacksonUtil.toJson(r));

        return r;
    }
}
