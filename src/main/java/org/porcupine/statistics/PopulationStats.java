/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.statistics;

import settlement.stats.STATS;

public class PopulationStats implements IStats {
	private int currentPopulationCount;
	private int currentNobelCount;
	private int currentEmigratingCount;
	private int currentOwnedSlaves;
	private int currentForeignSlaves;
	private int currentWrongfulDeaths;
	
	@Override
	public void refresh() {
		currentPopulationCount = STATS.POP().POP.data().get(null);
		currentNobelCount = STATS.POP().NOBLES.data().get(null);
		currentEmigratingCount = STATS.POP().EMMIGRATING.data().get(null);
		currentOwnedSlaves = STATS.POP().SLAVES_SELF.data().get(null);
		currentForeignSlaves = STATS.POP().SLAVES_OTHER.data().get(null);
		currentWrongfulDeaths = STATS.POP().WRONGFUL.data().get(null);
	}
	
	public int getCurrentPopulationCount() {
		return currentPopulationCount;
	}
	
	public int getCurrentNobelCount() {
		return currentNobelCount;
	}
	
	public int getCurrentEmigratingCount() {
		return currentEmigratingCount;
	}
	
	public int getCurrentOwnedSlaves() {
		return currentOwnedSlaves;
	}
	
	public int getCurrentForeignSlaves() {
		return currentForeignSlaves;
	}
	
	public int getCurrentWrongfulDeaths() {
		return currentWrongfulDeaths;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		PopulationStats other = (PopulationStats) obj;
		
		if (currentPopulationCount != other.currentPopulationCount)
			return false;
		if (currentNobelCount != other.currentNobelCount)
			return false;
		if (currentEmigratingCount != other.currentEmigratingCount)
			return false;
		if (currentOwnedSlaves != other.currentOwnedSlaves)
			return false;
		if (currentForeignSlaves != other.currentForeignSlaves)
			return false;
		return currentWrongfulDeaths == other.currentWrongfulDeaths;
	}
	
	@Override
	public int hashCode() {
		int result = currentPopulationCount;
		result = 31 * result + currentNobelCount;
		result = 31 * result + currentEmigratingCount;
		result = 31 * result + currentOwnedSlaves;
		result = 31 * result + currentForeignSlaves;
		result = 31 * result + currentWrongfulDeaths;
		return result;
	}
}
