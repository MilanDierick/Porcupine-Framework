/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import init.paths.ModInfo;
import org.jetbrains.annotations.Nullable;
import org.porcupine.io.ISerializableTypeAdapterFactory;
import org.porcupine.utilities.Version;
import snake2d.util.file.FileManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class AggregateModule {
	public IRenderCapable renderCapable;
	public IScriptEntity scriptEntity;
	public ISerializable serializable;
	public ITickCapable tickCapable;
	
	private ModInfo modInfo;
	
	public AggregateModule() {
	
	}
	
	/**
	 * Creates a new instance of AggregateModule.
	 *
	 * @param object The object to check.
	 *
	 * @throws IllegalArgumentException If the object does not implement {@link IRenderCapable}.
	 */
	public AggregateModule(Object object) {
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
	}
	
	@SuppressWarnings("ImplicitDefaultCharsetUsage")
	public static void serialize(AggregateModule module, Path moduleJsonPath) {
		if (module.serializable == null) {
			return;
		}
		
		Class<? extends ISerializable> implementingClass = module.serializable.getClass();
		
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ISerializableTypeAdapterFactory(implementingClass))
		                             .setPrettyPrinting()
		                             .create();
		
		try (FileWriter writer = new FileWriter(moduleJsonPath.toFile())) {
			gson.toJson(module.serializable, implementingClass, writer);
		} catch (JsonIOException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({"ImplicitDefaultCharsetUsage", "unchecked"})
	public static @Nullable AggregateModule deserialize(String implementingClassName, Path moduleJsonPath) {
		try (FileReader reader = new FileReader(moduleJsonPath.toFile())) {
			Class<? extends ISerializable> implementingClass = (Class<? extends ISerializable>) Class.forName(
					implementingClassName);
			
			Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ISerializableTypeAdapterFactory(
					implementingClass)).create();
			
			ISerializable serializable = gson.fromJson(reader, implementingClass);
			
			return new AggregateModule(serializable);
		} catch (ClassNotFoundException | JsonIOException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return null;
		}
	}
	
	public String getName() {
		return modInfo.name;
	}
	
	public String getDescription() {
		return modInfo.desc;
	}
	
	public String getAuthor() {
		return modInfo.author;
	}
	
	public Version getVersion() {
		return new Version(modInfo.version);
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
