package com.fyts.mail.common.queue;

import com.fyts.mail.entity.Mail;
import com.fyts.mail.service.IMailService;
import lombok.extern.slf4j.Slf4j;
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

	// @Reference(check = false)
	IMailService mailService;

	class PollMail implements Runnable {
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
						mailService.sendSimpleMail(mail);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@PostConstruct
	public void startThread() {
		ExecutorService e = Executors.newFixedThreadPool(2);// 固定大小线程池
		e.submit(new PollMail(mailService));
		e.submit(new PollMail(mailService));
	}

	@PreDestroy
	public void stopThread() {
		log.info("destroy");
	}
}
