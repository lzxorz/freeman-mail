package com.fyts.mail.service.impl;

import cn.hutool.extra.template.TemplateEngine;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyts.mail.common.queue.MailQueue;
import com.fyts.mail.common.util.Constants;
import com.fyts.mail.common.util.MailUtil;
import com.fyts.mail.common.util.Result;
import com.fyts.mail.entity.Email;
import com.fyts.mail.mapper.EmailMapper;
import com.fyts.mail.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
// @Service(version = "1.0.0")
public class MailServiceImpl extends ServiceImpl<EmailMapper, Email> implements IMailService {

    @Autowired(required = false)
    private MailUtil mailUtil;
    @Autowired(required = false)
    private EmailMapper emailMapper;
    @Autowired(required = false)
    private TemplateEngine templateEngine;
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;
    // @Autowired
    // private DynamicQuery dynamicQuery;

    static {
        System.setProperty("mail.mime.splitlongparameters", "false");
    }


    /**
     * 简单文本邮件
     */
    @Override
    public void sendSimpleMail(Email email) {
        log.info("发送邮件：{}", email.getContent());
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        // message.setFrom(email.getFrom().getUsername());
        //邮件接收人
        message.setTo(email.getTo());
        //邮件主题
        message.setSubject(email.getSubject());
        //邮件内容
        message.setText(email.getContent());
        //发送时间
        message.setSentDate(new Date());
        //发送邮件
        mailUtil.getMailSender().send(message);
        email.setStatus("ok");

        logger.info("发送邮件成功：{}->{}", mailVo.getFrom(), mailVo.getTo());


    }


    /**
     * html邮件
     */
    @Override
    public void sendHtmlMail(Email email) {
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setHost(email.getFrom().getHost());
        mailSender.setPort(email.getFrom().getPort());
		// 是否SSL加密连接
		/*if (email.getOnSsl() == 2) {
			Properties properties = new Properties();
			properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailSender.setJavaMailProperties(properties);
		}*/
		// 需要验证邮箱用户名和密码
		/*if (email.getOnAuth() == 2) {
			mailSender.setUsername(email.getFrom().getUsername());
			mailSender.setPassword(email.getFrom().getPassword());
		}*/
        //获取MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom().getUsername(), email.getFrom().getNickname());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText("<html><body><img src=\"cid:springcloud\" ></body></html>", true);
            // 发送图片
            File file = ResourceUtils.getFile("classpath:static" + Constants.FILE_SEPARATOR + "image" + Constants.FILE_SEPARATOR + "springcloud.png");
            helper.addInline("springcloud", file);

            //可以添加多个图片
            // String rscPath, String rscId)
            // FileSystemResource res = new FileSystemResource(new File(rscPath));
            // helper.addInline(rscId,res);
            helper.addInline("doge.gif", new File("xx/xx/doge.gif"));

            // String filePath
            // helper.addAttachment(filePath.substring(filePath.lastIndexOf(File.separator), new FileSystemResource(new File(filePath)));

            // 发送附件
            file = ResourceUtils.getFile("classpath:static" + Constants.FILE_SEPARATOR + "file" + Constants.FILE_SEPARATOR + "测试文件.zip");
            helper.addAttachment("附件", file);

            // helper.addAttachment("work.docx", new File("xx/xx/work.docx"));
            if (email.getMultipartFiles() != null) {
                for (MultipartFile multipartFile : email.getMultipartFiles()) {
                    helper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
                }
            }

            mailSender.send(message);
            //日志信息
            log.info("邮件已经发送。");
        } catch (MessagingException | UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
            log.error("发送邮件时发生异常！", e);
        }
    }

    @Override
    public void sendTemplateEmail(Email email) {
        // MimeMessagePreparator messagePreparator = mimeMessage -> {
        // 	MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        //
        // 	String content = mailContentBuilder.build(info);
        // 	helper.setText(content,true);
        // };
        // try {
        // 	emailSender.send(messagePreparator);
        // } catch (MailException e) {
        // 	// runtime exception; compiler will not force you to handle it
        // }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom().getUsername(), email.getFrom().getNickname());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());

            String text = templateEngine.getTemplate(email.getTemplate()).render(email.getKvMap());

            helper.setText(text, true);
			mailSender.send(message);
			emailMapper.insert(email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
			log.error("发送邮件时发生异常！", e);
        }
    }


    @Override
    public void sendQueue(Email mail) {
		try {
			MailQueue.getMailQueue().produce(mail);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void sendRedisQueue(Email mail) {
        redisTemplate.convertAndSend("mail", mail);
    }

    @Override
    public List<Email> listData(Email mail) {
        List<Email> list = emailMapper.selectList(null);
        return Result.ok("", list);
    }


}
