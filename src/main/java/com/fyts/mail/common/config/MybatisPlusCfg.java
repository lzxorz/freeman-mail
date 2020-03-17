package com.fyts.mail.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.fyts.mail.mapper")
public class MybatisPlusCfg {


    /***
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}