package com.fyts.mail.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.support.spring.FastJsonContainer;
import com.fyts.mail.common.queue.MailQueue;
import com.fyts.mail.entity.Mail;
import com.fyts.mail.entity.MailAccount;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 异步发送
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Slf4j @Setter @Component
public class MailUtil {
    private static String defaultEncoding = "Utf-8";
    private static int timeOut = 1000;
    private static Map<Long, MailSenderContainer> pool = new HashMap<>();
    private static List<Long> ids = new ArrayList<>();
    private static int currentIndex = 0;

    static {
        System.setProperty("mail.mime.splitlongparameters", "false");// linux默认为 true，会截断附件名
    }

    // @PostConstruct
    public void init(List<MailAccount> mailAccounts) {
        log.info("初始化mailSender缓冲池......");
        for (MailAccount mailAccount : mailAccounts) {
            MailSenderContainer mailSenderContainer = createMailSender(mailAccount);

            ids.add(mailAccount.getId());
            pool.put(mailAccount.getId(), mailSenderContainer);
        }
    }

    /**
     * 根据邮箱用户生成一个MailSender实例
     * @author 刘志新
     * @email  lzxorz@163.com
     */
    public static MailSenderContainer createMailSender(MailAccount mailAccount) {
        final MailSenderContainer mailSenderContainer = new MailSenderContainer();
        mailSenderContainer.setMailAccount(mailAccount);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailAccount.getServerHost());
        mailSender.setPort(mailAccount.getServerPort());
        mailSender.setDefaultEncoding(defaultEncoding);
        //需要验证发件人邮箱信息，username表示用户邮箱，password表示对应邮件授权码
        mailSender.setUsername(mailAccount.getUsername());
        mailSender.setPassword(mailAccount.getPassword());

        Properties p = new Properties();
        p.put("mail.smtp.timeout",  timeOut);//设置链接超时
        // p.put("mail.debug", "true");//启用调试
        p.put("mail.smtp.auth","true");//开启认证 让邮箱服务器 认证 用户名和密码是否正确
        p.put("mail.smtp.starttls.enable", true);
        p.put("mail.smtp.port", Integer.toString(mailAccount.getServerPort()));//设置端口
        p.put("mail.smtp.socketFactory.port", Integer.toString(mailAccount.getServerSslPort()));//设置SSL端口
        p.put("mail.smtp.socketFactory.fallback", "false");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");//SSL加密连接
        mailSender.setJavaMailProperties(p);

        mailSenderContainer.setJavaMailSender(mailSender);
        return mailSenderContainer;
    }

    /**
     * 从缓冲池中获取一个MailSender
     * @author 刘志新
     * @email  lzxorz@163.com
     */
    public static MailSenderContainer getMailSender(){
        if (!CollectionUtils.isEmpty(pool) && !CollectionUtils.isEmpty(ids) && pool.size()==ids.size()){
            if (!(currentIndex<ids.size())) currentIndex = 0;
            log.info("缓冲池获取MailSender...");
            return pool.get(ids.get(currentIndex++));
        }
        log.info("缓冲池获取MailSender失败...");
        return null;
    }

    public static JSONArray getMetaDataByTemplate(String template) {
        JSONArray result = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("templates/" + template+".json").getInputStream()));
            StringBuilder sb = new StringBuilder();
            for(String s = "";(s = br.readLine()) != null; ) {
                sb.append(s);
            }
            result = (JSONArray) JSONPath.read(sb.toString(), "$.params[?(@.type = 'img')]");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("读取模板元数据出错...");
        }
        return result;
    }

    /*@PreDestroy
    public void destroy(){
        log.info("系统运行结束.....");
    }*/

    public static void sendQueue(Mail mail) {
        try {
            MailQueue.getMailQueue().produce(mail);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("邮件ID: {} push到队列失败, 异常消息: {}", mail.getId(), e.getMessage());
        }
    }
    /*private ScheduledExecutorService service = Executors.newScheduledThreadPool(6);

    private final AtomicInteger count = new AtomicInteger(1);

    public void start(final JavaMailSender mailSender,final SimpleMailMessage message) {
        service.execute(() -> {
            try {
                if (count.get() == 2) {
                    service.shutdown();
                    log.info("the task is down");
                }
                log.info("start send email and the index is " + count);
                mailSender.send(message);
                log.info("邮件发送成功");
            } catch (Exception e) {
                log.error("邮件发送失败", e);
            }
        });
    }
    public void startHtml(final JavaMailSender mailSender,final MimeMessage message) {
        service.execute(() -> {
                try {
                    if (count.get() == 2) {
                        service.shutdown();
                        log.info("the task is down");
                    }
                    log.info("start send email and the index is " + count);
                    mailSender.send(message);
                    log.info("邮件发送成功");
                }catch (Exception e){
                    log.error("邮件发送失败" , e);
                }
        });
    }*/

    public static class MailSenderContainer {
        private JavaMailSenderImpl javaMailSender;
        private MailAccount mailAccount;

        public MailAccount getMailAccount() { return mailAccount; }

        public void setMailAccount(MailAccount mailAccount) { this.mailAccount = mailAccount; }

        public JavaMailSenderImpl getJavaMailSender() { return javaMailSender; }

        public void setJavaMailSender(JavaMailSenderImpl javaMailSender) { this.javaMailSender = javaMailSender; }
    }
}
