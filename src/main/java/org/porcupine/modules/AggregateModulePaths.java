/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import java.nio.file.Path;

public class AggregateModulePaths {
	public final Path absolutePath;
	public final Path campaignsPath;
	public final Path examplesPath;
	public final Path savesPath;
	public final Path scriptsPath;
	
	public AggregateModulePaths(
			Path absolutePath,
			Path campaignsPath,
			Path examplesPath,
			Path savesPath,
			Path scriptsPath
	) {
		this.absolutePath = absolutePath;
		this.campaignsPath = campaignsPath;
		this.examplesPath = examplesPath;
		this.savesPath = savesPath;
		this.scriptsPath = scriptsPath;
	}
}
