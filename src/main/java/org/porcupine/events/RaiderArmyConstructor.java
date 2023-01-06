/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import init.RES;
import init.race.RACES;
import init.race.Race;
import org.porcupine.utilities.Logger;
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
		
		Logger.info("Cost per pawn: " + costPerPawn + ", pawn budget: " + pawnsBudget);
		Logger.info("Creating an army with " + totalSoldierCount + " soldiers, divided into " + divisionCount + " divisions.");
		
		// This serves as a shortcut in case the budget is too low to even create a single division.
		if (totalSoldierCount < MIN_REBEL_ARMY_SIZE) {
			createDivision(race, MIN_REBEL_ARMY_SIZE);
			return;
		}
		
		while(totalSoldierCount >= pawnsPerDivision) {
			createDivision(race, pawnsPerDivision);
			totalSoldierCount -= pawnsPerDivision;
		}
		
		if (remainingSoldierCount != 0) {
			createDivision(race, remainingSoldierCount);
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
	
	@SuppressWarnings("UnsecureRandomNumberGeneration")
	private void createDivision(Race race, int soldierCount) {
		int meleeTraining = MELEE_TRAINING_LOWER_BOUND + (int) (Math.random() * (MELEE_TRAINING_UPPER_BOUND - MELEE_TRAINING_LOWER_BOUND));
		int rangedTraining = RANGED_TRAINING_LOWER_BOUND + (int) (Math.random() * (RANGED_TRAINING_UPPER_BOUND - RANGED_TRAINING_LOWER_BOUND));
		
		WDivRegional division = worldDivisions.create(race, soldierCount, meleeTraining, rangedTraining, army);
		
		division.menSet(soldierCount);
	}
}
