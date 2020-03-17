package com.fyts.mail.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Getter
@Setter
@Builder
@ApiModel(value = "电子邮箱用户", description = "电子邮箱用户")
public class User extends Model<Email> {

    @ApiModelProperty(value = "自增主键", notes = "新增不传,修改要传")
    private Long id;
    @ApiModelProperty(value = "用户昵称")
    private String nickname;
    @ApiModelProperty(value = "邮箱地址", required = true)
    private String username;
    @ApiModelProperty(value = "客户端授权码，不是邮箱密码", required = true)
    private String password;
    @ApiModelProperty(value = "邮件服务协议")
    private String protocol;
    @ApiModelProperty(value = "发送邮件服务器地址")
    private String host;
    @ApiModelProperty(value = "发送邮件服务器端口")
    private int port;
}
