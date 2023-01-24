/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import settlement.room.main.*;

public interface IRoomEvent {
	/**
	 * This method is called when the event occurs.
	 *
	 * @param instance the instance of the type of room that this event is associated with.
	 */
	void onEvent(RoomInstance instance);
	
	/**
	 * @return the chance that this event will occur per in-game second.
	 */
	float chancePerSecond();
	
	/**
	 * @return the name of the building that this event is associated with.
	 */
	RoomBlueprintIns<?> getRoomType();
}
