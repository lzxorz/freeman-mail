package com.fyts.mail.service.impl;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyts.mail.common.queue.MailQueue;
import com.fyts.mail.common.util.Constants;
import com.fyts.mail.entity.Mail;
import com.fyts.mail.mapper.MailMapper;
import com.fyts.mail.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
// @Service(version = "1.0.0")
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements IMailService {

    @Resource
    private MailMapper mailMapper;
    @Autowired(required = false)
    private TemplateEngine templateEngine;
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 简单文本邮件
     */
    @Override
    public void sendSimpleMail(Mail mail) {
        JavaMailSenderImpl mailSender = mail.getMailSender();

        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
         message.setFrom(mail.getFrom().getUsername());
        //邮件接收人
        message.setTo(mail.getTo());
        //邮件主题
        message.setSubject(mail.getSubject());
        //邮件内容
        message.setText(mail.getContent());
        //发送时间
        message.setSentDate(new Date());
        try {
            //发送邮件
            mailSender.send(message);
            mail.setStatus("ok");
            log.info("发送邮件成功：{}->{}", mail.getFrom(), mail.getTo());
        } catch (MailException e) {
            e.printStackTrace();
            log.error("发送邮件失败：{}", e.getMessage());
            mail.setStatus("error");
        }

        // 保存到数据库
        save(mail);

    }


    /**
     * html邮件
     */
    @Override
    public void sendHtmlMail(Mail mail) {
////////////////////////////////////////////////////////////////////////////////
//         MimeMessagePreparator messagePreparator = mimeMessage -> {
//         	MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//
//         	String content = mailContentBuilder.build(info);
//         	helper.setText(content,true);
//         };
//         try {
//         	emailSender.send(messagePreparator);
//         } catch (MailException e) {
//         	// runtime exception; compiler will not force you to handle it
//         }
////////////////////////////////////////////////////////////////////////////////
        JavaMailSenderImpl mailSender = mail.getMailSender();

        //获取MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(mail.getFrom().getUsername(), mail.getFrom().getNickname());
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            
            Template template = templateEngine.getTemplate("templates/" + mail.getTemplate()+".html");
            String text = templateEngine.getTemplate(mail.getTemplate()).render(mail.getKvMap());
            //设置邮件内容，true表示开启HTML文本格式
            helper.setText(text, true);

            // 添加图片
            Map<String,String> kvMap = mail.getKvMap();
            String metaData = "";
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new ClassPathResource("templates/" + mail.getTemplate()+".json").getInputStream()));
                StringBuilder sb = new StringBuilder();
                for(String s = "";(s = br.readLine()) != null; ) {
                    sb.append(s);
                }
                metaData = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("读取模板元数据出错...");
            }
            JSONArray params = (JSONArray)JSONPath.read(metaData,"$.param[?(@.type = 'img')]");
            //可以添加多个模板需要的图片
            if(!CollectionUtils.isEmpty(params) && !CollectionUtils.isEmpty(kvMap)){
                for (int i = 0; i < params.size(); i++) {
                    final String cid = params.getJSONObject(i).getString("cid");
                    if(kvMap.containsKey(cid)){
                        final String rscPath = kvMap.get(cid);
                        // helper.addInline(cid, ResourceUtils.getFile("classpath:static" + Constants.FILE_SEPARATOR + "image" + Constants.FILE_SEPARATOR + "xxx.png"));
                        helper.addInline(cid, new FileSystemResource(new File(rscPath)));
                    }
                }
            }
           
            // 发送附件

            // String filePath
            // helper.addAttachment(filePath.substring(filePath.lastIndexOf(File.separator), new FileSystemResource(new File(filePath)));


            //发送邮件
            mailSender.send(message);
            mail.setStatus("ok");
            log.info("发送邮件成功：{}->{}", mail.getFrom(), mail.getTo());
        } catch (MessagingException | UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
            log.error("发送邮件失败：{}", e.getMessage());
            mail.setStatus("error");
        }

        // 保存到数据库
        save(mail);
    }

    @Override
    public void sendQueue(Mail mail) {
		try {
			MailQueue.getMailQueue().produce(mail);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void sendRedisQueue(Mail mail) {
        redisTemplate.convertAndSend("mail", mail);
    }

    @Override
    public List<Mail> listData(Mail mail) {
        LambdaQueryWrapper<Mail> queryWrapper = new QueryWrapper<Mail>().lambda()
                .eq(Mail::getStatus, mail.getStatus());
        return mailMapper.selectList(queryWrapper);
    }


}
