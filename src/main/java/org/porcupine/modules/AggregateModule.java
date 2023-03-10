/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import org.porcupine.utilities.Version;

import java.util.Objects;

public class AggregateModule {
	public IRenderCapable renderCapable;
	public IScriptEntity scriptEntity;
	public ISerializable serializable;
	public ITickCapable tickCapable;
	
	public AggregateModuleInfo modInfo;
	public AggregateModuleConfig modConfig;
	
	public AggregateModule() {
	
	}
	
	/**
	 * Creates a new instance of AggregateModule.
	 *
	 * @param object The object to check.
	 *
	 * @throws IllegalArgumentException If the object does not implement {@link IRenderCapable}.
	 */
	public AggregateModule(Object object, AggregateModuleInfo modInfo) {
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
		
		if (!isValidModule()) {
			throw new IllegalArgumentException("Object is not a valid module");
		}
		
		this.modInfo = modInfo.clone();
	}
	
	public String getName() {
		return modInfo.name;
	}
	
	public String getDescription() {
		return modInfo.description;
	}
	
	public String getAuthor() {
		return modInfo.author;
	}
	
	public Version getVersion() {
		return modInfo.version;
	}
	
	private boolean isValidModule() {
		return !(renderCapable == null && scriptEntity == null && serializable == null && tickCapable == null);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		AggregateModule other = (AggregateModule) obj;
		
		if (!Objects.equals(renderCapable, other.renderCapable))
			return false;
		if (!scriptEntity.equals(other.scriptEntity))
			return false;
		if (!Objects.equals(serializable, other.serializable))
			return false;
		return Objects.equals(tickCapable, other.tickCapable);
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
