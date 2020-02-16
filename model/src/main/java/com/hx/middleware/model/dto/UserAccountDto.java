package com.hx.middleware.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author jxlgcmh
 * @date 2020-02-13 15:02
 * @description
 */
@Data
@ToString
public class UserAccountDto {
    private Integer userId;
    private Double amount;
}
