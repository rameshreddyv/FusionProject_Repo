/**
 * Copyright (c) 2014-15 GT. All rights reserved.
 */
package com.gt.core;

public class AppException extends RuntimeException {

	private String message;

	public AppException(String message) {
		super(message);
		this.message = message;
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
