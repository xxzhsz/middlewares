package com.hx.middleware.model.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-15 11:51
 * @description
 */
@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BloomDto implements Serializable {
    private Integer id;
    private String name;
}
