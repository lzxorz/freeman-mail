package com.fyts.mail.controller;

import com.fyts.mail.common.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mail/upload")
@Api(tags={"文件上传接口"})
public class UploadController {


	Logger logger = LoggerFactory.getLogger(getClass());


	@PostMapping("single")
	@ApiOperation(value="单个文件上传", notes="上传单个文件，并返回路径")
	public String singleUpload(@ApiParam(value = "所需上传文件",required = true) MultipartFile file,
							   @ApiParam(name="dirName", value = "上传文件的目录(接口对象名)",required = true) String dirName){

		return  upload(file,dirName);
	}


	@PostMapping("batch")
	@ApiOperation(value="批量文件上传", notes="上传批量文件，并返回路径列表")
	public List<String> batchUpload(@ApiParam(name = "file", value="文件数据", allowMultiple=true,required=true) HttpServletRequest request,
									@ApiParam(name = "file1", value = "所需上传文件(该处仅供swagger测试使用，具体开发需要上传files属性)",required = false) MultipartFile file1,
									@ApiParam(name = "file2", value = "所需上传文件(该处仅供swagger测试使用，具体开发需要上传files属性)",required = false) MultipartFile file2,
									@ApiParam(name = "dirName", value = "上传文件的目录(接口对象名)",required = true) String dirName){

		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("files");

		List<String> filePaths = new ArrayList<String>();
		for (MultipartFile multipartFile : files) {
			filePaths.add(upload(multipartFile,dirName));
		}
		return filePaths;
	}


	/**
	 * 获取系统当前的文件存储方式
	 */
	@Value("${tools.file.storage}")
	private String storage;

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

}
