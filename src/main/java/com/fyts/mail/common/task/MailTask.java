package com.fyts.mail.common.task;

import com.alibaba.fastjson.JSON;
import com.fyts.mail.common.constants.Constants;
import com.fyts.mail.common.util.MailUtil;
import com.fyts.mail.entity.Mail;
import com.fyts.mail.mapper.MailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 邮件定时任务
 * @author 刘志新
 * @email  lzxorz@163.com
 */
@Slf4j
@Component
public class MailTask {

	private static int retryTimes = 3;

	@Resource
	private MailMapper mailMapper;

	/**
	 * 每分钟检查一次, 已到指定发送时间/发送出错,并且重试次数<retryTimes 的邮件push到队列
	 * ??队列的邮件一分钟发不完, 邮件会被重复入队??
	 * @author 刘志新
	 * @email  lzxorz@163.com
	 */
	@Scheduled(cron="0 0/1 * * * ?")
	public void sendMail() {
		log.info("----------执行邮件定时任务 begin----------");
		/*final QueryWrapper<Mail> queryWrapper = new QueryWrapper<Mail>()
				.le("sent_date", new Date()).eq("status", Constants.MAIL_STATUS.SENT.value())
				.or(q -> q.eq("status", Constants.MAIL_STATUS.ERROR.value()).lE("retry_times", retryTimes));*/

		final List<Mail> mails = mailMapper.selectSentList(new Date(), Constants.MAIL_STATUS.SENT.value(), Constants.MAIL_STATUS.ERROR.value(), retryTimes);
		if (!CollectionUtils.isEmpty(mails)) {
			for (Mail mail : mails) {
				MailUtil.sendQueue(mail);
				// TODO 更新重试次数
				log.info("邮件==> {}", JSON.toJSONString(mail));
			}
			log.info("----------执行邮件定时任务 end......push到队列邮件数量: {} ----------", mails.size());
		}

	}

}
