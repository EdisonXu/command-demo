package com.ex.commanddemo.concurrent.exception;

/**
 * Created by edison
 * On 2018/4/4 15:56
 */
public class NoEnoughInventoryException extends RuntimeException {

	public NoEnoughInventoryException(String message) {
		super(message);
	}
}
