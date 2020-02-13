package com.hx.middleware.server.distributelock;

import org.jboss.netty.util.internal.NonReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jxlgcmh
 * @date 2020-02-13 12:26
 * @description
 */
public class LockOne {
    private static final Logger log = LoggerFactory.getLogger(LockOne.class);


    public static void main(String[] args) {
        Thread thread1 = new Thread(new LockThread(50), "存钱");
        Thread thread2 = new Thread(new LockThread(-50), "取钱");
        thread1.start();
        thread2.start();
    }

}

class LockThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(LockThread.class);
    Lock lock = new NonReentrantLock();
    private int count;

    public LockThread(int count) {
        this.count = count;
    }


    /**
     * 这里上锁还是有问题的
     */
    @Override
    public void run() {
        lock.lock();
        try {
            for (int i = 1; i < 11; i++) {
                SysConstant.AMOUNT = SysConstant.AMOUNT + count;
                log.info("第{}次{}交易当前账户余额为：{}", i, Thread.currentThread().getName(), SysConstant.AMOUNT);
            }
        }finally{
            lock.unlock();
        }
    }

//    @Override
//    public void run() {
//        for (int i = 1; i < 11; i++) {
//            // 加锁 synchronized
//            synchronized (LockOne.class) {
//                SysConstant.AMOUNT = SysConstant.AMOUNT + count;
//                log.info("第{}次{}交易当前账户余额为：{}", i, Thread.currentThread().getName(), SysConstant.AMOUNT);
//            }
//        }
//    }
}
