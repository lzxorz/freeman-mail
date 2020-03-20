package com.fyts.mail.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Getter
@Setter
@ApiModel(value = "附件", description = "邮件附件")
public class Attachment extends Model<Mail> {

    @ApiModelProperty(value = "自增主键", notes = "新增不传,修改要传")
    private Long id;
    @ApiModelProperty(value = "附件md5值(相同MD5值的文件只保存一份,再次上传只是生成一条附件数据记录,实现秒传)", required = true)
    private String md5;
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "路径", notes = "发送邮件接口需要传入")
    private String path;
    @ApiModelProperty(value = "文件类型(后缀)", required = true)
    private String type;
    @ApiModelProperty(value = "排序号", example="1")
    @Range(min = 1L, max = 256)
    private Integer sort;
}
