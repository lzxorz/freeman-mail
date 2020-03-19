package com.fyts.mail.common.runner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyts.mail.common.util.MailUtil;
import com.fyts.mail.entity.MailAccount;
import com.fyts.mail.mapper.MailAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 系统启动,加载缓存
 * @author 刘志新
 * @email  lzxorz@163.com
 */
@Order
@Slf4j
@Component
public class StartedUpRunner implements ApplicationRunner/*, CommandLineRunner*/ {

    private int initSize = 100;
    @Resource
    private MailAccountMapper mailAccountMapper;
    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("系统启动 ······");
            final Page<MailAccount> page = new Page<>(1,initSize);
            final Page<MailAccount> userPage = mailAccountMapper.selectPage(page, null);
            mailUtil.init(userPage.getRecords());

            if (context.isActive()) {
                log.info("邮箱服务 启动完毕, 时间: " + LocalDateTime.now());
            }

        } catch (Exception e) {
            log.error("缓存初始化失败，{}", e.getMessage());
            log.error("邮箱服务 启动失败......................");
            context.close();
        }
    }

}
