package com.hx.middleware.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author jxlgcmh
 * @date 2020-02-07 13:25
 * @description
 */
public class RedPacketUtil {

    /**
     *
     * @param totalAmount  钱是以分统计  1元=100分
     * @param totalPeopleNum
     * @return
     */
    public static List<Integer> dividedRedPacket(Integer totalAmount, Integer totalPeopleNum) {
        List<Integer> amountList =new ArrayList<>();
        if (totalAmount > 0 && totalPeopleNum > 0) {
            Integer resAmount = totalAmount;
            Integer resPeopleNum= totalPeopleNum;
            Random random =new Random();
            for (int i = 0; i < totalPeopleNum -1; i++) {
                int amount =  random.nextInt(resAmount / resPeopleNum * 2 - 1) + 1;
                resAmount-=amount;
                resPeopleNum--;
                amountList.add(amount);
            }
            // 添加最后一个
            amountList.add(resAmount);
        }
        return amountList;
    }
}
