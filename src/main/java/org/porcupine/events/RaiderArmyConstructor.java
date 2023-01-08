/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import init.RES;
import init.race.RACES;
import init.race.Race;
import org.porcupine.utilities.Logger;
import settlement.stats.STATS;
import settlement.stats.StatsEquippables;
import world.World;
import world.army.WDivRegional;
import world.army.WDivRegionalAll;
import world.entity.army.WArmy;
import world.map.regions.REGIOND;
import world.map.regions.Region;

public class RaiderArmyConstructor {
	/**
	 * Represents the amount of items of a type of equipment that is needed to increase the equipment level by 1.
	 */
	public static final int EQUIPMENT_REQUIRED_PER_TIER = 10;
	
	public static final int MIN_REBEL_ARMY_SIZE = 10;
	
	public static final int MELEE_TRAINING_LOWER_BOUND = 0;
	public static final int MELEE_TRAINING_UPPER_BOUND = 6;
	public static final int RANGED_TRAINING_LOWER_BOUND = 0;
	public static final int RANGED_TRAINING_UPPER_BOUND = 6;
	
	private int totalBudget;
	private int pawnsBudget;
	private int weaponsBudget;
	private int armourBudget;
	
	private int costPerPawn;
	private int costPerMeleeWeapon;
	private int costPerRangedWeapon;
	private int costPerArmour;
	
	private final int pawnsPerDivision;
	
	private final Region region;
	private final WArmy army;
	private final WDivRegionalAll worldDivisions;
	
	public RaiderArmyConstructor(Region region, WArmy army) {
		pawnsPerDivision = RES.config().BATTLE.MEN_PER_DIVISION;
		this.region = region;
		this.army = army;
		this.worldDivisions = World.ARMIES().regional();
	}
	
	public RaiderArmyConstructor setTotalBudget(int totalBudget) {
		this.totalBudget = totalBudget;
		return this;
	}
	
	public RaiderArmyConstructor setPawnsBudget(int pawnsBudget) {
		this.pawnsBudget = pawnsBudget;
		return this;
	}
	
	public RaiderArmyConstructor setWeaponsBudget(int weaponsBudget) {
		this.weaponsBudget = weaponsBudget;
		return this;
	}
	
	public RaiderArmyConstructor setArmourBudget(int armourBudget) {
		this.armourBudget = armourBudget;
		return this;
	}
	
	public RaiderArmyConstructor setCostPerPawn(int costPerPawn) {
		this.costPerPawn = costPerPawn;
		return this;
	}
	
	public RaiderArmyConstructor setCostPerMeleeWeapon(int costPerMeleeWeapon) {
		this.costPerMeleeWeapon = costPerMeleeWeapon;
		return this;
	}
	
	public RaiderArmyConstructor setCostPerRangedWeapon(int costPerRangedWeapon) {
		this.costPerRangedWeapon = costPerRangedWeapon;
		return this;
	}
	
	public RaiderArmyConstructor setCostPerArmour(int costPerArmour) {
		this.costPerArmour = costPerArmour;
		return this;
	}
	
