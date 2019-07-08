package com.enjoy.strategy.handle;

import com.enjoy.strategy.entity.OrderDTO;
import org.springframework.stereotype.Component;

@Component
@HandlerType("1")
public class NormalHandler extends AbstractHandler {

    public String handle(OrderDTO dto) {
        return "处理普通订单";
    }
}
