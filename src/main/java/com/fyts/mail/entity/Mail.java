package com.fyts.mail.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fyts.mail.common.util.MailUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;

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
    private MailAccount from;
    @ApiModelProperty(value = "接收方邮件地址", required = true)
    private String[] to;
    @ApiModelProperty(value = "抄送给")
    private String[] cc;
    @ApiModelProperty(value = "加密抄送给")
    private String[] bcc;
    @ApiModelProperty(value = "主题", required = true)
	private String subject;
    @ApiModelProperty(value = "发送时间")
    private Date sentDate;

    @ApiModelProperty(value = "邮件内容")
	private String content;

    @ApiModelProperty(value = "模板", notes = "使用模板,需要传入kvMap")
	private String template;
    @ApiModelProperty(value = "模板参数", notes ="模板图片: key为metaData中的cid,value为调用上传文件接口返回的路径;\n"+
                                                "模板变量: key为metaData中的key,value为实际的变量值;\n")
    private HashMap<String, Object> kvMap;

    @ApiModelProperty(value = "附件Ids", notes = "发送邮件,无论成功或失败,都要持久化保存到数据库,附件ID用英文逗号拼接", hidden = true)
    private String attachmentIds;

    @ApiModelProperty(value = "发送状态", notes = "发送邮件,无论成功或失败,都要持久化保存到数据库,发送成功值为ok,发送失败值为error")
    private String status;
    @ApiModelProperty(value = "发送错误消息", notes = "发送失败时的异常消息")
    private String error;


    public Mail() {
        super();
    }

    public static Mail buildSimpleMail(MailAccount from, String[] to, String subject, String content) {
        final Mail mail = new Mail();
        mail.from = from;
        mail.to = to;
        mail.subject = subject;
        mail.content = content;
        mail.sentDate = new Date();
        return mail;
    }

    public static Mail buildTemplateMail(MailAccount from, String[] to, String subject, String content, String template, HashMap<String, String> kvMap) {
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

    /**
     * 优先使用 传入的邮件发送者, 未传入则使用系统默认发送者
     * @return
     */
    public JavaMailSenderImpl getMailSender(){
        if (from!=null && StrUtil.isNotBlank(from.getHost()) && StrUtil.isNotBlank(from.getUsername()) && StrUtil.isNotBlank(from.getPassword())){
            log.info("前端传入MailSender...");
            return MailUtil.createMailSender(from);
        }
        return MailUtil.getMailSender();
    }

    public boolean isHtml(){
        return StrUtil.isNotBlank(template);
    }

}
