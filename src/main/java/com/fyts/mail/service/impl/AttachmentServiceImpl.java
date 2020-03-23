package com.fyts.mail.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyts.mail.entity.Attachment;
import com.fyts.mail.mapper.AttachmentMapper;
import com.fyts.mail.service.IAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
// @Service(version = "1.0.0")
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

    @Resource
    private AttachmentMapper attachmentMapper;


   /** 数据库检查MD5值，已有同MD5值的文件, 生成一条新纪录 */
	public List<Map<String, Object>> checkMd5(final List<Attachment> attachments) {
       final LambdaQueryWrapper<Attachment> queryWrapper = new QueryWrapper<Attachment>().lambda();
        queryWrapper.select(Attachment::getId,Attachment::getMd5,Attachment::getPath).in(Attachment::getMd5, attachments.stream().map(Attachment::getMd5).collect(Collectors.toList())).groupBy(Attachment::getMd5);
        List<Map<String, Object>> listMap = attachmentMapper.selectMaps(queryWrapper);
        return listMap;

    }


}
