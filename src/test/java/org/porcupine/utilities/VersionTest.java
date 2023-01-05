/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("DuplicateStringLiteralInspection")
class VersionTest {
	
	@Test
	void getMajor() {
		Version version = new Version(1, 2, 3);
		Assertions.assertEquals(1, version.getMajor(), "Major version is not correct.");
	}
	
	@Test
	void getMinor() {
		Version version = new Version(1, 2, 3);
		Assertions.assertEquals(2, version.getMinor(), "Minor version is not correct.");
	}
	
	@Test
	void getPatch() {
		Version version = new Version(1, 2, 3);
		Assertions.assertEquals(3, version.getPatch(), "Patch version is not correct.");
	}
	
	@Test
	void getSuffix() {
		Version version = new Version(1, 2, 3, "alpha");
		Assertions.assertEquals("alpha", version.getSuffix(), "Suffix is not correct.");
	}
	
	@Test
	void testToString() {
		Version version = new Version(1, 2, 3, "alpha");
		Assertions.assertEquals("1.2.3-alpha", version.toString(), "String representation is not correct.");
	}
	
	@Test
	void testEquals() {
		Version version1 = new Version(1, 2, 3, "alpha");
		Version version2 = new Version(1, 2, 3, "alpha");
		Assertions.assertEquals(version1, version2, "Versions are not equal.");
	}
	
	@Test
	void testHashCode() {
		Version version1 = new Version(1, 2, 3, "alpha");
		Version version2 = new Version(1, 2, 3, "alpha");
		Assertions.assertEquals(version1.hashCode(), version2.hashCode(), "Hash codes are not equal.");
	}
}