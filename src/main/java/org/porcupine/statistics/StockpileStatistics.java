/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.statistics;

import game.faction.FACTIONS;
import init.resources.RESOURCE;
import init.resources.RESOURCES;
import settlement.main.SETT;
import settlement.room.infra.stockpile.StockpileTally;

/**
 * @author Milan Dierick
 */
public class StockpileStatistics implements IStats {
	private final ResourceMetadataCache cache = new ResourceMetadataCache();
	
	@Override
	public void refresh() {
		cache.clear();
		
		StockpileTally tally = SETT.ROOMS().STOCKPILE.tally();
		
		for (RESOURCE resource : RESOURCES.ALL()) {
			int buyPrice = FACTIONS.player().credits().pricesBuy.get(resource);
			int sellPrice = FACTIONS.player().credits().pricesSell.get(resource);
			int stockpile = tally.amountTotal(resource);
			
			cache.put(resource, new ResourceMetadata(resource, buyPrice, sellPrice, stockpile));
		}
	}
	
	/**
	 * @param key The resource to get the metadata for.
	 *
	 * @return the metadata for the given resource.
	 */
	public ResourceMetadata get(RESOURCE key) {
		return cache.get(key);
	}
}
