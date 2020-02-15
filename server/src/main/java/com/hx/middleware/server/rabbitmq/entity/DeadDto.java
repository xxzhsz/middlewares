package com.hx.middleware.server.rabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-15 22:15
 * @description
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeadDto  implements Serializable {
    private Integer id;
    private String name;
}
