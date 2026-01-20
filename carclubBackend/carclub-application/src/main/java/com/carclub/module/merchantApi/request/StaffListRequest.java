package com.carclub.module.merchantApi.request;

import com.carclub.common.param.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 员工列表请求参数
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Data
public class StaffListRequest extends PageParam implements Serializable {

    @ApiModelProperty(value="关键字:手机号、姓名", name="keyword")
    private String keyword;

}
