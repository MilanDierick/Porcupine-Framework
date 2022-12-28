/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.rooms;

import org.porcupine.utilities.Vector2D;

import java.util.ArrayList;

public class Metalsmith extends ProductionRoom {
	public Metalsmith(
			Vector2D<Integer> location,
			ArrayList<? extends Tile> tiles,
			int maxWorkers,
			ArrayList<Resource> inputResources,
			ArrayList<Resource> outputResources,
			FurnitureType furnitureStatWorkers,
			FurnitureType furnitureStatEfficiency,
			FurnitureType furnitureStatProductivity
	) {
		super(
				location,
				tiles,
				maxWorkers,
				inputResources,
				outputResources,
				furnitureStatWorkers,
				furnitureStatEfficiency,
				furnitureStatProductivity
		);
	}
	
	@Override
	public void update() {
	
	}
	
	@Override
	public void render() {
	
	}
}
