package com.enjoy.strategy.pay;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Set;

@Service
public class BeanUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }
    public static Object getBean(String name) throws BeansException {
        System.out.println("applicationContext = " + applicationContext);
        return applicationContext.getBean(name);
    }

    /*
     * 把继承该类的类成员变量（通过spring 管理的bean)注入到继承的类中
     */
    public BeanUtils(){
        //加载继承该类的类，扫描成员变量
        Reflections reflections = new Reflections(this.getClass(),new FieldAnnotationsScanner());
        //将所有含有Resource注解的成员变量 扫描出来
        Set<Field> fields = reflections.getFieldsAnnotatedWith(javax.annotation.Resource.class);
        //循环变量成员变量
        for(Field f:fields){
            //循环的目的就是把这些为null的不被spring管理的注入对象，手动从applicationContext取出来，然后设置进。
            try{
                String simpleName = f.getType().getSimpleName();
                String beanName = toLowerCaseFirstLetter(simpleName);
                Object bean = applicationContext.getBean(beanName);
                if(bean == null)
                    return;
                f.setAccessible(true);
                f.set(this, bean);
            }catch (Exception e){
                e.getMessage();
            }
        }
    }

    private String toLowerCaseFirstLetter(String s) {
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
