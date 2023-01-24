/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class AggregateModuleConfigTest {
	
	@Test
	public void testEntrySet() {
		Map<String, Object> testMap = new HashMap<>();
		testMap.put("key1", "value1");
		testMap.put("key2", 2);
		testMap.put("key3", true);
		
		AggregateModuleConfig config = new AggregateModuleConfig();
		config.putAll(testMap);
		Assertions.assertEquals(testMap.entrySet(), config.entrySet(), "Entry set should be equal.");
	}
}