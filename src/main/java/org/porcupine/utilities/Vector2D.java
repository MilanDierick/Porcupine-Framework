/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.utilities;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class Vector2D<Type extends Number> {
	private @NotNull Type x;
	private @NotNull Type y;
	
	public Vector2D(@NotNull Type x, @NotNull Type y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2D(Vector2D<Type> other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	public @NotNull Type getX() {
		return x;
	}
	
	public void setX(@NotNull Type x) {
		this.x = x;
	}
	
	public @NotNull Type getY() {
		return y;
	}
	
	public void setY(@NotNull Type y) {
		this.y = y;
	}
	
	@Override
	@NonNls
	public String toString() {
		return "Vector2D{" + "x=" + x + ", y=" + y + '}';
	}
}
