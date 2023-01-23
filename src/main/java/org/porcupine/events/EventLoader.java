/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import init.paths.ModInfo;
import init.paths.PATHS;
import org.porcupine.io.FileManager;
import org.porcupine.modules.*;
import org.porcupine.utilities.Version;

import java.io.File;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("BooleanVariableAlwaysNegated")
public final class EventLoader {
	private static final Collection<AggregateModuleInfo> moduleInfos;
	private static final Collection<IGlobalEvent> globalEvents;
	private static final Collection<IRoomEvent> roomEvents;
	private static final Map<AggregateModuleInfo, Collection<String>> eventClasses;
	private static boolean eventsLoaded;
	
	static {
		moduleInfos = new ArrayList<>();
		globalEvents = new ArrayList<>();
		roomEvents = new ArrayList<>();
		eventClasses = new HashMap<>();
		eventsLoaded = false;
	}
	
	private EventLoader() {
		throw new AssertionError("This class cannot be instantiated");
	}
	
	public static Collection<IGlobalEvent> getGlobalEvents() {
		loadEvents();
		return Collections.unmodifiableCollection(globalEvents);
	}
	
	public static Collection<IRoomEvent> getRoomEvents() {
		loadEvents();
		return Collections.unmodifiableCollection(roomEvents);
	}
	
	private static void loadEvents() {
		if (!eventsLoaded) {
			createModuleInfos();
			prepareEventClasses();
			createEventInstances();
			eventsLoaded = true;
		}
	}
	
	private static void createModuleInfos() {
		for (ModInfo info : PATHS.currentMods()) {
			Path absolutePath = Paths.get(info.absolutePath, File.separator + "V63");
			Path campaignsPath = absolutePath.resolve("campaigns");
			Path examplesPath = absolutePath.resolve("examples");
			Path savesPath = absolutePath.resolve("saves");
			Path scriptsPath = absolutePath.resolve("script");
			
			Version version = new Version(info.version);
			
			AggregateModulePaths paths = new AggregateModulePaths(
					absolutePath,
					campaignsPath,
					examplesPath,
					savesPath,
					scriptsPath
			);
			
			Collection<JarFile> jarFiles = FileManager.getJarFilesInDirectory(paths.scriptsPath.resolve("jar"));
			
			moduleInfos.add(new AggregateModuleInfo(
					info.name,
					info.desc,
					info.author,
					version,
					paths,
					jarFiles
			));
		}
	}
	
	@SuppressWarnings({"DynamicRegexReplaceableByCompiledPattern", "HardcodedFileSeparator"})
	private static void prepareEventClasses() {
		for (AggregateModuleInfo info : moduleInfos) {
			Collection<String> classNames = new ArrayList<>();
			
			for (JarFile jarFile : info.jarFiles) {
				Enumeration<JarEntry> entries = jarFile.entries();
				
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					
					if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
						continue;
					}
					
					String className = entry.getName().replace(".class", "").replace('/', '.');
					
					if (className.contains("package-info")) {
						continue;
					}
					
					classNames.add(className);
				}
			}
			
			eventClasses.put(info, classNames);
		}
	}
	
	private static void createEventInstances() {
		for (AggregateModuleInfo info : moduleInfos) {
			for (String className : eventClasses.get(info)) {
				try (URLClassLoader classLoader = createLoader()) {
					Class<?> clazz = classLoader.loadClass(className);
					
					if (IGlobalEvent.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
						IGlobalEvent event = (IGlobalEvent) clazz.getConstructor().newInstance();
						globalEvents.add(event);
					} else if (IRoomEvent.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
						IRoomEvent event = (IRoomEvent) clazz.getConstructor().newInstance();
						roomEvents.add(event);
					}
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}
	
	private static URLClassLoader createLoader() {
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
}
