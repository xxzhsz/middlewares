package com.hx.middleware.server.controller.lock.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author jxlgcmh
 * @date 2020-02-13 16:44
 * @description
 */
@Data
@ToString
public class UserRegDto {
    private String userName; //用户名
    private String password; //密码
}
