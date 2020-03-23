package com.fyts.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Getter @Setter
@ApiModel(value = "附件", description = "邮件附件")
public class Attachment extends Model<Attachment> {

    @ApiModelProperty(value = "自增主键", notes = "新增不传,修改要传")
    @TableId/*(value = "id",type = IdType.AUTO)*/
    private Long id;
    @ApiModelProperty(value = "附件md5值(相同MD5值的文件只保存一份,再次上传只是生成一条附件数据记录,实现秒传)", required = true)
    @NotBlank @Length(min = 16, max = 32)
    private String md5;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank @Length(min = 1, max = 52)
    private String name;

    @ApiModelProperty(value = "附件路径", notes = "发送邮件接口需要传入")
    private String path;

    @ApiModelProperty(value = "文件类型(后缀)", required = true)
    @NotBlank @Length(min = 1, max = 10)
    private String type;
    @ApiModelProperty(value = "排序号", example="1")
    @Range(min = 1L, max = 256)
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;
    @ApiModelProperty(value = "删除标记", hidden = true)
    private String delFlag;
}
