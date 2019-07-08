package com.enjoy.strategy.handle;

import com.enjoy.strategy.entity.OrderDTO;

public  abstract class AbstractHandler {
    abstract public String handle(OrderDTO dto);

}
