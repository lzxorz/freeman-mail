package com.fyts.mail.service.impl;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.alibaba.fastjson.support.spring.FastJsonViewResponseBodyAdvice;
import com.alibaba.fastjson.support.spring.annotation.FastJsonView;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyts.mail.common.constants.Constants;
import com.fyts.mail.common.queue.MailQueue;
import com.fyts.mail.common.util.MailUtil;
import com.fyts.mail.entity.Attachment;
import com.fyts.mail.entity.Mail;
import com.fyts.mail.mapper.MailMapper;
import com.fyts.mail.service.IMailService;
import com.jfinal.template.Engine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
// @Service(version = "1.0.0")
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements IMailService {

    @Resource
    private MailMapper mailMapper;
    //文件处理绝对根路径
    @Value("${tools.file.local.path}")
    private String rootPath ;

    @Autowired(required = false)
    private Engine templateEngine;
    // @Autowired(required = false)
    // private RedisTemplate<String, String> redisTemplate;


    @Override
    public boolean save(Mail mail) {
        mail.setCreateDate(new Date());
        final boolean insert = retBool(mailMapper.insert(mail));

        long second = ChronoUnit.SECONDS.between(mail.getSentDate().toInstant(), Instant.now());
        // 状态为 待发送, 指定发送时间在10秒内, push到队列
        if (insert && Constants.MAIL_STATUS.SENT.value().equalsIgnoreCase(mail.getStatus()) && second<10){
            MailUtil.sendQueue(mail);
        }
        return insert;
    }

    @Override
    public boolean send(Long id, Date sentDate) {

        final Mail mail = new Mail();
        mail.setStatus(Constants.MAIL_STATUS.SENT.value());
        mail.setSentDate(sentDate);
        mail.setUpdateDate(new Date());
        final boolean update = retBool(mailMapper.update(mail, new UpdateWrapper<Mail>().eq("id", id)));
        long second = ChronoUnit.SECONDS.between(sentDate.toInstant(), Instant.now());
        // 指定发送时间在10秒内, push到队列
        if (update && second<10){
            MailUtil.sendQueue(mailMapper.selectById(id));
        }
        return update;
    }

    /**
     * 简单文本邮件
     */
    @Override
    public boolean sendSimpleMail(Mail mail) {
        JavaMailSenderImpl mailSender = mail.getMailSender().getJavaMailSender();

        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
         message.setFrom(mail.getSender().getUsername());
        //邮件接收人
        message.setTo(mail.getReceiver());
        //邮件主题
        message.setSubject(mail.getSubject());
        //邮件内容
        message.setText(mail.getContent());
        //发送时间
        // message.setSentDate(new Date());
        try {
            //发送邮件
            mailSender.send(message);
            mail.setStatus(Constants.MAIL_STATUS.OK.value());
            log.info("发送邮件成功：{}->{}", mail.getSender(), mail.getReceiver());
        } catch (MailException e) {
            e.printStackTrace();
            log.error("发送邮件失败：{}", e.getMessage());
            mail.setStatus(Constants.MAIL_STATUS.ERROR.value());
        }

        // 保存到数据库
        final boolean update = update(mail, new UpdateWrapper<Mail>().eq("id", mail.getId()));
        return update;
    }


    /**
     * html邮件
     */
    @Override
    public boolean sendHtmlMail(Mail mail) {
        ////////////////////////////////////////////////////////////////////////////////
        // MimeMessagePreparator messagePreparator = mimeMessage -> {
        //     MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        //
        //     String content = mailContentBuilder.build(info);
        //     helper.setText(content,true);
        // };
        // try {
        //     emailSender.send(messagePreparator);
        // } catch (MailException e) {
        //     // runtime exception; compiler will not force you to handle it
        // }
        ////////////////////////////////////////////////////////////////////////////////
        MailUtil.MailSenderContainer mailSender = mail.getMailSender();
        //获取MimeMessage对象
        MimeMessage message = mailSender.getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(mail.getSender().getUsername(), mail.getSender().getName());
            helper.setTo(mail.getReceiver());
            helper.setSubject(mail.getSubject());

            //模板html文件
            String text = templateEngine.getTemplate( mail.getTemplate()+".html").renderToString(mail.getParams());
            //设置邮件内容，true表示开启HTML文本格式
            helper.setText(text, true);

            //可以添加模板需要的图片
            Map<String,String> params = mail.getParams();
            JSONArray metaParams = MailUtil.getMetaDataByTemplate(mail.getTemplate());
            if(!CollectionUtils.isEmpty(metaParams) && !CollectionUtils.isEmpty(params)){
                for (int i = 0; i < metaParams.size(); i++) {
                    final String resId = metaParams.getJSONObject(i).getString("resId");
                    if(params.containsKey(resId)){
                        final String rscPath = params.get(resId);
                        // helper.addInline(cid, ResourceUtils.getFile("classpath:static" + Constants.FILE_SEPARATOR + "image" + Constants.FILE_SEPARATOR + "xxx.png"));
                        helper.addInline(resId, new FileSystemResource(new File(rootPath+rscPath)));
                    }
                }
            }
           
            // 发送附件
            if(!CollectionUtils.isEmpty(mail.getAttachments())) {
                for (Attachment attachment : mail.getAttachments()) {
                    helper.addAttachment(attachment.getName(), new FileSystemResource(new File(rootPath+attachment.getPath())));
                }
            }

            //发送邮件
            mailSender.getJavaMailSender().send(message);
            mail.setStatus(Constants.MAIL_STATUS.OK.value());
            log.info("发送邮件成功：{}->{}", mail.getSender(), mail.getReceiver());
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("发送邮件失败：{}", e.getMessage());
            mail.setStatus(Constants.MAIL_STATUS.ERROR.value());
        }

        // 保存到数据库
        final boolean update = update(mail, new UpdateWrapper<Mail>().eq("id", mail.getId()));
        return update;
    }

    /*@Override
    public void sendQueue(Mail mail) {
        try {
            MailQueue.getMailQueue().produce(mail);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("邮件ID: {} push到队列失败, 异常消息: {}", mail.getId(), e.getMessage());
        }
	}*/

    /*@Override
    public void sendRedisQueue(Mail mail) {
        redisTemplate.convertAndSend("mail", mail);
    }*/



}
