package com.hx.middleware.server.util;

/**
 * @author jxlgcmh
 * @date 2020-02-15 20:51
 * @description
 */

import com.hx.middleware.server.dto.RSetDto;

import java.util.Comparator;

/**
 * 自定义比较器
 */
public class RSetComparator implements Comparator<RSetDto> {

    @Override
    public int compare(RSetDto o1, RSetDto o2) {
        return o2.getAge().compareTo(o1.getAge());
    }
}

