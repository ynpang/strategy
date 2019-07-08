# 如何在 Spring Boot 中妙用策略模式

### 需求
这里虚拟一个业务需求，让大家容易理解。假设有一个订单系统，里面的一个功能是根据订单的不同类型作出不同的处理。
### 传统实现
    根据订单类型写一堆的if else：
### 策略模式实现
利用策略模式，只需要两行即可实现业务逻辑：
public class OrderServiceV2Impl implements IOrderService{

    @Autowired
    private HandlerContext handlerContext;

    @Override
    public String handle(OrderDTO dto) {
        AbstractHandler handler = handlerContext.getInstance(dto.getType());
        return handler.handle(dto);
    }
}
可以看到上面的方法中注入了HandlerContext，这是一个处理器上下文，用来保存不同的业务处理器，具体在下文会讲解。我们从中获取一个抽象的处理器AbstractHandler，调用其方法实现业务逻辑。
现在可以了解到，我们主要的业务逻辑是在处理器中实现的，因此有多少个订单类型，就对应有多少个处理器。以后需求变化，增加了订单类型，只需要添加相应的处理器就可以，上述OrderServiceV2Impl完全不需改动。
@Component
@HandlerType("1")
public class NormalHandler extends AbstractHandler {

    public String handle(OrderDTO dto) {
        return "处理普通订单";
    }
}
首先每个处理器都必须添加到spring容器中，因此需要加上@Component注解，其次需要加上一个自定义注解@HandlerType，用于标识该处理器对应哪个订单类型，最后就是继承AbstractHandler，实现自己的业务逻辑。
自定义注解 @HandlerType：
抽象处理器 AbstractHandler：

自定义注解和抽象处理器都很简单，那么如何将处理器注册到spring容器中呢？

具体思路是：

1、扫描指定包中标有@HandlerType的类；

2、将注解中的类型值作为key，对应的类作为value，保存在Map中；

3、以上面的map作为构造函数参数，初始化HandlerContext，将其注册到spring容器中；

我们将核心的功能封装在HandlerProcessor类中，完成上面的功能。

HandlerProcessor：
@Component
@SuppressWarnings("unchecked")
public class HandlerProcessor implements BeanFactoryPostProcessor{

    private static final String HANDLER_PACKAGE = "com.enjoy.strategy.handle.impl";

    /**
     * 扫描@HandleTpe,初始化HandlerContext,将其注册到spring容器
     * @param beanFactory bean工厂
     * @see HandlerType
     * @see HandlerContext
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map<String, Class> handlerMap = Maps.newHashMapWithExpectedSize(3);
        ClassScaner.scan(HANDLER_PACKAGE, HandlerType.class).forEach(clazz -> {
            //获取注解中的类型值
            String type = clazz.getAnnotation(HandlerType.class).value();
            //将注解中的类型值作为key,对应的类作为value,保存到Map中
            handlerMap.put(type,clazz);
        });
        //初始化HandlerContext,将其注册到spring容器中
        HandlerContext context = new HandlerContext(handlerMap);
        beanFactory.registerSingleton(HandlerContext.class.getName(), context);
    }
}
### ClassScanner：扫描工具类源码
HandlerProcessor需要实现BeanFactoryPostProcessor，在spring处理bean前，将自定义的bean注册到容器中。
核心工作已经完成，现在看看HandlerContext如何获取对应的处理器：
HandlerContext：
public class HandlerContext {

    private Map<String, Class> handlerMap;

    public HandlerContext(Map<String, Class> handlerMap){
        this.handlerMap = handlerMap;
    }

    public AbstractHandler getInstance(String type){
        Class clazz = handlerMap.get(type);
        if(clazz == null){
            throw new IllegalArgumentException("not found handler for type:" + type);
        }
        return (AbstractHandler) BeanTool.getBean(clazz);
    }
}

BeanTool：获取bean工具类
 getInstance 方法根据类型获取对应的class，然后根据class类型获取注册到spring中的bean。
最后请注意一点，HandlerProcessor和BeanTool必须能被扫描到，或者通过@Bean的方式显式的注册，才能在项目启动时发挥作用。
### 总结
利用策略模式可以简化繁杂的if else代码，方便维护，而利用自定义注解和自注册的方式，可以方便应对需求的变更。本文只是提供一个大致的思路，还有很多细节可以灵活变化，例如使用枚举类型、或者静态常量，作为订单的类型，相信你能想到更多更好的方法。


