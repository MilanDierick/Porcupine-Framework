/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

public class InvalidAggregateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidAggregateException(String message) {
		super(message);
	}
}
