/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import game.faction.FACTIONS;
import init.resources.RESOURCE;
import init.resources.RESOURCES;
import org.porcupine.modules.IScriptEntity;
import org.porcupine.modules.ITickCapable;
import org.porcupine.statistics.ResourceMetadata;
import org.porcupine.statistics.Statistics;
import org.porcupine.statistics.StockpileStatistics;
import org.porcupine.utilities.Logger;
import settlement.stats.STATS;
import snake2d.util.datatypes.COORDINATE;
import util.data.BOOLEAN_OBJECT;
import view.main.VIEW;
import view.sett.IDebugPanelSett;
import world.World;
import world.army.WARMYD;
import world.entity.WPathing;
import world.entity.army.WArmy;
import world.map.regions.Region;

// TODO: Calculate how much resources the raiding army receives to construct it's divisions.
// TODO: Calculate how much it costs to equip one pawn with a melee / ranged weapon and armor, per tier of equipment.
// TODO: Calculate the estimate worth of a pawn, based on it's resource consumption over a certain amount of time.
// TODO: Create a popup system, similar to the current Protection event.
// TODO: When the player fails to pay up, implement the raiding army to route to the player's settlement and attack.
// TODO: Calculate the chance each tick that a reading army will spawn. Might just use the game's chance calculation.

/**
 * In the current raider event, the soldier count is based on a very simple formula: garrison size + total settlement
 * population / 30 + soldier count of all armies in the target region of the raiding army + 0.5 * soldier count of all
 * player armies in player-owned regions + 0.25 * soldier count of all player armies in neutral regions
 * <p>
 * If we assume that the player does not have any armies anywhere, the settlement population is 500, and the garrison
 * size is 0, the formula would be: 0 + 500 / 30 + 0 + 0.5 * 0 + 0.25 * 0 ≈ 17 soldiers in the raiding army.
 * <p>
 * This can be easily exploited by the player, as they can simply assemble 50+ soldiers when they receive the Protection
 * event, defeat the incoming army, and then disband their garrison. Collect the armor and weapons from the defeated
 * raider armies, and the player doesn't have to worry about the increasing raiding army quality due to the winning
 * scaling factor.
 * <p>
 * Instead, I'd like to base the incoming raiding army size and quality on the player's settlement wealth. The player's
 * wealth will be distributed between raider soldier count, armor and weapon quality, with scaling modifiers applied for
 * balancing. To calculate the value of a settlement pawn, we can use the resource consumption of a pawn over a certain
 * amount of time, with scaling modifiers applied to compensate for the pawn's fighting effectiveness.
 */
public class RaidEvent implements IScriptEntity, ITickCapable {
	public static final int VALUE_OF_PAWN = 2000;
	public static final int MIN_REBEL_ARMY_SIZE = 10;
	
	/**
	 * The percentage of the total targeted settlement population that will be added to the raiding army.
	 */
	public static final float SETTLEMENT_POPULATION_WEIGHT = 0.03f;
	
	/**
	 * The percentage of soldiers in a player army that resides in a player-owned region that will be added to the
	 * raiding army.
	 */
	public static final float PLAYER_REGION_WEIGHT = 0.5f;
	
	/**
	 * The percentage of soldiers in a player army that resides in a non-player-owned region that will be added to the
	 * raiding army.
	 */
	public static final float NON_PLAYER_REGION_WEIGHT = 0.25f;
	
	private int playerVictories;
	
	private int settlementPopulation;
	private int settlementWealth;
	
	private int valueOfPawn;
	private int valueOfMeleeWeapon;
	private int valueOfRangedWeapon;
	private int valueOfArmor;
	
	private int budget;
	private final float budgetUsedForPawns = 0.4f;
	private final float budgetUsedForWeapons = 0.4f;
	private final float budgetUsedForArmor = 0.2f;
	
	private final BOOLEAN_OBJECT<Region> playerFinder = t -> t.faction() == FACTIONS.player();
	
	public RaidEvent() {
		IDebugPanelSett.add("Raid Event", this::triggerRaid);
	}
	
	@Override
	public void onTick(double delta) {
		// We want to make sure that the settlement is present in the game's state.
		// This is mainly to prevent the script from looking at the settlement when it hasn't been created yet.
		// This should be checked in a more concise manner instead of checking which view is active.
		if (VIEW.current() != VIEW.s() && VIEW.current() != VIEW.b() && VIEW.current() != VIEW.world()) {
			return;
		}
		
		if (canRaidOccur()) {
			triggerRaid();
		}
	}
	
	private boolean canRaidOccur() {
		return false;
	}
	
	private void clearCache() {
		settlementPopulation = 0;
		settlementWealth = 0;
	}
	
	private void calculatePopulation() {
		settlementPopulation = STATS.POP().POP.data().get(null);
	}
	
	private void calculateWealth() {
		StockpileStatistics statistics = Statistics.get(StockpileStatistics.class);
		
		for (RESOURCE resource : RESOURCES.ALL()) {
			ResourceMetadata metadata = statistics.get(resource);
			settlementWealth += metadata.getStockpile() * metadata.getBuyPrice();
		}
	}
	
