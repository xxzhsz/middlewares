package com.hx.middleware.server.service.lock;

import com.hx.middleware.model.dto.UserAccountDto;
import com.hx.middleware.model.entity.UserAccount;
import com.hx.middleware.model.entity.UserAccountRecord;
import com.hx.middleware.model.mapper.UserAccountMapper;
import com.hx.middleware.model.mapper.UserAccountRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jxlgcmh
 * @date 2020-02-13 15:04
 * @description
 */
@Service
public class DataBaseLockService {
    private static final Logger log = LoggerFactory.getLogger(DataBaseLockService.class);

    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserAccountRecordMapper userAccountRecordMapper;

    /**
     * 没有锁
     *
     * @param userAccountDto
     */
    public void withdrawNoLock(UserAccountDto userAccountDto) {
        UserAccount userAccount = userAccountMapper.selectByUserId(userAccountDto.getUserId());
        if (userAccount != null && userAccount.getAmount().doubleValue() >= userAccountDto.getAmount()) {
            userAccountMapper.updateAmount(userAccountDto.getAmount(), userAccount.getId());
            // 更新提现记录
            UserAccountRecord userAccountRecord = new UserAccountRecord();
            userAccountRecord.setAccountId(userAccount.getId());
            userAccountRecord.setMoney(BigDecimal.valueOf(userAccountDto.getAmount()));
            userAccountRecord.setCreateTime(new Date());
            userAccountRecordMapper.insert(userAccountRecord);
            log.info("提现成功:{}元", userAccountDto.getAmount());
        } else {
            throw new RuntimeException("账户不存在或余额不足！");
        }
    }


    /**
     * 乐观锁处理
     *
     * @param userAccountDto
     */
    public void withdrawWithLock(UserAccountDto userAccountDto) {
        UserAccount userAccount = userAccountMapper.selectByUserId(userAccountDto.getUserId());
        if (userAccount != null && userAccount.getAmount().doubleValue() >= userAccountDto.getAmount()) {
            // 从数据库查是否有对应的记录
            int res = userAccountMapper.updateByPKVersion(userAccountDto.getAmount(), userAccount.getId(), userAccount.getVersion());
            // 更新提现记录
            if (res > 0) {
                UserAccountRecord userAccountRecord = new UserAccountRecord();
                userAccountRecord.setAccountId(userAccount.getId());
                userAccountRecord.setMoney(BigDecimal.valueOf(userAccountDto.getAmount()));
                userAccountRecord.setCreateTime(new Date());
                userAccountRecordMapper.insert(userAccountRecord);
                log.info("提现成功:{}元", userAccountDto.getAmount());
            }
        } else {
            throw new RuntimeException("账户不存在或余额不足！");
        }
    }

    /**
     * 悲观锁处理   悲观锁何时会释放?当一个事物处理完毕就会释放  单独的一个方法就相当于一个事物
     *
     * @param userAccountDto
     */
    public void withdrawWithLockNegative(UserAccountDto userAccountDto) {
        UserAccount userAccount = userAccountMapper.selectByUserIdLock(userAccountDto.getUserId());
        if (userAccount != null && userAccount.getAmount().doubleValue() >= userAccountDto.getAmount()) {
            // 从数据库查是否有对应的记录
            int res = userAccountMapper.updateAmountLock(userAccountDto.getAmount(), userAccount.getId());
            // 更新提现记录
            if (res > 0) {
                UserAccountRecord userAccountRecord = new UserAccountRecord();
                userAccountRecord.setAccountId(userAccount.getId());
                userAccountRecord.setMoney(BigDecimal.valueOf(userAccountDto.getAmount()));
                userAccountRecord.setCreateTime(new Date());
                userAccountRecordMapper.insert(userAccountRecord);
                log.info("提现成功:{}元", userAccountDto.getAmount());
            }
        } else {
            throw new RuntimeException("悲观锁处理===>账户不存在或余额不足！");
        }
    }
}
