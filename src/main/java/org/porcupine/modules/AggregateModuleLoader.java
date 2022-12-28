/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import init.paths.PATHS;
import org.porcupine.utilities.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class AggregateModuleLoader {
	private static HashMap<String, URL> loadedClasses = new HashMap<>();
	
	public static ArrayList<Path> getModPaths() {
		String[] modFolderNames = PATHS.local().MODS.folders();
		Path modsPath = PATHS.local().MODS.get();
		ArrayList<Path> paths = new ArrayList<>();
		
		for (String modPathName : modFolderNames) {
			paths.add(modsPath.resolve(modPathName));
		}
		
		return paths;
	}
	
	public static ArrayList<Path> extendToScriptPaths(ArrayList<Path> modPaths) {
		ArrayList<Path> paths = new ArrayList<>();
		
		for (Path modPath : modPaths) {
			paths.add(modPath.resolve("V63/script/jar")); // TODO: Get the game version dynamically.
		}
		
		return paths;
	}
	
	public static ArrayList<Path> extendToJarPaths(ArrayList<Path> modulePaths) {
		ArrayList<Path> paths = new ArrayList<>();
		
		for (Path modulePath : modulePaths) {
			String[] jarNames = modulePath.toFile().list();
			
			if (jarNames == null) {
				continue;
			}
			
			for (String jarName : jarNames) {
				paths.add(modulePath.resolve(jarName));
			}
		}
		
		return paths;
	}
	
	public static Set<AggregateModule> extractModules(ArrayList<Path> jarPaths) {
		Set<AggregateModule> modules = new HashSet<>(); // We use a set to prevent duplicates.
		List<URL> urls = new ArrayList<>(jarPaths.size());
		
		try {
			for (Path jarPath : jarPaths) {
				urls.add(jarPath.toUri().toURL());
			}
		} catch (MalformedURLException e) {
			Logger.error("Failed to interpret jar path as URL, aborting.");
			return null;
		}
		
		URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[jarPaths.size()]), AggregateModuleLoader.class.getClassLoader());
		
		for (Path jarPath : jarPaths) {
			try {
				JarInputStream stream = new JarInputStream(urls.get(jarPaths.indexOf(jarPath)).openStream());
				JarEntry entry;
				
				while ((entry = stream.getNextJarEntry()) != null) {
					AggregateModule module = extractModule(loader, entry, urls.get(jarPaths.indexOf(jarPath)));
					
					if (module != null) {
						if (modules.add(module)) {
							Logger.info("Loaded module %s from %s", module.getName(), jarPath.getFileName());
						} else {
							Logger.warn("Duplicate module %s found in %s. Please contact the module developer.", module.getName(), jarPath.getFileName());
						}
					}
				}
			} catch (IOException e) {
				Logger.error("Failed to read jar file, aborting.");
				return null;
			}
		}
		
		return modules;
	}
	
	private static AggregateModule extractModule(URLClassLoader loader, JarEntry entry, URL currentJarPath) {
		if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
			return null;
		}
		
		String className = entry.getName().replace(".class", "").replace("/", ".");
		
		if (loadedClasses.containsKey(className)) {
			String loadedJar = loadedClasses.get(className).getFile();
			String currentJar = currentJarPath.getFile();
			
			// These strings are formatted as file:/path/to/jar.jar
			// We just need the file name.
			loadedJar = loadedJar.substring(loadedJar.lastIndexOf('/') + 1);
			currentJar = currentJar.substring(currentJar.lastIndexOf('/') + 1);
			
			Logger.warn("Duplicate class %s found in %s and %s. Please contact the module developers.", className, loadedJar, currentJar);
		}
		
		loadedClasses.put(className, currentJarPath);
		
		try {
			Class<?> clazz = loader.loadClass(className);
			
			if (IScriptEntity.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
				return new AggregateModule(clazz.getDeclaredConstructor().newInstance());
			}
		} catch (ClassNotFoundException e) {
			Logger.error("Failed to load class %s. Please contact the module developer.", className);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
		         NoSuchMethodException e) {
			Logger.error("Failed to instantiate class %s. Please contact the module developer.", className);
		}
		
		return null;
	}
}
