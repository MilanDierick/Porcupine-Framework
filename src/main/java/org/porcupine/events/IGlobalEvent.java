/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;


public interface IGlobalEvent {
	/**
	 * This method is called when the event occurs.
	 */
	void onEvent();
	
	/**
	 * @return the chance that this event will occur per in-game second.
	 */
	float chancePerSecond();
}
