package com.fyts.mail.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * Email封装类
 */
@Getter
@Setter
@ApiModel(value = "邮件", description = "邮件对象")
public class Email extends Model<Email> {

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



    public Email() {
        super();
    }

    public static Email buildSimpleMail(User from, String[] to, String subject, String content) {
        final Email email = new Email();
        email.from = from;
        email.to = to;
        email.subject = subject;
        email.content = content;
        email.sentDate = new Date();
        return email;
    }

    public static Email buildTemplateMail(User from, String[] to, String subject, String content, String template, HashMap<String, String> kvMap) {
        final Email email = new Email();
        email.from = from;
        email.to = to;
        email.subject = subject;
        email.content = content;
        email.template = template;
        email.kvMap = kvMap;
        email.sentDate = new Date();
        return email;
    }

}
