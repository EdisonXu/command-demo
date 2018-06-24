package com.ex.commanddemo.config.command;

import com.ex.commanddemo.concurrent.message.CommandMessage;
import com.ex.commanddemo.concurrent.message.MessageQueue;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Edison
 * On 2018/3/25 23:49
 */
public class CommandConfig {

	private static final Logger LOGGER = getLogger(CommandConfig.class);

	private PlatformTransactionManager platformTransactionManager;
	private ApplicationContext context;
	private CommandHandlerRegistry commandHandlerRegistry;
	private MessageQueue<CommandMessage> messageQueue;
	private ApplicationEventPublisher springEventPublisher;

	public CommandConfig(PlatformTransactionManager platformTransactionManager,
                         ApplicationContext context,
                         CommandHandlerRegistry commandHandlerRegistry,
                         MessageQueue<CommandMessage> messageQueue,
                         ApplicationEventPublisher eventPublisher){
		this.platformTransactionManager = platformTransactionManager;
		this.context = context;
		this.commandHandlerRegistry = commandHandlerRegistry;
		this.messageQueue = messageQueue;
		this.springEventPublisher = eventPublisher;
	}

	/*public CommandConfig register(Long userId,Command command, Class<? extends CommandHandler> handlerClz) {
		String groupId = command.getGroupId(userId);
		try {
			//lock.tryLock(1, TimeUnit.SECONDS);
            GroupedQueue origin = groupedQueue.get(groupId);
            if(origin==null){
                groupedQueue.put(groupId, new GroupedQueue(groupId));
            }else{
                LOGGER.debug("Group queue {} already exists", groupId);
            }
		} catch (Exception e){
			LOGGER.error("Failed to register {} with {}", command.getClass(), handler, e);
			throw new IllegalArgumentException(e);
		}*//*finally {
			lock.unlock();
        }*//*
		return this;
	}
*/
	/*public Map<Class, CommandHandler> loadCommand() throws Exception{
		Map<Class, CommandHandler> map = new ConcurrentHashMap<>();
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
		Set<BeanDefinition> set = provider.findCandidateComponents(commandPackageName);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for (BeanDefinition beanDefinition : set) {
			Class<?> loadClass = classLoader.loadClass(beanDefinition.getBeanClassName());
			// 通过反射机制获取子类传递过来的实体类的类型信息
	        ParameterizedType type = (ParameterizedType) loadClass.getGenericSuperclass();
			Class tClass = (Class<?>) type.getActualTypeArguments()[0];
			map.put(loadClass, handler.get(tClass));
		}
		return map;
	}*/

	public ApplicationContext getContext() {
		return context;
	}

	public PlatformTransactionManager getPlatformTransactionManager() {
		return platformTransactionManager;
	}

    public CommandHandlerRegistry getCommandHandlerRegistry() {
        return commandHandlerRegistry;
    }

    public MessageQueue<CommandMessage> getMessageQueue() {
        return messageQueue;
    }

    public ApplicationEventPublisher getSpringEventPublisher() {
        return springEventPublisher;
    }
}
