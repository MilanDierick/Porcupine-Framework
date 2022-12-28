/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.rooms;

import org.porcupine.utilities.Vector2D;

import java.util.List;

/**
 * This is the base interface for all rooms. It contains common functionality and procedures.
 */
public interface IRoom {
	void update();
	void render();
	Vector2D<Integer> getLocation();
	List<Tile> getTiles();
}
