package com.fyts.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fyts.mail.entity.Attachment;

import java.util.List;
import java.util.Map;

/**
 * 邮件接口
 * @author 刘志新
 * @email  lzxorz@163.com
 */
public interface IAttachmentService extends IService<Attachment> {

	/** 数据库检查MD5值，已有同MD5值的文件, 生成一条新纪录 */
	List<Map<String, Object>> checkMd5(List<Attachment> attachments);

}
