/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.rooms;

/**
 * The primary type of room. A room can fulfill secondary roles as well, but construction category this room belongs
 * to is the primary role.
 */
public enum RoomType {
	DECORATION, PRODUCTION, RESIDENTIAL, SERVICE, UTILITY,
}
