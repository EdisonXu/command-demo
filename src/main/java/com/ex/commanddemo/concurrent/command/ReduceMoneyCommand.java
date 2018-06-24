package com.ex.commanddemo.concurrent.command;

import com.ex.commanddemo.domain.ReduceMoneyEvent;

/**
 * @author edison
 * On 2018/6/22 17:19
 */
public class ReduceMoneyCommand implements Command{

	private ReduceMoneyEvent event;

	public ReduceMoneyCommand() {
	}

	public ReduceMoneyCommand(ReduceMoneyEvent event) {
		this.event = event;
	}

	public ReduceMoneyEvent getEvent() {
		return event;
	}

	public void setEvent(ReduceMoneyEvent event) {
		this.event = event;
	}
}
