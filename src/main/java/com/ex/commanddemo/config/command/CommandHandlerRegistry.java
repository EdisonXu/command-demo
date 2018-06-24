package com.ex.commanddemo.config.command;

import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.handler.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by edison
 * On 2018/5/15 17:33
 */
public class CommandHandlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlerRegistry.class);
    private static String BASE_PACKAGE_NAME = "com.ex.commanddemo.concurrent.handler";

    private Map<Class, CommandHandler> registeredHandlers = new ConcurrentHashMap<>();

    public CommandHandlerRegistry() throws Exception {
        loadAllHandler();
    }

    public void loadAllHandler() throws Exception{
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        //provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
        provider.addIncludeFilter(new AssignableTypeFilter(CommandHandler.class));
        Set<BeanDefinition> set = provider.findCandidateComponents(BASE_PACKAGE_NAME);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (BeanDefinition beanDefinition : set) {
            Class<?> loadClass = classLoader.loadClass(beanDefinition.getBeanClassName());
            CommandHandler commandHandler = (CommandHandler) loadClass.newInstance();
            for(Type type: loadClass.getGenericInterfaces()){
                if(type instanceof ParameterizedType){
                    ParameterizedType pType = (ParameterizedType)type;
                    if(pType.getRawType().getTypeName().equals(CommandHandler.class.getName())) {
                        String commandName = (pType.getActualTypeArguments()[0]).getTypeName();
                        Class commandClass = classLoader.loadClass(commandName);
                        registeredHandlers.put(commandClass, commandHandler);
                    }
                }
            }
        }
        LOGGER.debug("Registered command handlers: "+registeredHandlers.size());
    }

    public CommandHandler find(Command command){
        CommandHandler handler = registeredHandlers.get(command.getClass());
        if(handler==null)
            LOGGER.error("Unable to find command handler for command {}", command.getClass().getSimpleName());
        return handler;
    }
}
