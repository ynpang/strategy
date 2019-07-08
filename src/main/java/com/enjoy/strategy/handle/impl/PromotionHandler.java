package com.enjoy.strategy.handle.impl;

import com.enjoy.strategy.entity.OrderDTO;
import com.enjoy.strategy.handle.AbstractHandler;
import com.enjoy.strategy.handle.HandlerType;
import org.springframework.stereotype.Component;

@Component
@HandlerType("1")
public class PromotionHandler extends AbstractHandler {

    public String handle(OrderDTO dto) {
        return "处理促销订单";
    }
}