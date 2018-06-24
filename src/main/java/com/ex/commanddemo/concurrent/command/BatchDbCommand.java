package com.ex.commanddemo.concurrent.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edison
 * On 2018/5/15 22:19
 */
public class BatchDbCommand implements Command{

    private List<Command> commandList = new ArrayList<>();

    public List<Command> getCommandList() {
        return commandList;
    }

    public void add(Command command){
        commandList.add(command);
    }

    public BatchDbCommand() {
    }

    public BatchDbCommand(Command... commands) {
        for(Command each: commands){
            if(each!=null)
                commandList.add(each);
        }
    }

    public void addAll(List<Command> commands){
        this.commandList.addAll(commands);
    }

}
