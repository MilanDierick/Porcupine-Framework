/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("DuplicateStringLiteralInspection")
class Vector2DTest {
	
	@Test
	void getX() {
		Vector2D<Integer> vector = new Vector2D<>(1, 2);
		Assertions.assertEquals(1, vector.getX(), "X value is not correct.");
	}
	
	@Test
	void setX() {
		Vector2D<Integer> vector = new Vector2D<>(1, 2);
		vector.setX(3);
		Assertions.assertEquals(3, vector.getX(), "X value is not correct.");
	}
	
	@Test
	void getY() {
		Vector2D<Integer> vector = new Vector2D<>(1, 2);
		Assertions.assertEquals(2, vector.getY(), "Y value is not correct.");
	}
	
	@Test
	void setY() {
		Vector2D<Integer> vector = new Vector2D<>(1, 2);
		vector.setY(3);
		Assertions.assertEquals(3, vector.getY(), "Y value is not correct.");
	}
	
	@Test
	void testToString() {
		Vector2D<Integer> vector = new Vector2D<>(1, 2);
		Assertions.assertEquals("Vector2D{x=1, y=2}", vector.toString(), "String representation is not correct.");
	}
}