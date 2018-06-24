package com.ex.commanddemo.config.command;

import com.ex.commanddemo.concurrent.annotation.EventHandler;
import com.ex.commanddemo.concurrent.events.CommandEvent;
import com.ex.commanddemo.concurrent.events.SimpleEventBus;
import org.slf4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * The registry information stored all &lt;CommandEvent, EventHandler&gt; pairs.
 * <p>It dynamically finds all the classes having methods annotated with {@link EventHandler}, and register them into the registry.
 * Supports both POJO & Spring Bean.
 *
 *
 * @author edison
 * @
 * On 2018/5/15 14:21
 */
@Component
public class EventHandlerRegistry {

    private static final Logger LOGGER = getLogger(EventHandlerRegistry.class);
    private Map<Class, List<EventHandlerSubcriber>> subcriberMap = new ConcurrentHashMap<>();
    private static final String BASE_PACKAGE = "com.chimestone";
    //private static final Pattern packagePattern = Pattern.compile(BASE_PACKAGE);

    public EventHandlerRegistry() {
    }

    public List<EventHandlerSubcriber> getSubscriber(CommandEvent event){
        return subcriberMap.get(event.getClass());
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event){
        findAllAnnotationedMethodHandlers(event.getApplicationContext());
    }

    public void findAllAnnotationedMethodHandlers(ApplicationContext context){
        LOGGER.debug("Begin to find all event handlers");
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
        Set<BeanDefinition> set = provider.findCandidateComponents(BASE_PACKAGE);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (BeanDefinition beanDefinition : set) {
            try {
                Class<?> loadClass = classLoader.loadClass(beanDefinition.getBeanClassName());
                for(Method m: loadClass.getDeclaredMethods()){
                    // check if any method annotated with EventHandler
                    if (m.isAnnotationPresent(EventHandler.class)) {
                        Class<?>[] parameterTypes =  m.getParameterTypes();
                        if(parameterTypes.length==0 || parameterTypes.length>1)
                            throw new IllegalArgumentException("Eventhandler can only have one parameter as the event it receives");
                        Class<?> eventType = parameterTypes[0];
                        Object handlerInstance = null;
                        try {
                            handlerInstance = context.getBean(loadClass);
                        } catch (NoSuchBeanDefinitionException e) {
                            handlerInstance = loadClass.newInstance();
                        }
                        List subcribers = subcriberMap.get(eventType);
                        if(subcribers==null)
                            subcribers = new ArrayList();
                        subcribers.add(new EventHandlerSubcriber(handlerInstance, m));
                        subcriberMap.put(eventType, subcribers);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        /*for (String beanName : context.getBeanDefinitionNames()) {
            Object obj = context.getBean(beanName);
            *//*
             * As you are using AOP check for AOP proxying. If you are proxying with Spring CGLIB (not via Spring AOP)
             * Use org.springframework.cglib.proxy.Proxy#isProxyClass to detect proxy If you are proxying using JDK
             * Proxy use java.lang.reflect.Proxy#isProxyClass
             *//*
            Class<?> objClz = obj.getClass();
            Package pkg = objClz.getPackage();
            if(pkg==null)
                continue;
            String pkgName = pkg.getName();
            if(!pkgName.startsWith(BASE_PACKAGE) || this.getClass().getName().equals(beanName))
                continue;
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
            }

            for (Method m : objClz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(EventHandler.class)) {
                    Class<?>[] parameterTypes = m.getParameterTypes();
                    if(parameterTypes.length==0 || parameterTypes.length>1)
                        throw new IllegalArgumentException("Eventhandler can only have one parameter as the event it receives");
                    Class<?> eventType = parameterTypes[0];
                    synchronized (subcriberMap){
                        List subcribers = subcriberMap.get(eventType);
                        if(subcribers==null)
                            subcribers = new ArrayList();
                        subcribers.add(new EventHandlerSubcriber(obj, m));
                        subcriberMap.put(eventType, subcribers);
                    }
                }
            }
        }*/
        context.getBean(SimpleEventBus.class).setRegistry(this);
        LOGGER.debug("Find event handlers: {}", subcriberMap);
    }

    public static class EventHandlerSubcriber{
        Object handler;
        Method handleMethod;

        public EventHandlerSubcriber(Object handler, Method handleMethod) {
            this.handler = handler;
            this.handleMethod = handleMethod;
        }

        @Override
        public String toString() {
            return "EventHandlerSubcriber{" +
                "handler=" + handler +
                ", handleMethod=" + handleMethod +
                '}';
        }

        public Object getHandler() {
            return handler;
        }

        public Method getHandleMethod() {
            return handleMethod;
        }
    }

}
