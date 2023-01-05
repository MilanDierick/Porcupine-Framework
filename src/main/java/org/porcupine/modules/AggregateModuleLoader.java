/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import init.paths.PATHS;
import org.jetbrains.annotations.Nullable;
import org.porcupine.utilities.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public final class AggregateModuleLoader {
	private static final Map<String, URI> loadedClasses = new HashMap<>();
	
	/**
	 * @apiNote Setting this constructor to private prevents the class from being erroneously instantiated.
	 */
	private AggregateModuleLoader() {
	}
	
	public static Iterable<Path> getModPaths() {
		String[] modFolderNames = PATHS.local().MODS.folders();
		Path modsPath = PATHS.local().MODS.get();
		Collection<Path> paths = new ArrayList<>();
		
		for (String modPathName : modFolderNames) {
			paths.add(modsPath.resolve(modPathName));
		}
		
		return paths;
	}
	
	@SuppressWarnings("HardcodedFileSeparator")
	public static Collection<Path> extendToScriptPaths(Iterable<? extends Path> modPaths) {
		Collection<Path> paths = new ArrayList<>();
		
		for (Path modPath : modPaths) {
			paths.add(modPath.resolve("V63/script/jar")); // TODO: Get the game version dynamically.
		}
		
		return paths;
	}
	
	public static List<Path> extendToJarPaths(Collection<? extends Path> modulePaths) {
		return modulePaths.stream().map(Path::toFile).filter(File::isDirectory)
		                  .flatMap(dir -> Arrays.stream(Objects.requireNonNull(dir.listFiles())))
		                  .filter(file -> file.isFile() && file.getName().endsWith(".jar")).map(File::toPath)
		                  .collect(Collectors.toCollection(ArrayList::new));
	}
	
	// TODO: Simplify this method.
	@SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
	public static @Nullable Set<AggregateModule> extractModules(List<? extends Path> jarPaths) {
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
		
		URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[jarPaths.size()]),
		                                           AggregateModuleLoader.class.getClassLoader()
		);
		
		for (Path jarPath : jarPaths) {
			JarInputStream stream;
			try {
				stream = new JarInputStream(urls.get(jarPaths.indexOf(jarPath)).openStream());
			} catch (IOException e) {
				Logger.error("Failed to open jar stream, aborting.");
				return null;
			}
			
			try {
				JarEntry entry;
				
				while ((entry = stream.getNextJarEntry()) != null) {
					AggregateModule module = extractModule(loader, entry, urls.get(jarPaths.indexOf(jarPath)).toURI());
					
					if (module != null) {
						if (modules.add(module)) {
							Logger.info("Loaded module %s from %s", module.getName(), jarPath.getFileName());
						} else {
							Logger.warn("Duplicate module %s found in %s. Please contact the module developer.",
							            module.getName(),
							            jarPath.getFileName()
							);
						}
					}
				}
			} catch (IOException e) {
				Logger.error("Failed to read jar file, aborting.");
				return null;
			} catch (URISyntaxException e) {
				Logger.error("Failed to interpret jar path as URI, aborting.");
				return null;
			} finally {
				try {
					loader.close();
					stream.close();
				} catch (IOException e) {
					Logger.error("Failed to close URLClassLoader, aborting.");
				}
			}
			
			return null;
		}
		
		return modules;
	}
	
	/**
	 * Constructs a File instance from a file URI. Returns null if it's not a file URI.
	 *
	 * @param uri a URI instance (never null).
	 *
	 * @return null if uri is not a file URI or a File instance
	 */
	public static File getFile(URI uri) {
		assert uri != null;
		
		@Nullable File result;
		String scheme = uri.getScheme();
		if ((scheme == null) || !"file".equals(scheme.toLowerCase(Locale.ROOT))) { //NOI18N
			result = null;
		} else {
			try {
				result = new File(uri);
			} catch (IllegalArgumentException x) {
				result = null;
			}
		}
		
		return result;
	}
	
	@SuppressWarnings({"DuplicateStringLiteralInspection", "DynamicRegexReplaceableByCompiledPattern"})
	private static @Nullable AggregateModule extractModule(URLClassLoader loader, JarEntry entry, URI currentJarPath) {
		if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
			return null;
		}
		
		String className = entry.getName().replace(".class", "").replace(File.separatorChar, '.');
		
		if (loadedClasses.containsKey(className)) {
			String loadedJar = getFile(loadedClasses.get(className)).getPath();
			String currentJar = getFile(currentJarPath).getPath();
			
			// These strings are formatted as file:/path/to/jar.jar
			// We just need the file name.
			loadedJar = loadedJar.substring(loadedJar.lastIndexOf(File.separatorChar) + 1);
			currentJar = currentJar.substring(currentJar.lastIndexOf(File.separatorChar) + 1);
			
			Logger.warn("Duplicate class %s found in %s and %s. Please contact the module developers.",
			            className,
			            loadedJar,
			            currentJar
			);
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
