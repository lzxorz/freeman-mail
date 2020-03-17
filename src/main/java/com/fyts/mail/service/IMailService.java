package com.fyts.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fyts.mail.common.util.Result;
import com.fyts.mail.entity.Email;

/**
 * 邮件接口
 * @author 刘志新
 * @email  lzxorz@163.com
 */
public interface IMailService extends IService<Email> {

	/** 发送纯文本邮件 */
	void sendSimpleMail(Email mail);

	/** 发送富文本邮件 */
	void sendHtmlMail(Email mail);

	/** 发送模版邮件 */
	void sendTemplateEmail(Email mail);

	/** 队列 */
	void sendQueue(Email mail);

	/** Redis 队列 */
	void sendRedisQueue(Email mail);

	Result listMail(Email mail);
}
