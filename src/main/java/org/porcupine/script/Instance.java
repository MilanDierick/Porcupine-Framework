/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.script;

import org.porcupine.modules.IRenderCapable;
import org.porcupine.modules.IScriptEntity;
import org.porcupine.modules.ISerializable;
import org.porcupine.modules.ITickCapable;
import org.porcupine.modules.AggregateModule;
import org.porcupine.modules.AggregateModuleLoader;
import org.porcupine.utilities.Logger;
import script.SCRIPT;
import snake2d.Renderer;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
		
		Iterable<Path> paths = AggregateModuleLoader.getModPaths();
		Collection<Path> modulePaths = AggregateModuleLoader.extendToScriptPaths(paths);
		List<Path> jarPaths = AggregateModuleLoader.extendToJarPaths(modulePaths);
		Set<AggregateModule> modules = AggregateModuleLoader.extractModules(jarPaths);
		
		if (modules == null) {
			return;
		}
		
		for (AggregateModule module : modules) {
			processModule(module);
		}
		
		for (IScriptEntity entity : scriptEntities) {
			entity.onInitializeEarly();
		}
		
		for (IScriptEntity entity : scriptEntities) {
			entity.onInitializeLate();
		}
	}
	
	private void processModule(AggregateModule module) {
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
	
	/**
	 * @param v The time in seconds since the last game tick while the game was running.
	 *
	 * @apiNote delta is zero when the game is paused.
	 */
	@Override
	public void update(double v) {
		for (ITickCapable tickCapable : tickCapables) {
			tickCapable.onTick(v);
		}
	}
	
	@Override
	public void render(Renderer r, float ds) {
		SCRIPT.SCRIPT_INSTANCE.super.render(r, ds);
		
		for (IRenderCapable renderCapable : renderCapables) {
			renderCapable.onRender(r, ds);
		}
	}
	
	@Override
	public void save(FilePutter filePutter) {
		Logger.info("Saving Porcupine framework state information...");
		
		for (ISerializable serializable : serializables) {
			serializable.onSerialize(filePutter);
		}
	}
	
	@Override
	public void load(FileGetter fileGetter) {
		Logger.info("Loading Porcupine framework state information...");
		
		for (ISerializable serializable : serializables) {
			serializable.onDeserialize(fileGetter);
		}
	}
}
