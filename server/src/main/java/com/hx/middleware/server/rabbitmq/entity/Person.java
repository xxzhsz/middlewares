package com.hx.middleware.server.rabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-10 18:58
 * @description
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

    private Integer id;
    private String name;
    private String userName;
}
