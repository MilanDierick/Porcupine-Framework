/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import init.paths.ModInfo;
import init.paths.PATHS;
import org.porcupine.io.FileManager;
import org.porcupine.utilities.Version;

import java.io.File;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("BooleanVariableAlwaysNegated")
public final class AggregateModuleLoader {
	private static final Collection<AggregateModuleInfo> moduleInfos;
	private static final Collection<AggregateModule> modules;
	private static final Map<AggregateModuleInfo, AggregateModuleConfig> moduleConfigs;
	private static final Map<AggregateModuleInfo, Collection<String>> moduleClasses;
	private static boolean modulesLoaded;
	
	static {
		moduleInfos = new ArrayList<>();
		modules = new ArrayList<>();
		moduleConfigs = new HashMap<>();
		moduleClasses = new HashMap<>();
		modulesLoaded = false;
	}
	
	/**
	 * @apiNote Setting this constructor to private prevents the class from being erroneously instantiated.
	 */
	private AggregateModuleLoader() {
		throw new AssertionError("This class cannot be instantiated.");
	}
	
	public static Collection<AggregateModule> getModules() {
		if (!modulesLoaded) {
			createModuleInfos();
			createModuleConfigs();
			prepareModuleClasses();
			createAggregateModules();
			modulesLoaded = true;
		}
		
		return Collections.unmodifiableCollection(modules);
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
	
	private static void createModuleConfigs() {
		for (AggregateModuleInfo info : moduleInfos) {
			File configFile = info.paths.absolutePath.resolve("config.properties").toFile();
			Properties properties = FileManager.loadProperties(configFile);
			AggregateModuleConfig config = new AggregateModuleConfig(properties);
			moduleConfigs.put(info, config);
		}
	}
	
	@SuppressWarnings({"DynamicRegexReplaceableByCompiledPattern", "HardcodedFileSeparator"})
	private static void prepareModuleClasses() {
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
			
			moduleClasses.put(info, classNames);
		}
	}
	
	private static void createAggregateModules() {
		for (AggregateModuleInfo info : moduleInfos) {
			for (String className : moduleClasses.get(info)) {
				try (URLClassLoader classLoader = createLoader()) {
					Class<?> clazz = classLoader.loadClass(className);
					
					if (IScriptEntity.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
						AggregateModule module = new AggregateModule(clazz.getConstructor().newInstance(), info);
						
						module.modInfo = info;
						module.modConfig = moduleConfigs.get(info);
						
						modules.add(module);
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
