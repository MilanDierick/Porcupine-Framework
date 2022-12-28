/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.rooms;

import org.porcupine.utilities.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room implements IRoom {
	protected final Vector2D<Integer> location;
	protected final ArrayList<Tile> tiles;
	
	public Room(Vector2D<Integer> location, ArrayList<? extends Tile> tiles) {
		this.location = location;
		this.tiles = new ArrayList<>(tiles);
	}
	
	/**
	 *
	 */
	@Override
	public void update() {
	
	}
	
	/**
	 *
	 */
	@Override
	public void render() {
	
	}
	
	/**
	 * @return the location of the room.
	 */
	@Override
	public Vector2D<Integer> getLocation() {
		return location;
	}
	
	/**
	 * @return the tiles of the room.
	 */
	@Override
	public List<Tile> getTiles() {
		return Collections.unmodifiableList(tiles);
	}
}
