/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.rooms;

import org.porcupine.utilities.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class ProductionRoom extends Room implements IProductionRoom {
	protected float workerEfficiency;
	protected float outputEfficiency;
	protected float outputProductivity;
	protected int currentWorkers;
	protected final int maxWorkers;
	
	protected Tile inputTile;
	protected Tile outputTile;
	
	protected final List<Resource> inputResources;
	protected final List<Resource> outputResources;
	
	protected final FurnitureType furnitureStatWorkers;
	protected final FurnitureType furnitureStatEfficiency;
	protected final FurnitureType furnitureStatProductivity;
	
	/**
	 * @param location                  the location of the room.
	 * @param tiles                     the tiles of the room.
	 * @param maxWorkers                the maximum amount of workers that can work in the room.
	 * @param inputResources            the input resources of the room.
	 * @param outputResources           the output resources of the room.
	 * @param furnitureStatWorkers      the furniture type that influences the worker efficiency stat.
	 * @param furnitureStatEfficiency   the furniture type that influences the efficiency stat.
	 * @param furnitureStatProductivity the furniture type that influences the productivity stat.
	 *
	 * @apiNote Do not initialize this class directly, use the {@link ProductionRoomBuilder} instead.
	 */
	public ProductionRoom(
			Vector2D<Integer> location,
			ArrayList<? extends Tile> tiles,
			int maxWorkers,
			ArrayList<Resource> inputResources,
			ArrayList<Resource> outputResources,
			FurnitureType furnitureStatWorkers,
			FurnitureType furnitureStatEfficiency,
			FurnitureType furnitureStatProductivity
	) {
		super(location, tiles);
		this.maxWorkers = maxWorkers;
		this.inputResources = new ArrayList<>(inputResources);
		this.outputResources = new ArrayList<>(outputResources);
		this.furnitureStatWorkers = furnitureStatWorkers;
		this.furnitureStatEfficiency = furnitureStatEfficiency;
		this.furnitureStatProductivity = furnitureStatProductivity;
		
		calculateMaxWorkers();
		calculateFurnitureStats();
		
		recalculateWorkerEfficiency();
		recalculateOutputEfficiency();
		recalculateOutputProductivity();
	}
	
	private void calculateFurnitureStats() {
	
	}
	
	private void calculateMaxWorkers() {
	
	}
	
	private void recalculateWorkerEfficiency() {
	
	}
	
	private void recalculateOutputEfficiency() {
	
	}
	
	private void recalculateOutputProductivity() {
	
	}
	
	@Override
	public float getWorkerEfficiency() {
		recalculateWorkerEfficiency();
		return workerEfficiency;
	}
	
	@Override
	public float getOutputEfficiency() {
		return outputEfficiency;
	}
	
	@Override
	public float getOutputProductivity() {
		return outputProductivity;
	}
	
	@Override
	public int getCurrentWorkers() {
		return currentWorkers;
	}
	
	@Override
	public int getMaxWorkers() {
		return maxWorkers;
	}
	
	@Override
	public Tile getInputTile() {
		return inputTile;
	}
	
	@Override
	public Tile getOutputTile() {
		return outputTile;
	}
}
