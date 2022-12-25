/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.entities;

import init.sprite.UI.UI;
import org.porcupine.interfaces.IRenderCapable;
import org.porcupine.interfaces.IScriptEntity;
import org.porcupine.interfaces.ITickCapable;
import snake2d.Renderer;
import snake2d.util.sprite.text.Text;

import java.util.ArrayDeque;

public class FPSCounter implements IScriptEntity, ITickCapable, IRenderCapable {
    private final int maxSamples = 100;
    private int tickFPS = 0;
    private final ArrayDeque<Double> tickTimes = new ArrayDeque<>(maxSamples);

    @Override
    public void onInitializeEarly() {

    }

    @Override
    public void onInitializeLate() {

    }

    @Override
    public void onRender(Renderer renderer, double delta) {
        new Text(UI.FONT().S, "Tick FPS: " + tickFPS).render(renderer, 25, 125);
    }

    @Override
    public void onTick(double delta) {
        if (tickTimes.size() >= maxSamples) {
            tickTimes.removeFirst();
        }

        tickTimes.addLast(delta);

        if (tickTimes.size() > 1) { // We need at least two samples to calculate FPS.
            double tickTime = tickTimes.getLast() - tickTimes.getFirst();
            tickFPS = (int) (maxSamples / (tickTime / 1000.0));
        }
    }
}
