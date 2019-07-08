package com.enjoy.strategy.pay;

public class Context {

    public String pay(Integer payType, String orderId) throws Exception{
        StrategyFactory strategyFactory = StrategyFactory.getInstance();
        Strategy strategy = strategyFactory.create(payType);
        return strategy.pay(payType,orderId);
    }


}
