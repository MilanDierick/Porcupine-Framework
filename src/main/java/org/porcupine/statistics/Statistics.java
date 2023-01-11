/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.statistics;

/**
 * This interface is used as a wrapper for all statistics.
 *
 * @author Milan Dierick
 * @implNote Internally, this interface uses a HashSet to store all statistic objects. This is done to allow for other
 * statistics to be added at runtime, for example by other modules. The framework provides accessors for all vanilla
 * statistics, but other statistics can be added using the {@link #put(Class, IStats)} method.
 */
// TODO: The statistics need to be saved and loaded, as to not having to recalculate statistics when loading the game.
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public final class Statistics {
	private static final StatisticsCategoryCache cache = new StatisticsCategoryCache();
	
	static {
		put(PopulationStats.class, new PopulationStats());
		put(StockpileStatistics.class, new StockpileStatistics());
	}
	
	/**
	 * @apiNote Setting this constructor to private prevents the class from being erroneously instantiated.
	 */
	private Statistics() {
	}
	
	/**
	 * @param key   The class type of the statistics object to insert.
	 * @param value The statistic to put.
	 */
	public static void put(Class<? extends IStats> key, IStats value) {
		cache.put(key, value);
	}
	
	public static void refreshAllStats() {
		for (IStats stats : cache.values()) {
			stats.refresh();
		}
	}
	
	/**
	 * This method is used to get a statistics object for which a specific accessor doesn't exist.
	 *
	 * @param key The class of the statistic to retrieve.
	 *
	 * @return the statistic object.
	 */
	public static <Type extends IStats> Type get(Class<Type> key) {
		return key.cast(cache.get(key));
	}
	
	/**
	 * @return the population statistics.
	 */
	public static PopulationStats getPopulationStats() {
		return (PopulationStats) cache.get(PopulationStats.class);
	}
	
	/**
	 * @return the stockpile statistics.
	 */
	public static StockpileStatistics getStocksStatistics() {
		return (StockpileStatistics) cache.get(StockpileStatistics.class);
	}
}
