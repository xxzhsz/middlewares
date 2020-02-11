package com.hx.middleware.server.rabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author jxlgcmh
 * @date 2020-02-11 12:58
 * @description 消息确认实体
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeInfo {
    private int id;
    /**
     * 模式名称
     */
    private String mode;
    /**
     * 模式编码
     */
    private String code;
}
