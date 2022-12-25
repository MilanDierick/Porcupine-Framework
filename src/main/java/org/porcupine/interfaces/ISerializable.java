/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.interfaces;

import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

public interface ISerializable {
    /**
     * Invoked when the object is being serialized.
     *
     * @param writer The writer to write to.
     */
    void onSerialize(FilePutter writer);

    /**
     * Invoked when the object is being deserialized.
     *
     * @param reader The reader to read from.
     */
    void onDeserialize(FileGetter reader);
}
