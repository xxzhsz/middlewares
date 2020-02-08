package com.hx.middleware.server.event;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * @author jxlgcmh
 * @date 2020-02-08 10:04
 * @description
 */
@Data
@ToString
public class LoginEvent extends ApplicationEvent implements Serializable {

    private String userName;
    private String  loginTime;
    private String  ip;


    public LoginEvent(Object source) {
        super(source);
    }

    public LoginEvent(Object source, String userName, String loginTime, String ip) {
        super(source);
        this.userName = userName;
        this.loginTime = loginTime;
        this.ip = ip;
    }
}
