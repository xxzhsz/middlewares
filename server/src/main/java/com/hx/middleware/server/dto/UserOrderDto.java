package com.hx.middleware.server.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-12 08:13
 * @description
 */
@Data
@ToString
public class UserOrderDto implements Serializable {
    @NotBlank
    private String orderNo;
    @NotNull
    private Integer userId;
}
