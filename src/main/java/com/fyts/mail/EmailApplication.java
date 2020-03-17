package com.fyts.mail;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * 
 * 启动 java -jar spring-boot-mail.jar --server.port=8080
 * linux 下 后台启动  nohup java -jar spring-boot-mail.jar --server.port=8080 &
 *
 */
@SpringBootApplication
@EnableScheduling
//必须配置包扫描、否则Dubbo无法注册服务
// @EnableDubbo(scanBasePackages  = "com.fyts.mail.service.impl")
@MapperScan("com.fyts.mail.mapper")
public class EmailApplication {
	private static final Logger logger = LoggerFactory.getLogger(EmailApplication.class);
	
	public static void main(String[] args){
		SpringApplication.run(EmailApplication.class, args);
		logger.info("邮件服务项目启动");
	}
}