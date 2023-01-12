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
import java.io.IOException;
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
	
	private static @Nullable AggregateModuleInfo loadModInfo(@NotNull ModInfo modInfo) {
		Path absolutePath = Paths.get(modInfo.absolutePath, File.separator + "V63");
		Path campaigns = absolutePath.resolve("campaigns");
		Path examples = absolutePath.resolve("examples");
		Path saves = absolutePath.resolve("saves");
		Path scripts = absolutePath.resolve("script" + File.separator + "jar");
		
		AggregateModulePaths paths = new AggregateModulePaths(absolutePath, campaigns, examples, saves, scripts);
		Collection<JarFile> jarFiles = FileManager.getObjectsInDirectory(paths.scriptsPath, ".jar", JarFile.class);
		
		if (jarFiles == null) {
			Logger.warn("Failed to load jar files from " + paths.scriptsPath);
			return null;
		}
		
		return new AggregateModuleInfo(modInfo.name, modInfo.desc, modInfo.author, new Version(modInfo.version), paths, jarFiles);
	}
	
	/**
	 * @throws IOException       if an I/O error occurs when the URLClassLoader tries to load a class.
	 * @throws SecurityException if a security manager is present, and it denies {@link RuntimePermission}
	 *                           {@code accessClassInPackage}.
	 */
	public static Collection<AggregateModuleInfo> loadModInfos() throws IOException {
		Collection<AggregateModuleInfo> moduleInfos = new ArrayList<>(PATHS.currentMods().size());
		
		for (ModInfo info : PATHS.currentMods()) {
			AggregateModuleInfo moduleInfo = loadModInfo(info);
			
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
		
		URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[jarPaths.size()]), AggregateModuleLoader.class.getClassLoader());
		
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
							Logger.warn("Duplicate module %s found in %s. Please contact the module developer.", module.getName(), jarPath.getFileName());
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
