package com.fyts.mail.common.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 统计失败邮件定时重新发送
 *
 */
@Component
public class SendMail {

	@Scheduled(cron="*/6 * * * * ?")
	public void sendMail() {
		System.out.println("邮件发送");
	}
}
