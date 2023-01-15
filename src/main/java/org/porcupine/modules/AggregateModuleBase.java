/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import java.util.Collections;
import java.util.Map;

public class AggregateModuleBase {
	protected AggregateModuleInfo info;
	protected AggregateModuleConfig config;
	
	public AggregateModuleBase(AggregateModuleInfo info, AggregateModuleConfig config) {
		this.info = info.clone();
		this.config = config.clone();
	}
	
	public AggregateModuleInfo getInfo() {
		return info;
	}
	
	public Map<Object, Object> getConfig() {
		return Collections.unmodifiableMap(config);
	}
}
