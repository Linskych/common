package com.cloudminds.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@MapperScan({"com.cloudminds.common.repo.dao.mapper"})
@PropertySource("classpath:mysql.properties")
@Configuration
public class MybatisConfig {
}
