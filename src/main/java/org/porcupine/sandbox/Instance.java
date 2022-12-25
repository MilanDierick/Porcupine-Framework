/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.sandbox;

import org.porcupine.entities.FPSCounter;
import org.porcupine.interfaces.IRenderCapable;
import org.porcupine.interfaces.IScriptEntity;
import org.porcupine.interfaces.ISerializable;
import org.porcupine.interfaces.ITickCapable;
import script.SCRIPT;
import snake2d.Renderer;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.util.ArrayList;
import java.util.List;

// DISCLAIMER:

// Please note that this framework is in an early development phase and has not yet undergone thorough testing or
// optimization. As such, it may contain bugs or issues that could potentially cause problems or unexpected behaviour.
// This framework might be deprecated or archived at any point, due to changes in the game or the lack of personal time.
// Use at your own risk.

// This framework is intended to serve as an abstraction layer for common routines and structures found in the game,
// functioning as an aggregator of multiple instances of different interface types, including IScriptEntity,
// ITickCapable, IRenderCapable, and ISerializable. These interfaces represent different capabilities that script
// entities can implement in order to participate in the framework.

// One of the primary goals of this framework is to provide a more intuitive and easier-to-use interface for accessing
// the scripting capabilities of the base game. The base game's scripting capabilities can be difficult to understand
// and use, especially for developers who are unfamiliar with the underlying implementation. By providing a clear and
// consistent interface, this framework aims to make it easier for developers to create scripts and interact with the
// game.

// Another goal of this framework is to intercept erroneous calls of the base game's API, by virtue of requiring
// developers to access the game's API through the framework. By routing all access to the base game's API through the
// framework, it becomes possible to validate and sanitize incoming calls, ensuring that developers do not put the game
// into an undefined state or cause other unintended consequences. This can help to improve the stability and
// reliability of scripts that use the framework, as well as protect against malicious or poorly-written scripts that
// could potentially cause problems.

// Please be aware that the API of this framework is subject to change as development progresses. It is not recommended
// to rely on specific implementation details or behaviour that may change in future updates. It is recommended to
// check for updates regularly and to adapt your code accordingly.

// Since I'm still very unfamiliar with the game's API, I might create functionality that is inefficient or nonsensical
// in the scope of the current game API, or there may be easier and more straightforward ways to implement certain
// functionality. If you think that's the case, please have a chat with me!

// Finally, the framework is not designed to support multiple instances of itself running concurrently. While it may be
// possible to do so, it is not supported nor (currently) enforced, and may result in unpredictable behaviour. Please
// ensure that only one instance of this framework is loaded at a time.

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
		
		// TODO: Instead of hardcoding the entities, use reflection to find them. This will allow scripts to use the
		//  framework without having to modify the framework itself, as well as remove the need to link
		//  against the base game. This effectively injects the framework in between the game and a script.
		addScriptEntity(new FPSCounter());
		
		for (IScriptEntity scriptEntity : scriptEntities) {
			scriptEntity.onInitializeEarly();
		}
		
		for (IScriptEntity scriptEntity : scriptEntities) {
			scriptEntity.onInitializeLate();
		}
	}
	
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
	
	public void addScriptEntity(IScriptEntity scriptEntity) {
		scriptEntities.add(scriptEntity);
		
		if (scriptEntity instanceof ITickCapable) {
			tickCapables.add((ITickCapable) scriptEntity);
		}
		
		if (scriptEntity instanceof IRenderCapable) {
			renderCapables.add((IRenderCapable) scriptEntity);
		}
		
		if (scriptEntity instanceof ISerializable) {
			serializables.add((ISerializable) scriptEntity);
		}
	}
}
