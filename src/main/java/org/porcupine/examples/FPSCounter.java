/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.examples;

import init.sprite.UI.UI;
import org.porcupine.interfaces.IRenderCapable;
import org.porcupine.interfaces.IScriptEntity;
import org.porcupine.interfaces.ITickCapable;
import snake2d.Renderer;
import snake2d.util.sprite.text.Text;

import java.util.ArrayDeque;

public class FPSCounter implements IScriptEntity, ITickCapable, IRenderCapable {
	private long lastTime;
	private final int maxSamples = 100;
	private int tickFPS = 0;
	private final ArrayDeque<Long> tickTimes = new ArrayDeque<>(maxSamples);
	
	@Override
	public void onRender(Renderer renderer, double v) {
		new Text(UI.FONT().S, "Tick FPS: " + tickFPS).render(renderer, 25, 125);
	}
	
	@Override
	public void onTick(double v) {
		if (tickTimes.size() >= maxSamples) {
			tickTimes.removeFirst();
		}
		
		long currentTime = System.currentTimeMillis();
		
		tickTimes.addLast(currentTime - lastTime);
		lastTime = currentTime;
		
		// Calculate the average tick time over the last 100 ticks.
		double tickTime = tickTimes.stream().mapToLong(Long::longValue).average().orElse(0);
		
		// Calculate the FPS based on the average tick time.
		tickFPS = (int) (1000 / tickTime);
	}
}
