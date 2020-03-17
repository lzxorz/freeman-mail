package com.fyts.mail.common.util;

import com.fyts.mail.entity.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步发送
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Slf4j
@Setter
@Component
public class MailUtil {
    private /*static*/ String defaultEncoding = "Utf-8";
    private /*static*/ Map<Long, JavaMailSenderImpl> pool = new HashMap<>();
    private /*static*/ List<Long> ids = new ArrayList<>();
    private /*static*/ int currentIndex = 0;


    // @PostConstruct
    public void init(List<User> users) {
        log.info("加载mailSender......");
        for (User user : users) {
            JavaMailSenderImpl mailSender = createMailSender(user);

            ids.add(user.getId());
            pool.put(user.getId(), mailSender);
            // 查询数据库....
        }
    }

    /**
     * 根据邮箱用户生成一个MailSender实例
     * @author 刘志新
     * @email  lzxorz@163.com
     */
    public JavaMailSenderImpl createMailSender(User user) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(user.getHost());
        mailSender.setPort(user.getPort());
        mailSender.setUsername(user.getUsername());
        mailSender.setPassword(user.getPassword());
        mailSender.setDefaultEncoding(defaultEncoding);
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout",  "1000");
        // p.setProperty("mail.smtp.auth","true");
        p.setProperty("mail.debug", "true");
        mailSender.setJavaMailProperties(p);
        return mailSender;
    }
    /**
     * 从缓冲池中获取一个MailSender
     * @author 刘志新
     * @email  lzxorz@163.com
     */
    public JavaMailSenderImpl getMailSender(){
        if (!CollectionUtils.isEmpty(pool) && !CollectionUtils.isEmpty(ids) && pool.size()==ids.size()){
            if (!(currentIndex<ids.size())) currentIndex = 0;
            return pool.get(ids.get(currentIndex++));
        }
        return null;
    }

    /*@PreDestroy
    public void destroy(){
        log.info("系统运行结束.....");
    }*/


    private ScheduledExecutorService service = Executors.newScheduledThreadPool(6);

    private final AtomicInteger count = new AtomicInteger(1);

    public void start(final JavaMailSender mailSender,final SimpleMailMessage message) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (count.get() == 2) {
                        service.shutdown();
                        log.info("the task is down");
                    }
                    log.info("start send email and the index is " + count);
                    mailSender.send(message);
                    log.info("send email success");
                }catch (Exception e){
                    log.error("send email fail" , e);
                }

            }
        });
    }
    public void startHtml(final JavaMailSender mailSender,final MimeMessage message) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (count.get() == 2) {
                        service.shutdown();
                        log.info("the task is down");
                    }
                    log.info("start send email and the index is " + count);
                    mailSender.send(message);
                    log.info("send email success");
                }catch (Exception e){
                    log.error("send email fail" , e);
                }

            }
        });
    }

}
