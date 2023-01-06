/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.utilities;

import game.GameLoader;
import init.biomes.CLIMATES;
import init.biomes.TERRAINS;
import init.paths.PATHS;
import init.race.RACES;
import init.settings.S;
import init.error.ErrorHandler;
import snake2d.CORE;

@SuppressWarnings({"DuplicateStringLiteralInspection", "HardcodedFileSeparator", "PublicStaticArrayField"})
public final class TestInstanceLauncher {
	public static final String[] MODS = {"C:\\Users\\milan\\AppData\\Roaming\\songsofsyx\\mods\\PorcupineFramework"};
	
	private TestInstanceLauncher() {
	}
	
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public static void main(String[] args) {
		CORE.init(new ErrorHandler());
		PATHS.init(MODS, null, false);
		CLIMATES.init();
		TERRAINS.init();
		new RACES();
		CORE.create(S.get().make());
		CORE.start(new GameLoader(PATHS.MISC().SAVES.get("_Tutorial"), "_Tutorial"));
	}
}
