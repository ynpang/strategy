package com.enjoy.strategy.pay.impl;

import com.enjoy.strategy.pay.BeanUtils;
import com.enjoy.strategy.pay.Pay;
import com.enjoy.strategy.pay.Strategy;
import lombok.extern.slf4j.Slf4j;

@Pay(payType = 1)
@Slf4j
public class AmoutStrategy extends BeanUtils implements Strategy {
    @Override
    public String pay(Integer payType, String orderId) {
        log.info("余额支付");
        return "余额支付";
    }
}
