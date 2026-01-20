package com.carclub.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 日期实体
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Getter
@Setter
public class DateDto {

    @ApiModelProperty("开始时间")
    private String startDate;

    @ApiModelProperty("结束时间")
    private String endDate;
}
