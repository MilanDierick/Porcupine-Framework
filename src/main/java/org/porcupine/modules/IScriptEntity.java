/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

public interface IScriptEntity {
    /**
     * Invoked sometime before a new world has been created.
     *
     * @apiNote This method is not invoked by the game.
     * Presumably this method would be invoked before a world is created, right after the script has been loaded.
     */
    default void onInitializeEarly() {
    
    }
    
    /**
     * Invoked after a new world is created.
     */
    default void onInitializeLate() {
    
    }
}
