/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.interfaces;

public interface ITickCapable {
    /**
     * Invoked every tick.
     *
     * @param delta Time in milliseconds since last tick.
     */
    void onTick(double delta);
}