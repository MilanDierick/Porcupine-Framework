/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import init.paths.ModInfo;
import init.paths.PATHS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.porcupine.io.FileManager;
import org.porcupine.utilities.Logger;
import org.porcupine.utilities.Version;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.*;

public final class AggregateModuleLoader {
	private static final Map<String, URI> loadedClasses;
	
	static {
		loadedClasses = new HashMap<>();
	}
	
	/**
	 * @apiNote Setting this constructor to private prevents the class from being erroneously instantiated.
	 */
	private AggregateModuleLoader() {
		throw new AssertionError("This class cannot be instantiated.");
	}
	
	public static Collection<AggregateModule> loadModules() {
		Collection<AggregateModuleInfo> moduleInfos = loadModInfos();
		Collection<AggregateModule> modules = new ArrayList<>(moduleInfos.size());
		URLClassLoader loader = createLoader(moduleInfos);
		
		for (AggregateModuleInfo info : moduleInfos) {
			for (JarFile jarFile : info.jarFiles) {
				Collection<AggregateModule> modulesInJar = processJar(jarFile, loader);
				modulesInJar.forEach(module -> module.modInfo = info);
				modules.addAll(processJar(jarFile, loader));
			}
		}
		
		return modules;
	}
	
	private static URLClassLoader createLoader(Collection<? extends AggregateModuleInfo> moduleInfos) {
		URL[] urls = moduleInfos.stream()
				.flatMap(moduleInfo -> moduleInfo.jarFiles.stream())
				.map(jarFile -> {
					try {
						return new File(jarFile.getName()).toURI().toURL();
					} catch (MalformedURLException e) {
						throw new RuntimeException(e);
					}
				}).toArray(URL[]::new);
		
		return new URLClassLoader(urls, AggregateModuleLoader.class.getClassLoader());
	}
	
	private static Collection<AggregateModuleInfo> loadModInfos() {
		Collection<AggregateModuleInfo> moduleInfos = new ArrayList<>(PATHS.currentMods().size());
		
		for (ModInfo info : PATHS.currentMods()) {
			AggregateModuleInfo moduleInfo = null;
			Path absolutePath = Paths.get(info.absolutePath, File.separator + "V63");
			Path campaigns = absolutePath.resolve("campaigns");
			Path examples = absolutePath.resolve("examples");
			Path saves = absolutePath.resolve("saves");
			Path scripts = absolutePath.resolve("script" + File.separator + "jar");
			
			AggregateModulePaths paths = new AggregateModulePaths(absolutePath, campaigns, examples, saves, scripts);
			Collection<JarFile> jarFiles = FileManager.getJarFilesInDirectory(paths.scriptsPath);
			
			if (jarFiles == null) {
				Logger.warn("Failed to load jar files from " + paths.scriptsPath);
			} else {
				moduleInfo = new AggregateModuleInfo(info.name, info.desc, info.author, new Version(info.version), paths, jarFiles);
			}
			
			if (moduleInfo != null) {
				moduleInfos.add(moduleInfo);
			}
		}
		
		if (moduleInfos.isEmpty()) {
			Logger.warn("No jar files found in any of the modules.");
			return Collections.emptyList();
		}
		
		return moduleInfos;
	}
	
	@SuppressWarnings({"DynamicRegexReplaceableByCompiledPattern", "HardcodedFileSeparator"})
	private static @NotNull Collection<AggregateModule> processJar(JarFile jarFile, URLClassLoader loader) {
		Collection<AggregateModule> modules = new ArrayList<>();
		Enumeration<JarEntry> entries = jarFile.entries();
		
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			
			if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
				continue;
			}
			
			String className = entry.getName().replace(".class", "").replace('/', '.');
			
			AggregateModule module = loadClass(loader, className, jarFile);
			
			if (module != null) {
				modules.add(module);
			}
		}
		
		return modules;
	}
	
	private static @Nullable AggregateModule loadClass(URLClassLoader loader, String name, JarFile file) {
		try {
			Class<?> clazz = loader.loadClass(name);
			
			if (IScriptEntity.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
				return new AggregateModule(clazz.getConstructor().newInstance());
			}
		} catch (ClassNotFoundException e) {
			Logger.error("Failed to load class %s from jar file %s.", name, file.getName());
		} catch (InstantiationException e) {
			Logger.error("Failed to instantiate class %s from jar file %s.", name, file.getName());
		} catch (IllegalAccessException e) {
			Logger.error("Failed to access class %s from jar file %s.", name, file.getName());
		} catch (InvocationTargetException e) {
			Logger.error("Failed to invoke constructor of class %s from jar file %s.", name, file.getName());
		} catch (NoSuchMethodException e) {
			Logger.error("Failed to find constructor of class %s from jar file %s.", name, file.getName());
		}
		
		return null;
	}
}
