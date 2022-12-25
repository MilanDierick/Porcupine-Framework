/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import org.porcupine.interfaces.IRenderCapable;
import org.porcupine.interfaces.IScriptEntity;
import org.porcupine.interfaces.ISerializable;
import org.porcupine.interfaces.ITickCapable;

public class AggregateModule {
	public IRenderCapable renderCapable;
	public IScriptEntity scriptEntity;
	public ISerializable serializable;
	public ITickCapable tickCapable;
	
	public AggregateModule tryCreate(Object entity) throws InvalidAggregateException {
		if (entity instanceof IScriptEntity) {
			scriptEntity = (IScriptEntity) entity;
		}
		
		if (entity instanceof IRenderCapable) {
			renderCapable = (IRenderCapable) entity;
		}
		
		if (entity instanceof ISerializable) {
			serializable = (ISerializable) entity;
		}
		
		if (entity instanceof ITickCapable) {
			tickCapable = (ITickCapable) entity;
		}
		
		if (renderCapable == null && scriptEntity == null && serializable == null && tickCapable == null) {
			throw new InvalidAggregateException("The entity is not an instance of any of the interfaces.");
		}
		
		return this;
	}
}
