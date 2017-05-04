package com.fosun.fc.projects.creepers.exception;

/**
 * Service层公用的Exception.
 * 
 * 继承自RuntimeException, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 */
public class CreeperException extends RuntimeException {

	private static final long serialVersionUID = 3583566093089790852L;

	public CreeperException() {
		super();
	}

	public CreeperException(String message) {
		super(message);
	}

	public CreeperException(Throwable cause) {
		super(cause);
	}

	public CreeperException(String message, Throwable cause) {
		super(message, cause);
	}
}
