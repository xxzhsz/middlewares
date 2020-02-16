package com.hx.middleware.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author jxlgcmh
 * @date 2020-02-16 10:27
 * @description
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PraiseDto implements Serializable {
    @NotNull
    private Integer blogId;     //博客id
    @NotNull
    private Integer userId;     //点赞人id
}