	public void configure() {
		Race race = getLargestRacePopulation();
		
		int totalSoldierCount = pawnsBudget / costPerPawn;
		int remainingSoldierCount = totalSoldierCount % pawnsPerDivision;
		int divisionCount = (int) Math.ceil((double) totalSoldierCount / pawnsPerDivision);
		
		int meleeWeaponCount = weaponsBudget / costPerMeleeWeapon;
		int rangedWeaponCount = weaponsBudget / costPerRangedWeapon;
		int armourCount = armourBudget / costPerArmour;
		
		int totalMeleeWeaponCount = meleeWeaponCount;
		int totalRangedWeaponCount = rangedWeaponCount;
		int totalArmourCount = armourCount;
		
		Logger.info("Cost per pawn: " + costPerPawn + ", pawn budget: " + pawnsBudget);
		Logger.info("Creating an army with " + totalSoldierCount + " soldiers, divided into " + divisionCount + " divisions.");
		
		// This serves as a shortcut in case the budget is too low to even create a single division.
		if (totalSoldierCount < MIN_REBEL_ARMY_SIZE) {
			createDivision(race, MIN_REBEL_ARMY_SIZE, meleeWeaponCount, rangedWeaponCount, armourCount);
			return;
		}
		
		while (totalSoldierCount >= pawnsPerDivision) {
			int meleeWeaponCountForDivision = totalMeleeWeaponCount / divisionCount;
			int rangedWeaponCountForDivision = totalRangedWeaponCount / divisionCount;
			int armourCountForDivision = totalArmourCount / divisionCount;
			
			createDivision(
					race,
					pawnsPerDivision,
					meleeWeaponCountForDivision,
					rangedWeaponCountForDivision,
					armourCountForDivision
			);
			
			totalSoldierCount -= pawnsPerDivision;
			meleeWeaponCount -= meleeWeaponCountForDivision;
			rangedWeaponCount -= rangedWeaponCountForDivision;
			armourCount -= armourCountForDivision;
		}
		
		if (remainingSoldierCount != 0) {
			createDivision(race, remainingSoldierCount, meleeWeaponCount, rangedWeaponCount, armourCount);
		}
	}
	
	private Race getLargestRacePopulation() {
		Race largest = RACES.all().get(0);
		int biggestPopulation = 0;
		
		for (Race r : RACES.all()) {
			int population = REGIOND.RACE(r).population.get(region);
			if (population > biggestPopulation) {
				largest = r;
				biggestPopulation = population;
			}
		}
		
		return largest;
	}
	
	@SuppressWarnings("SameParameterValue")
	private double normalize(double value, double min, double max) {
		return (value - min) / (max - min);
	}
	
	@SuppressWarnings({"UnsecureRandomNumberGeneration", "DuplicateStringLiteralInspection"})
	private void createDivision(
			Race race, int soldierCount, int meleeWeaponCount, int rangedWeaponCount, int armourCount
	) {
		int meleeTraining = MELEE_TRAINING_LOWER_BOUND + (int) (Math.random() * (MELEE_TRAINING_UPPER_BOUND - MELEE_TRAINING_LOWER_BOUND));
		int rangedTraining = RANGED_TRAINING_LOWER_BOUND + (int) (Math.random() * (RANGED_TRAINING_UPPER_BOUND - RANGED_TRAINING_LOWER_BOUND));
		
		WDivRegional division = worldDivisions.create(race, soldierCount, meleeTraining, rangedTraining, army);
		
		StatsEquippables.EQUIPPABLE_MILITARY meleeWeapon = findEquippableWithName("WEAPON");
		StatsEquippables.EQUIPPABLE_MILITARY rangedWeapon = findEquippableWithName("BOW");
		StatsEquippables.EQUIPPABLE_MILITARY armour = findEquippableWithName("ARMOUR");
		
		// Each soldier can equip up to 8 pieces of equipment per type, so multiply the count by 8.
		int maxEquipmentCountPerType = soldierCount << 3;
		
		int normalizedMeleeWeaponCount = (int) (normalize(meleeWeaponCount, 0, maxEquipmentCountPerType) * 8);
		int normalizedRangedWeaponCount = (int) (normalize(rangedWeaponCount, 0, maxEquipmentCountPerType) * 8);
		int normalizedArmourCount = (int) (normalize(armourCount, 0, maxEquipmentCountPerType) * 8);
		
		division.menSet(soldierCount);
		division.equipTargetset(meleeWeapon, normalizedMeleeWeaponCount);
		division.equipTargetset(rangedWeapon, normalizedRangedWeaponCount);
		division.equipTargetset(armour, normalizedArmourCount);
	}
	
	private StatsEquippables.EQUIPPABLE_MILITARY findEquippableWithName(String equippableName) {
		for (StatsEquippables.EQUIPPABLE_MILITARY equippable : STATS.EQUIP().military_all()) {
			if (equippable.resource().key.toString().equals(equippableName)) {
				return equippable;
			}
		}
		
		throw new IllegalArgumentException("No equippable with name " + equippableName + " found.");
	}
}