	private void calculateItemValues() {
		StockpileStatistics statistics = Statistics.get(StockpileStatistics.class);
		
		valueOfPawn = VALUE_OF_PAWN;
		
		valueOfMeleeWeapon = statistics.get(RESOURCES.map().tryGet("WEAPON")).getBuyPrice();
		valueOfRangedWeapon = statistics.get(RESOURCES.map().tryGet("BOW")).getBuyPrice();
		valueOfArmor = statistics.get(RESOURCES.map().tryGet("ARMOR")).getBuyPrice();
	}
	
	/**
	 * Checks if this region is adjacent to a region owned by the player.
	 *
	 * @param region The region where the raid will occur.
	 *
	 * @return a boolean indicating whether this region is suitable to spawn a raid in.
	 */
	private boolean isRegionSuitable(Region region) {
		// If we somehow managed to grab an ill-formed region, return false.
		if (region == null || region.area() == 0) {
			return false;
		}
		
		// If the rebel faction can't create a new army, return false.
		if (!World.ARMIES().rebels().canCreate()) {
			return false;
		}
		
		// If this region already contains a rebel army, return false.
		for (WArmy army : World.ARMIES().rebels().all()) {
			if (army.region() == region) {
				return false;
			}
		}
		
		// If this region is owned by the player, return.
		if (region.faction() == FACTIONS.player()) {
			return false;
		}
		
		return WPathing.findAdjacentRegion(region.cx(), region.cy(), playerFinder) != null;
	}
	
	/**
	 * Finds a suitable region to spawn a raid in, that is adjacent to a region owned by the player.
	 *
	 * @return a random region that is suitable to spawn a raid in.
	 *
	 * @implNote With how it is currently set up, a raid will always spawn in the same region given the player doesn't
	 * occupy new regions. This is not the intended behaviour, but it is a good starting point. Ideally, the raid should
	 * collect all regions that are suitable to spawn a raid in, and then randomly select one of those regions.
	 * <p>
	 * TODO: Implement the above.
	 */
	private Region findSuitableRegion() {
		Region region = null;
		
		for (Region r : World.REGIONS().all()) {
			if (isRegionSuitable(r)) {
				region = r;
				break;
			}
		}
		
		return region;
	}
	
	/**
	 * Calculates the number of soldiers in a rebel army for a given region.
	 *
	 * @param targetRegion The region where the raid will occur.
	 *
	 * @return the number of pawns that will be spawned in the raid.
	 *
	 * @implNote This implementation is copied from the game, using the flawed logic. This should be changed to a more
	 * sensible implementation as described in the disclaimer of {@link RaidEvent}.
	 */
	private int calculateRebelArmySoldierCount(Region targetRegion) {
		int rebelArmySoldierCount = 0;
		
		// If the target region is the player's capital region, add a number of soldiers based on the settlement's
		// population.
		if (targetRegion == FACTIONS.player().capitolRegion()) {
			rebelArmySoldierCount += settlementPopulation * SETTLEMENT_POPULATION_WEIGHT;
		}
		
		// Add soldiers to the rebel army based on the location of the player army.
		for (WArmy playerArmy : FACTIONS.player().kingdom().armies().all()) {
			if (playerArmy.region() == targetRegion) {
				rebelArmySoldierCount += WARMYD.men(null).get(playerArmy);
			} else if (playerArmy.region() != null && playerArmy.region().faction() == FACTIONS.player()) {
				rebelArmySoldierCount += WARMYD.men(null).get(playerArmy) * PLAYER_REGION_WEIGHT;
			} else {
				rebelArmySoldierCount += WARMYD.men(null).get(playerArmy) * NON_PLAYER_REGION_WEIGHT;
			}
		}
		
		rebelArmySoldierCount = Math.max(rebelArmySoldierCount, MIN_REBEL_ARMY_SIZE);
		
		return rebelArmySoldierCount;
	}
	
	private void spawnArmyInRegion(Region stagingRegion) {
		// Find a random point in the staging region to spawn the army.
		COORDINATE coordinate = WPathing.random(stagingRegion);
		
		// Why are we deducting 1 from the staging region's area?
		WArmy army = World.ARMIES().createRebel(coordinate.x() - 1, coordinate.y() - 1);
		
		if (army == null) {
			throw new IllegalStateException("Failed to create rebel army in staging region " + stagingRegion.name());
		}
		
		Region targetRegion = WPathing.findAdjacentRegion(stagingRegion.cx(), stagingRegion.cy(), playerFinder);
		
		if (targetRegion == null) {
			throw new IllegalStateException("Failed to find target region for staging region " + stagingRegion.name());
		}
		
		// TODO: Continue here, see notes at the top of the file.
	}
	
	private void triggerRaid() {
		Logger.info("Triggering raid event, population: " + settlementPopulation + ", wealth: " + settlementWealth);
		
		Region spawnRegion = findSuitableRegion();
		
		// If we can't find a suitable region, then something is seriously wrong.
		if (spawnRegion == null) {
			throw new IllegalStateException("RaidEvent: no suitable region found to spawn raid in.");
		}
	}
}
