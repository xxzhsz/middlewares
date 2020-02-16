package com.hx.middleware.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author jxlgcmh
 * @date 2020-02-14 08:39
 * @description
 */
@Data
@ToString
public class BookRobDto {
    private Integer userId;//用户id
    private String bookNo; //书籍编号
}
