package com.fyts.mail.controller;

import com.fyts.mail.common.util.FileUtil;
import com.fyts.mail.entity.Attachment;
import com.fyts.mail.service.IAttachmentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mail/upload")
@Api(tags={"文件上传接口"})
public class UploadController {

	Logger logger = LoggerFactory.getLogger(getClass());
	/** 获取系统当前的文件存储方式 */
	@Value("${tools.file.storage}")
	private String storage;
	/** 获取系统当前的文件存储方式 */
	@Value("${tools.file.prefixPlaceholder}")
	private String placeholder;

	@Autowired
	private IAttachmentService attachmentService;


	@PostMapping("md5")
	@ApiOperation(value="文件上传前传入MD5值检查是否存在相同文件", notes="文件上传前传入MD5值检查是否存在相同文件,如果有: 生成新的附件表记录, 返回路径, 不再需要调用上传文件接口")
	public List<Map<String, Object>> checkMd5(@RequestBody List<Attachment> attachments){
		return attachmentService.checkMd5(attachments);
	}

	@PostMapping("single")
	@ApiOperation(value="单个文件上传", notes="上传单个文件,并返回路径,前置接口: /mail/upload/md5")
	public String singleUpload(@ApiParam(value = "所需上传文件",required = true) MultipartFile file,
							   @ApiParam(name="dirName", value = "上传文件的目录(接口对象名)",required = true) String dirName){
		final String md5 = getMd5(file);
		String filePath = upload(file,dirName);
		final Attachment attachment = new Attachment();
		attachment.setMd5(md5);
		attachment.setCreateDate(new Date());
		attachment.setName(file.getOriginalFilename());
		attachment.setPath(filePath);
		attachment.setType(filePath.substring(filePath.lastIndexOf(".")+1));
		attachmentService.save(attachment);
		return  /*placeholder+*/filePath;
	}


	@PostMapping("batch")
	@ApiOperation(value="批量文件上传", notes="上传批量文件,并返回路径列表,前置接口: /mail/upload/md5")
	public List<String> batchUpload(@ApiParam(name = "file", value="文件数据", allowMultiple=true,required=true) HttpServletRequest request,
									@ApiParam(name = "file1", value = "所需上传文件(该处仅供swagger测试使用，具体开发需要上传files属性)",required = false) MultipartFile file1,
									@ApiParam(name = "file2", value = "所需上传文件(该处仅供swagger测试使用，具体开发需要上传files属性)",required = false) MultipartFile file2,
									@ApiParam(name = "dirName", value = "上传文件的目录(接口对象名)",required = true) String dirName){

		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("files");

		List<String> filePaths = new ArrayList<String>();
		String filePath;
		for (MultipartFile multipartFile : files) {
			filePath = upload(multipartFile,dirName);
			filePaths.add(/*placeholder+*/filePath);
		}
		return filePaths;
	}


	/**
	 * 根据不同方式处理文件上传（方法放的位置不太好，后期可能会调整）
	 * @param file
	 * @return
	 */
	String upload(MultipartFile file, String dirName){
		switch (storage) {

			case "local":
				return FileUtil.uploadFile(file,dirName);
			case "fastdfs":
				return null;
			default:
				return null;
		}
	}

	/**
	 * 获取上传文件的md5
	 * @param file
	 */
	private String getMd5(MultipartFile file) {

		try {
			byte[] uploadBytes = file.getBytes();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(uploadBytes);
			String hashString = new BigInteger(1, digest).toString(16);
			return hashString;
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e.toString(), e);
		}
		return null;

	}

}
