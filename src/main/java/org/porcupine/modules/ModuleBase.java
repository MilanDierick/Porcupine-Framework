/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import org.jetbrains.annotations.Nullable;
import org.porcupine.utilities.Logger;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class ModuleBase {
	protected AggregateModuleInfo info;
	protected AggregateModuleConfig config;
	
	public ModuleBase(AggregateModuleInfo info, @Nullable AggregateModuleConfig config) {
		this.info = info.clone();
		
		if (config != null) {
			this.config = config.clone();
		} else {
			this.config = new AggregateModuleConfig();
		}
	}
	
	public AggregateModuleInfo getInfo() {
		return info;
	}
	
	public Map<Object, Object> getConfig() {
		return Collections.unmodifiableMap(config);
	}
	
	public void persistConfig() {
		Path configPath = info.paths.savesPath;
		
		try (OutputStream outputStream = Files.newOutputStream(configPath)) {
			config.store(outputStream, "Porcupine Aggregate Module Config");
		} catch (Exception e) {
			Logger.error("Failed to persist config for module %s", info.name);
		}
	}
}
