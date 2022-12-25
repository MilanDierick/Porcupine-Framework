/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.sandbox;

import org.porcupine.interfaces.IRenderCapable;
import org.porcupine.interfaces.IScriptEntity;
import org.porcupine.interfaces.ISerializable;
import org.porcupine.interfaces.ITickCapable;
import org.porcupine.modules.AggregateModule;
import org.porcupine.modules.AggregateModuleLoader;
import script.SCRIPT;
import snake2d.Renderer;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Instance implements SCRIPT.SCRIPT_INSTANCE {
	private final List<IScriptEntity> scriptEntities;
	private final List<ITickCapable> tickCapables;
	private final List<IRenderCapable> renderCapables;
	private final List<ISerializable> serializables;
	
	public Instance() {
		this.scriptEntities = new ArrayList<>();
		this.tickCapables = new ArrayList<>();
		this.renderCapables = new ArrayList<>();
		this.serializables = new ArrayList<>();
		
		ArrayList<Path> paths = AggregateModuleLoader.getModPaths();
		ArrayList<Path> modulePaths = AggregateModuleLoader.extendToScriptPaths(paths);
		ArrayList<Path> jarPaths = AggregateModuleLoader.extendToJarPaths(modulePaths);
		ArrayList<AggregateModule> modules = AggregateModuleLoader.extractModules(jarPaths);
		
		for (AggregateModule module : modules) {
			if (module.scriptEntity != null) {
				scriptEntities.add(module.scriptEntity);
			}
			
			if (module.tickCapable != null) {
				tickCapables.add(module.tickCapable);
			}
			
			if (module.renderCapable != null) {
				renderCapables.add(module.renderCapable);
			}
			
			if (module.serializable != null) {
				serializables.add(module.serializable);
			}
		}
		
		for (IScriptEntity entity : scriptEntities) {
			entity.onInitializeEarly();
		}
		
		for (IScriptEntity entity : scriptEntities) {
			entity.onInitializeLate();
		}
	}
	
	/**
	 * @param delta The time in seconds since the last game tick while the game was running.
	 *
	 * @apiNote delta is zero when the game is paused.
	 */
	@Override
	public void update(double delta) {
		for (ITickCapable tickCapable : tickCapables) {
			tickCapable.onTick(delta);
		}
	}
	
	@Override
	public void render(Renderer renderer, float delta) {
		SCRIPT.SCRIPT_INSTANCE.super.render(renderer, delta);
		
		for (IRenderCapable renderCapable : renderCapables) {
			renderCapable.onRender(renderer, delta);
		}
	}
	
	@Override
	public void save(FilePutter filePutter) {
		System.out.println("Saving Porcupine framework state information...");
		
		for (ISerializable serializable : serializables) {
			serializable.onSerialize(filePutter);
		}
	}
	
	@Override
	public void load(FileGetter fileGetter) {
		System.out.println("Loading Porcupine framework state information...");
		
		for (ISerializable serializable : serializables) {
			serializable.onDeserialize(fileGetter);
		}
	}
}
