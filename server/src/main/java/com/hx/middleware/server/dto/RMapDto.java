package com.hx.middleware.server.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-15 12:52
 * @description
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RMapDto implements Serializable {
    private Integer id;
    private String name;
}
