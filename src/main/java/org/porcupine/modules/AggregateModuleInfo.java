/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import org.porcupine.utilities.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarFile;

public class AggregateModuleInfo {
	public final String name;
	public final String description;
	public final String author;
	public final Version version;
	public final AggregateModulePaths paths;
	public final Collection<JarFile> jarFiles;
	
	public AggregateModuleInfo(
			String name,
			String description,
			String author,
			Version version,
			AggregateModulePaths paths,
			Collection<JarFile> jarFiles
	) {
		this.name = name;
		this.description = description;
		this.author = author;
		this.version = version;
		this.paths = paths;
		this.jarFiles = new ArrayList<>(jarFiles);
	}
}
