/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

public interface ISerializable {
	/**
	 * Invoked when the object is being serialized.
	 */
	default void onSerialize(FilePutter writer) {
	
	}
	
	/**
	 * Invoked when the object is being deserialized.
	 */
	default void onDeserialize(FileGetter reader) {
	
	}
}
