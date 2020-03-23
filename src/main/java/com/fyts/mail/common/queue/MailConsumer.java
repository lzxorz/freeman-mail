package com.fyts.mail.common.queue;

import com.fyts.mail.common.constants.Constants;
import com.fyts.mail.entity.Mail;
import com.fyts.mail.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消费队列
 */
@Slf4j
@Component
public class MailConsumer {
	/**线程池大小*/
	static final int threadPoolSize   = 4;

	// @Reference(check = false)
	@Autowired
	IMailService mailService;

	class PollMail implements Runnable {
		@Autowired
		IMailService mailService;

		public PollMail(IMailService mailService) {
			this.mailService = mailService;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Mail mail = MailQueue.getMailQueue().consume();
					if (mail != null) {
						log.info("剩余邮件总数: {}",MailQueue.getMailQueue().size());
						// 待发送
						/*if (Constants.MAIL_STATUS.SENT.value().equalsIgnoreCase(mail.getStatus())){*/
						final String ctype = mail.getCtype();
						if (Constants.MAIL_CTYPE.PLAIN_TEXT.value().equals(ctype)) {
							mailService.sendSimpleMail(mail);
						}else {
							mailService.sendHtmlMail(mail);
						}
						/*}*/
					}
				} catch (Exception e) {
					log.error("异步发送异常: ", e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	@PostConstruct
	public void startThread() {
		ExecutorService e = Executors.newFixedThreadPool(threadPoolSize);// 固定大小线程池
		for (int i = 0; i < threadPoolSize; i++) {
			e.submit(new PollMail(mailService));
		}
	}

	@PreDestroy
	public void stopThread() {
		log.info("destroy");
	}
}
