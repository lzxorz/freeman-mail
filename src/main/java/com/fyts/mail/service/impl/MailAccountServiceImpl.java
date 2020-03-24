package com.fyts.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyts.mail.entity.MailAccount;
import com.fyts.mail.mapper.MailAccountMapper;
import com.fyts.mail.service.IMailAccountService;
import org.springframework.stereotype.Service;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Service
public class MailAccountServiceImpl extends ServiceImpl<MailAccountMapper, MailAccount> implements IMailAccountService {

}
