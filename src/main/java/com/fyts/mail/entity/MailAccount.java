package com.fyts.mail.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Getter @Setter
@ApiModel(value = "电子邮箱账号", description = "电子邮箱用户")
public class MailAccount extends Model<MailAccount> {

    @ApiModelProperty(value = "自增主键", notes = "新增不传,修改要传")
    @TableId
    private Long id;
    @ApiModelProperty(value = "用户名称")
    private String name;
    @ApiModelProperty(value = "邮箱地址", required = true)
    private String username;
    @ApiModelProperty(value = "客户端授权码，不是邮箱密码", required = true)
    private String password;
    @ApiModelProperty(value = "分类", required = true)
    private String type;
    @ApiModelProperty(value = "状态", required = true)
    @TableField(value = "`status`")
    private String status;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "邮件服务协议")
    private String protocol="smtp";
    @ApiModelProperty(value = "发送邮件服务器地址", required = true)
    private String serverHost;
    @ApiModelProperty(value = "发送邮件服务器端口")
    private int serverPort=25;
    @ApiModelProperty(value = "发送邮件服务器SSL端口")
    private int serverSslPort=465;

    @ApiModelProperty(value = "创建人")
    private Long createBy;
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
    @ApiModelProperty(value = "更新人")
    private Long updateBy;
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "删除标记", hidden = true)
    private String delFlag;

    @ApiModelProperty(value = "扩展字段1", hidden = true)
    private String ext1;
    @ApiModelProperty(value = "扩展字段2", hidden = true)
    private String ext2;
    @ApiModelProperty(value = "扩展字段3", hidden = true)
    private String ext3;
    @ApiModelProperty(value = "扩展字段4", hidden = true)
    private String ext4;
    @ApiModelProperty(value = "扩展字段5", hidden = true)
    private String ext5;

    public MailAccount() {
    }
}
