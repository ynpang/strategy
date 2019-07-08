package com.enjoy.strategy.service;

import com.enjoy.strategy.entity.OrderDTO;
import com.enjoy.strategy.handle.AbstractHandler;
import com.enjoy.strategy.handle.HandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceV2Impl implements IOrderService {

    @Autowired
    private HandlerContext handlerContext;

    @Override
    public String handle(OrderDTO dto) {
        AbstractHandler handler = handlerContext.getInstance(dto.getType());
        return handler.handle(dto);
    }

}
