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
import com.fyts.mail.entity.Attachment;
import com.fyts.mail.entity.Mail;
import com.fyts.mail.mapper.AttachmentMapper;
import com.fyts.mail.mapper.MailMapper;
import com.fyts.mail.service.IAttachmentService;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
// @Service(version = "1.0.0")
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

    @Resource
    private AttachmentMapper attachmentMapper;


   /** 数据库检查MD5值，已有同MD5值的文件, 生成一条新纪录 */
	public List<Map<String, Object>> checkMd5(final List<Attachment> attachments) {
       final LambdaQueryWrapper<Attachment> queryWrapper = new QueryWrapper<Attachment>().lambda();
        queryWrapper.select(Attachment::getId,Attachment::getMd5,Attachment::getPath).in(Attachment::getMd5, attachments.stream().map(Attachment::getMd5).collect(Collectors.toList())).groupBy(Attachment::getMd5);
        List<Map<String, Object>> listMap = attachmentMapper.selectMaps(queryWrapper);

        return listMap;

    }


}
