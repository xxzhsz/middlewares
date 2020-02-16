package com.hx.middleware.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author jxlgcmh
 * @date 2020-02-11 21:22
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeadInfo {
    private Integer id;
    private String msg;
}
