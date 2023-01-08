/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import game.faction.FACTIONS;
import init.race.RACES;
import init.resources.RESOURCE;
import init.resources.RESOURCES;
import org.porcupine.modules.IScriptEntity;
import org.porcupine.modules.ITickCapable;
import org.porcupine.statistics.ResourceMetadata;
import org.porcupine.statistics.Statistics;
import org.porcupine.statistics.StockpileStatistics;
import org.porcupine.utilities.Logger;
import settlement.main.SETT;
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

// TODO: Calculate how much it costs to equip one pawn with a melee / ranged weapon and armor, per tier of equipment.
// TODO: Create a popup system, similar to the current Protection event.
// TODO: Calculate the chance each tick that a reading army will spawn. Might just use the game's chance calculation.
// TODO: Consider adding (a portion of) the liquid silver to the raider's budget.

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
	
	public static final int RAID_FREQUENCY_YEARS = 5;
	public static final int RAID_FREQUENCY_LOWER_BOUNDS = 2;
	public static final int RAID_FREQUENCY_UPPER_BOUNDS = 10;
	
	public static final int BASE_PAWN_VALUE = 500;
	public static final float RATION_CONSUMPTION_PER_DAY = 0.25f;
	public static final float DRINK_CONSUMPTION_PER_DAY = 0.25f;
	public static final float CLOTHES_CONSUMPTION_PER_DAY = 0.04f;
	public static final float SETTLEMENT_RICHES_WEIGHT = 0.2f;
	
	public static final int PAWN_CONSUMPTION_PERIOD = 5;
	public static final int DAYS_PER_YEAR = 16;
	
	private int playerVictories;
	
	private int settlementPopulation;
	private int settlementWealth;
	
	private int valueOfPawn;
	private int valueOfMeleeWeapon;
	private int valueOfRangedWeapon;
	private int valueOfArmor;
	
	private final StockpileStatistics stockpileStatistics;
	private ArmyBudgetDivision armyBudgetDivision;
	
	private final BOOLEAN_OBJECT<Region> playerFinder = t -> t.faction() == FACTIONS.player();
	
	public RaidEvent() {
		IDebugPanelSett.add("Raid Event", this::triggerRaid);
		
		stockpileStatistics = Statistics.get(StockpileStatistics.class);
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
	
	/**
	 * Calculates the wealth of the settlement, based on the amount of resources in the stockpile.
	 *
	 * @implNote We are using the sell price of the resource, as it is the price that the raiders would receive if they
	 * sold the resource to the markets. Raiders are an impatient folk, and would not want to wait for the resource to
	 * be bought by other settlements, they want their profits NOW!
	 */
	private void calculateSettlementProsperity() {
		for (RESOURCE resource : RESOURCES.ALL()) {
			ResourceMetadata metadata = stockpileStatistics.get(resource);
			settlementWealth += metadata.getStockpile() * metadata.getSellPrice();
		}
		
		settlementWealth += settlementPopulation * valueOfPawn;
		settlementWealth += STATS.GOVERN().RICHES.data().get(null) * SETTLEMENT_RICHES_WEIGHT;
	}
	
	private void calculateBudget() {
		armyBudgetDivision = new ArmyBudgetDivision(settlementWealth);
	}
	
	/**
	 * Calculates the value of a pawn, based on the amount of army supplies it consumes over a certain amount of time,
	 * added on top of the base value of a pawn.
	 */
	private void calculatePawnValue() {
		int valueOfRations = stockpileStatistics.get(RESOURCES.map().tryGet("RATION")).getBuyPrice();
		int valueOfDrinks = stockpileStatistics.get(RESOURCES.map().tryGet("_ALCOHOL")).getBuyPrice();
		int valueOfClothes = stockpileStatistics.get(RESOURCES.map().tryGet("CLOTHES")).getBuyPrice();
		
		float valueOfRationsPerDay = valueOfRations * RATION_CONSUMPTION_PER_DAY;
		float valueOfDrinksPerDay = valueOfDrinks * DRINK_CONSUMPTION_PER_DAY;
		float valueOfClothesPerDay = valueOfClothes * CLOTHES_CONSUMPTION_PER_DAY;
		
		float valueOfRationsTotal = valueOfRationsPerDay * DAYS_PER_YEAR * PAWN_CONSUMPTION_PERIOD;
		float valueOfDrinksTotal = valueOfDrinksPerDay * DAYS_PER_YEAR * PAWN_CONSUMPTION_PERIOD;
		float valueOfClothesTotal = valueOfClothesPerDay * DAYS_PER_YEAR * PAWN_CONSUMPTION_PERIOD;
		
		valueOfPawn = (int) (BASE_PAWN_VALUE + valueOfRationsTotal + valueOfDrinksTotal + valueOfClothesTotal);
	}
	
	/**
	 * Calculates the value of the different types of equipment based on the buy price of the resource.
	 */
	@SuppressWarnings("DuplicateStringLiteralInspection")
	private void calculateEquipmentValue() {
		valueOfMeleeWeapon = stockpileStatistics.get(RESOURCES.map().tryGet("WEAPON")).getBuyPrice();
		valueOfRangedWeapon = stockpileStatistics.get(RESOURCES.map().tryGet("BOW")).getBuyPrice();
		valueOfArmor = stockpileStatistics.get(RESOURCES.map().tryGet("ARMOUR")).getBuyPrice();
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
		
		// If this region is owned by the player, return true.
		if (region.faction() == FACTIONS.player()) {
			return false;
		}
		
		return WPathing.findAdjacentRegion(region, playerFinder) != null;
	}
	
	/**
	 * Finds a suitable region to spawn a raid in, that is adjacent to a region owned by the player.
	 *
	 * @return a region that is suitable to spawn a raid in.
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
		
		RaiderArmyConstructor constructor = new RaiderArmyConstructor(targetRegion, army);
		
		constructor.setTotalBudget(armyBudgetDivision.getTotalBudget())
		           .setPawnsBudget(armyBudgetDivision.getPawnsBudget())
		           .setWeaponsBudget(armyBudgetDivision.getWeaponsBudget())
		           .setArmourBudget(armyBudgetDivision.getArmourBudget())
		           .setCostPerPawn(valueOfPawn)
		           .setCostPerMeleeWeapon(valueOfMeleeWeapon)
		           .setCostPerRangedWeapon(valueOfRangedWeapon)
		           .setCostPerArmour(valueOfArmor)
		           .configure();
		
		army.name.clear().add(RACES.all().get(0).info.armyNames.rnd());
		
		// Fill the army with supplies, rebel armies should have enough supplies to reach the player's settlement.
		for (WARMYD.WArmySupply supply : WARMYD.supplies().all) {
			supply.current().set(army, supply.max(army));
		}
	}
	
	private void triggerRaid() {
		clearCache();
		calculatePopulation();
		calculateSettlementProsperity();
		calculatePawnValue();
		calculateEquipmentValue();
		calculateBudget();
		
		Logger.info("Triggering raid event, population: " + settlementPopulation + ", wealth: " + settlementWealth);
		
		Region spawnRegion = findSuitableRegion();
		
		// If we can't find a suitable region, then something is seriously wrong.
		if (spawnRegion == null) {
			throw new IllegalStateException("RaidEvent: no suitable region found to spawn raid in.");
		}
		
		spawnArmyInRegion(spawnRegion);
		
		int soldierCount = armyBudgetDivision.getPawnsBudget() / valueOfPawn;
		String raiderName = RACES.all().get(0).info.armyNames.rnd();
		
		CouncilorMessage councilorMessage = new CouncilorMessage("Councilor", soldierCount);
		
		new RaidersMessage(
				"Raiders!",
				SETT.FACTION().capitolRegion().name().toString(),
				raiderName,
				councilorMessage
		).send();
	}
}
