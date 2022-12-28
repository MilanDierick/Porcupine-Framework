/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import snake2d.Renderer;

public interface IRenderCapable {
    /**
     * Invoked every frame.
     */
    default void onRender(Renderer renderer, double delta) {
    
    }
}
