package com.hx.middleware.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-16 18:34
 * @description
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyPerson  implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
}
