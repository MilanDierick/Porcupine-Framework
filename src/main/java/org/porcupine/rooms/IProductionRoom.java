/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.rooms;

public interface IProductionRoom {
	/**
	 * @return the efficiency of the workers, influences how long it takes to produce a product.
	 */
	float getWorkerEfficiency();
	
	/**
	 * @return the output efficiency of the room.
	 */
	float getOutputEfficiency();
	
	/**
	 * @return the increase in production amount per worker.
	 */
	float getOutputProductivity();
	
	/**
	 * @return the current amount of workers in the room.
	 */
	int getCurrentWorkers();
	
	/**
	 * @return the maximum workers able to work in this room.
	 */
	int getMaxWorkers();
	
	/**
	 * @return the input tile of the room.
	 */
	Tile getInputTile();
	
	/**
	 * @return the output tile of the room.
	 */
	Tile getOutputTile();
}
