/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import java.util.Objects;

public class AggregateModule {
	public IRenderCapable renderCapable;
	public IScriptEntity scriptEntity;
	public ISerializable serializable;
	public ITickCapable tickCapable;
	
	public AggregateModule(Object object) throws IllegalArgumentException {
		if (object instanceof IRenderCapable) {
			renderCapable = (IRenderCapable) object;
		}
		
		if (object instanceof IScriptEntity) {
			scriptEntity = (IScriptEntity) object;
		}
		
		if (object instanceof ISerializable) {
			serializable = (ISerializable) object;
		}
		
		if (object instanceof ITickCapable) {
			tickCapable = (ITickCapable) object;
		}
		
		if (renderCapable == null && scriptEntity == null && serializable == null && tickCapable == null) {
			throw new IllegalArgumentException("Object is not a valid module");
		}
	}
	
	public String getName() {
		return scriptEntity.getName();
	}
	
	public String getDescription() {
		return scriptEntity.getDescription();
	}
	
	public String getAuthor() {
		return scriptEntity.getAuthor();
	}
	
	public String getVersion() {
		return scriptEntity.getVersion();
	}
	
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
}
