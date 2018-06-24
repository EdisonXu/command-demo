package com.ex.commanddemo.concurrent.handler.db;


import com.ex.commanddemo.concurrent.command.BatchDbCommand;
import com.ex.commanddemo.concurrent.command.Command;
import com.ex.commanddemo.concurrent.command.ExecutionResult;
import com.ex.commanddemo.concurrent.events.EventBus;
import com.ex.commanddemo.concurrent.handler.CommandHandler;
import com.ex.commanddemo.concurrent.handler.CommandHandlerContext;
import com.ex.commanddemo.config.command.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Created by edison
 * On 2018/4/9 20:22
 */
public class BatchDbCommandCommandHandler implements CommandHandler<BatchDbCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchDbCommandCommandHandler.class);


	public BatchDbCommandCommandHandler() {
	}

    @Override
    public ExecutionResult on(BatchDbCommand command, EventBus eventBus, CommandHandlerContext context) {
        TransactionStatus status = null;
        CommandConfig commandConfig = context.getCommandConfig();
        PlatformTransactionManager platformTransactionManager = commandConfig.getPlatformTransactionManager();
        try {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            status = platformTransactionManager.getTransaction(def);
            ExecutionResult result = ExecutionResult.successResult();
            command.getCommandList().forEach(c->{
                ExecutionResult singleResult = context.getCommandDispatcher().dispatchInCurrThread(c);
                if(singleResult==null)
                    result.setResultCode(ExecutionResult.Code.FAILED);
                else if(!singleResult.getResultCode().equals(ExecutionResult.Code.SUCCESS))
                    result.setResultCode(singleResult.getResultCode(), singleResult.getMsg());
            });
            if(result.getResultCode().equals(ExecutionResult.Code.SUCCESS)) {
                platformTransactionManager.commit(status);
            } else {
                rollback(command, status,platformTransactionManager);
                LOGGER.error("QueuedDbCommandHandler result.ResultCode!=SUCCESS result:{};  rollback command",result);
            }
            return result;
        }catch (Exception e){
            rollback(command, status,platformTransactionManager);
            throw e;
        }
    }

    private void rollback(Command command, TransactionStatus status, PlatformTransactionManager platformTransactionManager){
        LOGGER.error("Failed to execute command {}!", command);
        if(status!=null)
            platformTransactionManager.rollback(status);
    }

}
