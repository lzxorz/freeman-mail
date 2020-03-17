package com.fyts.mail.entity;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fyts.mail.common.util.MailUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;

/**
 * Email封装类
 */
@Getter @Setter @Slf4j
@ApiModel(value = "邮件", description = "邮件对象")
public class Mail extends Model<Mail> {

    @ApiModelProperty(value = "自增主键", notes = "新增不传,修改要传")
    private Long id;
    @ApiModelProperty(value = "发送者", required = true)
    @TableField()
    private User from;
    @ApiModelProperty(value = "接收方邮件地址", required = true)
    private String[] to;
    @ApiModelProperty(value = "抄送给")
    private String[] cc;
    @ApiModelProperty(value = "加密抄送给")
    private String[] bcc;
    @ApiModelProperty(value = "主题", required = true)
	private String subject;
    @ApiModelProperty(value = "邮件内容", required = true)
	private String content;
    @ApiModelProperty(value = "模板", notes = "使用模板,需要传入kvMap")
	private String template;
    @ApiModelProperty(value = "模板参数", notes = "kvMap")
    private HashMap<String, String> kvMap;
    @ApiModelProperty(value = "发送时间")
    private Date sentDate;
    @ApiModelProperty(value = "发送状态")
    private String status;
    @ApiModelProperty(value = "发送错误消息")
    private String error;

    @JsonIgnore @JSONField(serialize = false)
    @ApiModelProperty(value = "发送错误消息", hidden = true)
    @TableField(exist = false)
    private MultipartFile[] multipartFiles;
    private String[] filePath;



    public Mail() {
        super();
    }

    public static Mail buildSimpleMail(User from, String[] to, String subject, String content) {
        final Mail mail = new Mail();
        mail.from = from;
        mail.to = to;
        mail.subject = subject;
        mail.content = content;
        mail.sentDate = new Date();
        return mail;
    }

    public static Mail buildTemplateMail(User from, String[] to, String subject, String content, String template, HashMap<String, String> kvMap) {
        final Mail mail = new Mail();
        mail.from = from;
        mail.to = to;
        mail.subject = subject;
        mail.content = content;
        mail.template = template;
        mail.kvMap = kvMap;
        mail.sentDate = new Date();
        return mail;
    }

    public JavaMailSenderImpl getMailSender(){
        if (from!=null && StrUtil.isNotBlank(from.getHost()) && StrUtil.isNotBlank(from.getUsername()) && StrUtil.isNotBlank(from.getPassword())){
            log.info("前端传入MailSender...");
            return MailUtil.createMailSender(from);
        }
        return MailUtil.getMailSender();
    }

}
