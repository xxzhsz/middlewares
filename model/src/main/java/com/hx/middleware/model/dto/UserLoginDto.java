package com.hx.middleware.model.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-11 20:18
 * @description
 */
@Data
@ToString
public class UserLoginDto implements Serializable {

    @NotBlank
    private String userName;
    @NotBlank
    private String password;

    private Integer userId;
}
