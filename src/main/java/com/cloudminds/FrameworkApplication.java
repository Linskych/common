package com.cloudminds;

import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FrameworkApplication {

    public static void main(String[] args) {
        //Enable async logger
        System.setProperty("log4j2.contextSelector", AsyncLoggerContextSelector.class.getName());

        SpringApplication.run(FrameworkApplication.class, args);
    }

}
