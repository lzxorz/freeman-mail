package com.fyts.mail.controller;

import com.fyts.mail.common.util.Result;
import com.fyts.mail.entity.Mail;
import com.fyts.mail.service.IMailService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags ="邮件管理")
@RestController
@RequestMapping("/mail")
public class mailController {

	// @Reference(version = "1.0.0")
	private IMailService mailService;
	
	@PostMapping("send")
	public Result send(Mail mail) {
		try {
			mailService.sendHtmlMail(mail);
		} catch (Exception e) {
			e.printStackTrace();
			return  Result.error();
		}
		return  Result.ok();
	}
	
	@PostMapping("list")
	public Result list(Mail mail) {
		return Result.ok("获取成功", mailService.listData(mail));
	}
}
