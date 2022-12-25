/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.interfaces;

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
    
    default String getName() {
        return null;
    }
    
    default String getDescription() {
        return null;
    }
    
    default String getAuthor() {
        return null;
    }
    
    default String getVersion() {
        return null;
    }
}
