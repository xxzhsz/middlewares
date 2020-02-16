package com.hx.middleware.model.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-15 13:29
 * @description
 */
@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RSetDto  implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
    private Double score;
}
