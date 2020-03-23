package com.fyts.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fyts.mail.entity.Mail;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/** 邮件 */
public interface MailMapper extends BaseMapper<Mail> {

    List<Mail> selectSentList(@Param("sentDate") Date sentDate, @Param("statusSent") String statusSent, @Param("statusError") String statusError, @Param("retryTimes") int retryTimes);
}
