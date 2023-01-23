/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import settlement.room.main.Room;

public interface IRoomEvent {
	void onEvent(Room room);
	
	float chancePerSecond();
	
	/**
	 * @return the name of the building that this event is associated with.
	 */
	String getRoomType();
}
