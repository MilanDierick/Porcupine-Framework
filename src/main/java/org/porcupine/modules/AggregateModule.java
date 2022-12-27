/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import org.porcupine.interfaces.IRenderCapable;
import org.porcupine.interfaces.IScriptEntity;
import org.porcupine.interfaces.ISerializable;
import org.porcupine.interfaces.ITickCapable;

import java.util.Objects;

public class AggregateModule {
	public IRenderCapable renderCapable;
	public IScriptEntity scriptEntity;
	public ISerializable serializable;
	public ITickCapable tickCapable;
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		AggregateModule that = (AggregateModule) o;
		
		if (!Objects.equals(renderCapable, that.renderCapable))
			return false;
		if (!scriptEntity.equals(that.scriptEntity))
			return false;
		if (!Objects.equals(serializable, that.serializable))
			return false;
		return Objects.equals(tickCapable, that.tickCapable);
	}
	
	@Override
	public int hashCode() {
		int result = renderCapable != null ? renderCapable.hashCode() : 0;
		result = 31 * result + scriptEntity.hashCode();
		result = 31 * result + (serializable != null ? serializable.hashCode() : 0);
		result = 31 * result + (tickCapable != null ? tickCapable.hashCode() : 0);
		return result;
	}
	
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
