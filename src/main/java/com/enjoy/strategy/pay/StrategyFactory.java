package com.enjoy.strategy.pay;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Set;

public class StrategyFactory {
    private static StrategyFactory strategyFactory = new StrategyFactory();
    private StrategyFactory(){}

    //map维护的是 支付渠道对应的具体实现类
    private static HashMap<Integer, String> source_map = new HashMap<>();

    //    static {
//        source_map.put(1,"mmall.pay.impl.ICBCBankImpl");
//        source_map.put(2,"mmall.pay.impl.ICBCBankImpl");
//    }
    static {
        Reflections reflections = new Reflections("com.macro.mall.pay.impl");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(Pay.class);
        for(Class clazz:classSet){
            Pay pay = (Pay) clazz.getAnnotation(Pay.class);
            source_map.put(pay.payType(),clazz.getCanonicalName());
        }
    }

    //生成具体的实现类
    public Strategy create(int channelId) throws Exception{
        String clazz = source_map.get(channelId);
        Class clazz_ = Class.forName(clazz);
        return (Strategy) clazz_.newInstance();
    }

    public static StrategyFactory getInstance(){
        return strategyFactory;
    }
}
