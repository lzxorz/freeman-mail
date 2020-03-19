package com.fyts.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fyts.mail.entity.Mail;

import java.util.List;

/**
 * 邮件接口
 * @author 刘志新
 * @email  lzxorz@163.com
 */
public interface IMailService extends IService<Mail> {

	/** 发送纯文本邮件 */
	void sendSimpleMail(Mail mail);

	/** 发送富文本邮件 */
	void sendHtmlMail(Mail mail);

	/** 队列 */
	void sendQueue(Mail mail);

	/** Redis 队列 */
	void sendRedisQueue(Mail mail);

	List<Mail> listData(Mail mail);
}
