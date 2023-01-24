/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.script;

import org.porcupine.events.*;
import org.porcupine.modules.*;
import org.porcupine.statistics.Statistics;
import org.porcupine.utilities.Logger;
import script.SCRIPT;
import settlement.room.main.RoomBlueprintIns;
import settlement.room.main.RoomInstance;
import snake2d.Renderer;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class Instance implements SCRIPT.SCRIPT_INSTANCE {
	private final Collection<IScriptEntity> scriptEntities;
	private final Collection<ITickCapable> tickCapables;
	private final Collection<IRenderCapable> renderCapables;
	private final Collection<ISerializable> serializables;
	private final Collection<IGlobalEvent> globalEvents;
	private final Collection<IRoomEvent> roomEvents;
	
	public Instance() {
		this.scriptEntities = new ArrayList<>();
		this.tickCapables = new ArrayList<>();
		this.renderCapables = new ArrayList<>();
		this.serializables = new ArrayList<>();
		this.globalEvents = EventLoader.getGlobalEvents();
		this.roomEvents = EventLoader.getRoomEvents();
		
		Iterable<AggregateModule> modules = AggregateModuleLoader.getModules();
		
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
	 * @param ds The time in seconds since the last game tick while the game was running.
	 *
	 * @apiNote delta is zero when the game is paused.
	 */
	@SuppressWarnings("UnsecureRandomNumberGeneration")
	@Override
	public void update(double ds) {
		Statistics.refreshAllStats();
		
		for (ITickCapable tickCapable : tickCapables) {
			tickCapable.onTick(ds);
		}
		
		for (IGlobalEvent globalEvent : globalEvents) {
			float chance = globalEvent.chancePerSecond();
			
			if (chance > 0 && Math.random() < chance * ds) {
				globalEvent.onEvent();
			}
		}
		
		for (IRoomEvent event : roomEvents) {
			RoomBlueprintIns<?> blueprint = event.getRoomType();
			for (int ri = 0; ri < blueprint.instancesSize(); ri++) {
				RoomInstance instance = blueprint.getInstance(ri);
				event.onEvent(instance);
			}
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
	public void save(FilePutter file) {
		Logger.info("Saving Porcupine framework state information...");
		
		//		for (ISerializable serializable : serializables) {
		//			serializable.onSerialize(file);
		//		}
	}
	
	@Override
	public void load(FileGetter file) {
		Logger.info("Loading Porcupine framework state information...");
		
		//		for (ISerializable serializable : serializables) {
		//			serializable.onDeserialize(file);
		//		}
	}
}
