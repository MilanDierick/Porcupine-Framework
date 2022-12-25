/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.sandbox;

import script.SCRIPT;

public class Sandbox implements SCRIPT {
    @Override
    public CharSequence name() {
        return "Porcupine Framework";
    }

    @Override
    public CharSequence desc() {
        return "Framework to collect common routines and structures.";
    }

    @Override
    public void initBeforeGameCreated() {
        System.out.println("Initializing Porcupine framework...");
    }

    @Override
    public SCRIPT_INSTANCE initAfterGameCreated() {
        System.out.println("Porcupine framework initialized.");
        return new Instance();
    }


}
