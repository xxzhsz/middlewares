package com.hx.middleware.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;


/**
 * @author jxlgcmh
 * @date 2020-02-07 13:58
 * @description
 */
@Data
@ToString
public class RedPacketDto {
    private Integer userId;

    //指定多少人抢
    @NotNull
    private Integer total;

    //指定总金额-单位为分
    @NotNull
    private Integer amount;
}
