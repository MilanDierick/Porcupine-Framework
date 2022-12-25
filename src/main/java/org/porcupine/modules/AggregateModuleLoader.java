/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import init.paths.PATHS;
import org.porcupine.interfaces.IScriptEntity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
	
	public static ArrayList<AggregateModule> extractModules(ArrayList<Path> jarPaths) {
		ArrayList<AggregateModule> modules = new ArrayList<>();
		
		for (Path jarPath : jarPaths) {
			try (JarInputStream stream = new JarInputStream(Files.newInputStream(jarPath))) {
				JarEntry entry;
				
				while ((entry = stream.getNextJarEntry()) != null) {
					String entryName = entry.getName();
					
					if (entry.getName().endsWith(".class")) {
						String className = entryName.replace(".class", "").replace("/", ".");
						Class<?> clazz = Class.forName(className);
						
						// We want to ensure that we are not picking up any interface classes. We only want to pick up
						// classes that implement the interfaces. This is why we check if the class is an interface.
						if (IScriptEntity.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
							AggregateModule module = new AggregateModule();
							modules.add(module.tryCreate(clazz.getDeclaredConstructor().newInstance()));
						}
					}
				}
			} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException |
			         InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		
		return modules;
	}
}
