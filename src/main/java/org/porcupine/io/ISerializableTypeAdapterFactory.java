/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.io;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;
import org.porcupine.modules.ISerializable;

public class ISerializableTypeAdapterFactory implements TypeAdapterFactory {
	private final Class<? extends ISerializable> implementationClass;
	
	public ISerializableTypeAdapterFactory(Class<? extends ISerializable> implementationClass) {
		this.implementationClass = implementationClass;
	}
	
	/**
	 * Creates a type adapter for the given type, or returns null if this factory doesn't support type {@code T}.
	 *
	 * @param gson      The gson instance.
	 * @param type The type token.
	 * @param <T>       The type.
	 *
	 * @return the type adapter of the specified type.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> @Nullable TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (implementationClass.isAssignableFrom(type.getRawType())) {
			return (TypeAdapter<T>) gson.getAdapter(implementationClass);
		}
		
		return null;
	}
}
