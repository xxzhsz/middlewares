package com.hx.middleware.server.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author jxlgcmh
 * @date 2020-02-06 20:40
 * @description
 */
@Data
@ToString
public class User {
    private Integer id;
    private String name;

    public User() {
    }

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
