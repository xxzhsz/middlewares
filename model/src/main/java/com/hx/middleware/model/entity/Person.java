package com.hx.middleware.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-07 07:54
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
}
