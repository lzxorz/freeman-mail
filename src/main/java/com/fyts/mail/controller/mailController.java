package com.fyts.mail.controller;

import com.fyts.mail.common.util.Result;
import com.fyts.mail.entity.Email;
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
	public Result send(Email mail) {
		try {
			mailService.sendTemplateEmail(mail);
		} catch (Exception e) {
			e.printStackTrace();
			return  Result.error();
		}
		return  Result.ok();
	}
	
	@PostMapping("list")
	public Result list(Email mail) {
		return mailService.listMail(mail);
	}
}
