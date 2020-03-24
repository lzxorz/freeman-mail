package com.fyts.mail.entity;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fyts.mail.common.constants.Constants;
import com.fyts.mail.common.typehandler.StringArrayTypeHandler;
import com.fyts.mail.common.util.MailUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Email封装类
 */
@Getter @Setter @Slf4j
@ApiModel(value = "邮件", description = "邮件对象")
@TableName(autoResultMap = true)
public class Mail extends Model<Mail> {

    @ApiModelProperty(value = "自增主键", notes = "新增不传,修改要传")
    @TableId
    private Long id;

    @ApiModelProperty(value = "添加邮件的用户ID")
    private Long userId;

    @ApiModelProperty(value = "发送者", required = true)
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler=FastjsonTypeHandler.class)
    private MailAccount sender;

    @ApiModelProperty(value = "接收方邮件地址", required = true)
    @NotEmpty
    @TableField(typeHandler= StringArrayTypeHandler.class)
    private String[] receiver;
    @ApiModelProperty(value = "抄送给")
    @TableField(typeHandler= StringArrayTypeHandler.class)
    private String[] cc;
    @ApiModelProperty(value = "加密抄送给")
    @TableField(typeHandler= StringArrayTypeHandler.class)
    private String[] bcc;
    @ApiModelProperty(value = "主题", required = true)
    @NotBlank
	private String subject;

    @ApiModelProperty(value = "邮件内容类型|1:纯文本,2:模板,3:富文本", required = true)
    @NotBlank
	private String ctype;

    @ApiModelProperty(value = "邮件内容", notes = "纯文本邮件、富文本邮件 必传此参数")
	private String content;

    @ApiModelProperty(value = "模板", notes = "模板邮件必传此参数, 需要传入 params")
	private String template;
    @ApiModelProperty(value = "模板参数", notes ="模板邮件必传此参数； demo示例： {\"img1\": \"key是metaData中的resId,value是上传文件返回的路径\", \"img2\": \"key是metaData中的resId,value是上传文件返回的路径\"," +
                                                 "\"username\": \"key是metaData中的key,value是用户输入值\", \"gender\": \"key是metaData中的key,value是用户输入值\"}")
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler=FastjsonTypeHandler.class)
    private Map<String, String> params;

    @ApiModelProperty(value = "附件", notes = "发送邮件,无论成功或失败,都要持久化保存到数据库")
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler=FastjsonTypeHandler.class)
    private List<Attachment> attachments;

    @ApiModelProperty(value = "发送时间", notes = "指定发送时间(不一定会准时发送,有一定的延迟)", required = true)
    @NotNull
    private Date sentDate;

    @ApiModelProperty(value = "状态", notes = "状态|1:草稿(可修改),2:待发送(不可修改,到sentDate指定的时间发送),3:成功,4:失败", required = true)
    @NotBlank
    private String status;
    @ApiModelProperty(value = "发送错误消息", notes = "发送失败时的异常消息")
    private String error;
    @ApiModelProperty(value = "发送错误重试次数")
    private int retryTimes;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "删除标记", hidden = true)
    private String delFlag;

    public static Mail buildSimpleMail(MailAccount sender, String[] receiver, String subject, String content) {
        final Mail mail = new Mail();
        mail.sender = sender;
        mail.receiver = receiver;
        mail.subject = subject;
        mail.content = content;
        return mail;
        // return Mail.builder().sender(sender).receiver(receiver).subject(subject).content(content).build();
    }

    public static Mail buildTemplateMail(MailAccount sender, String[] receiver, String subject, String template, Map<String, String> params,  List<Attachment> attachments) {
        final Mail mail = new Mail();
        mail.sender = sender;
        mail.receiver = receiver;
        mail.subject = subject;
        mail.template = template;
        mail.params = params;
        mail.attachments = attachments;
        return mail;
        // return Mail.builder().sender(sender).receiver(receiver).subject(subject).template(template).params(params).attachments(attachments).build();
    }

    /**
     * 优先使用 传入的邮件发送者, 未传入则使用系统默认发送者
     * @return
     */
    @JSONField(serialize = false)
    public MailUtil.MailSenderContainer getMailSender(){
        if (sender !=null && StrUtil.isNotBlank(sender.getServerHost()) && StrUtil.isNotBlank(sender.getUsername()) && StrUtil.isNotBlank(sender.getPassword())){
            log.info("前端传入MailSender...");
            return MailUtil.createMailSender(sender);
        }
        final MailUtil.MailSenderContainer mailSender = MailUtil.getMailSender();
        if (null!=mailSender) sender = mailSender.getMailAccount();
        return mailSender;
    }

    public boolean verify() {
        boolean res = false;
        // 纯文本、富文本 content 不为空
        if (Constants.MAIL_CTYPE.PLAIN_TEXT.value().equalsIgnoreCase(ctype) || Constants.MAIL_CTYPE.RICH_TEXT.value().equalsIgnoreCase(ctype)){
            res = StrUtil.isNotBlank(content);
        }else {
            // 模板邮件、template、params 不为空
            res = StrUtil.isNotBlank(template) && !CollectionUtils.isEmpty(params);
        }
        if (!res){
            log.error("邮件验证失败 用户: {}, 主题: {}", userId, subject);
        }
        return res;
    }

    public List<Attachment> getAttachments() {
        if (!CollectionUtils.isEmpty(attachments)){
           return JSON.parseArray(JSON.toJSONString(attachments), Attachment.class);
        }
        return attachments;
    }
}
