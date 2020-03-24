package com.fyts.mail.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyts.mail.common.base.BaseController;
import com.fyts.mail.common.util.Result;
import com.fyts.mail.entity.MailAccount;
import com.fyts.mail.service.IMailAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Api(tags ="邮件账号管理")
@RestController
@RequestMapping("/mailAccount")
public class MailAccountController extends BaseController {

	// @Reference(version = "1.0.0")
	@Autowired
	private IMailAccountService mailAccountService;

	@GetMapping(value="",headers="api-version=V1")
	@ApiOperation(value ="查询邮件账号",httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name="api-version", value="版本号", paramType="header", required=true, defaultValue="V1"),
			@ApiImplicitParam(name="pageNo",value="页码", paramType="query", required=true, dataType = "Integer", defaultValue="1"),
			@ApiImplicitParam(name="pageSize",value="页容量", paramType="query", required=true, dataType = "Integer", defaultValue="20"),
			@ApiImplicitParam(name="userId",value="用户ID", paramType="query", required=false, dataType = "Long"),
			@ApiImplicitParam(name="status",value="状态", paramType="query", required=false, dataType = "String")
	})
	public Result list(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo, @RequestParam(name="pageSize", defaultValue="20") Integer pageSize,
					   @RequestParam(value="userId" , required=false) Long userId,@RequestParam(value="status" , required=true) String status) {

		final Page<MailAccount> page = mailAccountService.page(new Page<MailAccount>(pageNo, pageSize), new QueryWrapper<MailAccount>()
				.eq(null!=userId, "user_id", userId)
				.eq(StrUtil.isNotBlank(status), "status", status)
				.orderByDesc("create_date"));

		return Result.ok("获取成功",page);
	}
	
	@PostMapping
	@ApiOperation(value ="新增邮件账号",httpMethod = "POST", notes = "注意状态参数")
	public Result save(@RequestBody @Validated MailAccount mailAccount) {
		/*if (!mailAccount.verify()) {
			return Result.error("参数缺失");
		}*/
		final boolean save = mailAccountService.save(mailAccount);

		return (save) ? Result.ok("保存成功") : Result.error("保存失败");
	}

	/*@PutMapping("{id}")
	@ApiOperation(value ="修改邮件账号",httpMethod = "PUT", notes = "草稿状态(可修改)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "邮件ID", paramType = "path", required = true, dataType = "Long")
	})
	public Result update(@PathVariable("id") Long id, @RequestBody @Validated MailAccount mailAccount) {
		*//*if (!mailAccount.verify()) {
			return Result.error("参数缺失");
		}*//*
		mailAccount.setId(id);
		mailAccount.setUpdateDate(new Date());
		final boolean status = mailAccountService.update(mailAccount, new UpdateWrapper<MailAccount>().eq("status", Constants.MAIL_STATUS.DRAFT));

		return  status ? Result.ok("修改成功") : Result.error("修改失败");
	}*/

	/*@PutMapping("send/{id}")
	@ApiOperation(value ="发送/重发邮件",httpMethod = "PUT", notes = "状态设置为待发送,且指定发送时间")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "邮件ID", paramType = "path", required = true, dataType = "Long"),
			@ApiImplicitParam(name = "sentDate", value = "发送时间", paramType = "form", required = true, dataType = "Date")
	})
	public Result send(@PathVariable("id") Long id, @RequestParam("sentDate") Date sentDate) {

		final boolean send = mailAccountService.send(id,sentDate);

		return  send ? Result.ok("发送成功") : Result.error("发送失败,请稍后再试...");
	}*/
	
	/*@PostMapping("list")
	public Result list(Mail mail) {
		return Result.ok("获取成功", mailService.listData(mail));
	}*/
}
