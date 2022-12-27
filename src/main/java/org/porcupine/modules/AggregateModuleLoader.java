/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import init.paths.PATHS;
import org.porcupine.interfaces.IScriptEntity;
import org.porcupine.utilities.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class AggregateModuleLoader {
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
		
		URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[jarPaths.size()]));
		
		for (Path jarPath : jarPaths) {
			try {
				JarInputStream stream = new JarInputStream(urls.get(jarPaths.indexOf(jarPath)).openStream());
				JarEntry entry;
				
				while ((entry = stream.getNextJarEntry()) != null) {
					AggregateModule module = extractModule(loader, entry);
					
					if (module != null) {
						modules.add(module);
					}
				}
			} catch (IOException e) {
				Logger.error("Failed to read jar file, aborting.");
				return null;
			}
		}
		
		return modules;
	}
	
	private static AggregateModule extractModule(URLClassLoader loader, JarEntry entry) {
		if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
			return null;
		}
		
		String className = entry.getName().replace(".class", "").replace("/", ".");
		
		try {
			Class<?> clazz = loader.loadClass(className);
			
			if (IScriptEntity.class.isAssignableFrom(clazz)) {
				return new AggregateModule().tryCreate(clazz);
			}
		} catch (ClassNotFoundException e) {
			Logger.error("Failed to load class {} from jar {}.\nPlease contact the module developer.", className, entry.getName());
		}
		
		return null;
	}
}
