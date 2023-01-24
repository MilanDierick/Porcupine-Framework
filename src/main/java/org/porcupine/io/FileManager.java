/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.porcupine.utilities.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileManager {
	
	private FileManager() {
		throw new AssertionError("This class cannot be instantiated.");
	}
	
	public static @Nullable Collection<Path> getFilesInDirectory(@NotNull Path directory, @NotNull String extension) {
		if (!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("The given path is not a directory.");
		}
		
		if (!Files.isReadable(directory)) {
			throw new IllegalArgumentException("The given directory is not readable.");
		}
		
		try (Stream<Path> stream = Files.walk(directory)) {
			return stream.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(extension))
					.collect(Collectors.toList());
		} catch (SecurityException e) {
			Logger.error("Security exception while trying to get files in directory: %s", directory);
		} catch (IOException e) {
			Logger.error("IO exception while trying to get files in directory: %s", directory);
		}
		
		return null;
	}
	
	public static @Nullable Collection<Path> getFilesInDirectory(
			@NotNull Path directory,
			@NotNull Iterable<String> extensions
	) {
		Collection<Path> files = null;
		
		for (String extension : extensions) {
			Collection<Path> filesInDirectory = getFilesInDirectory(directory, extension);
			
			if (filesInDirectory != null) {
				if (files == null) {
					files = new ArrayList<>();
				}
				
				files.addAll(filesInDirectory);
			}
		}
		
		return files;
	}
	
	public static @Nullable Collection<Path> getFilesInDirectories(
			@NotNull Iterable<? extends Path> directories,
			@NotNull String extension
	) {
		Collection<Path> files = null;
		
		for (Path directory : directories) {
			Collection<Path> filesInDirectory = getFilesInDirectory(directory, extension);
			
			if (filesInDirectory != null) {
				if (files == null) {
					files = new ArrayList<>();
				}
				
				files.addAll(filesInDirectory);
			}
		}
		
		return files;
	}
	
	public static @Nullable Collection<Path> getFilesInDirectories(
			@NotNull Iterable<? extends Path> directories,
			@NotNull Iterable<String> extensions
	) {
		Collection<Path> files = null;
		
		for (String extension : extensions) {
			Collection<Path> filesInDirectories = getFilesInDirectories(directories, extension);
			
			if (filesInDirectories != null) {
				if (files == null) {
					files = new ArrayList<>();
				}
				
				files.addAll(filesInDirectories);
			}
		}
		
		return files;
	}
	
	public static @Nullable JarFile readJarFileFromPath(@NotNull Path file) {
		if (!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("The given path is not a file.");
		}
		
		if (!Files.isReadable(file)) {
			throw new IllegalArgumentException("The given file is not readable.");
		}
		
		try {
			return new JarFile(file.toFile());
		} catch (IOException e) {
			Logger.error("IO exception while trying to transform path to jar file: %s", file);
		}
		
		return null;
	}
	
	public static @Nullable Collection<JarFile> getJarFilesInDirectory(@NotNull Path directory) {
		Iterable<Path> files = getFilesInDirectory(directory, ".jar");
		
		if (files == null) {
			return null;
		}
		
		Collection<JarFile> jarFiles = new ArrayList<>();
		
		for (Path file : files) {
			JarFile object = readJarFileFromPath(file);
			
			if (object != null) {
				jarFiles.add(object);
			}
		}
		
		return jarFiles;
	}
	
	public static @NotNull Properties loadProperties(@NotNull File configFile) {
		Properties properties = new Properties();
		
		try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
			properties.load(fileInputStream);
		} catch (IOException e) {
			Logger.error("IO exception while trying to load properties from file: %s", configFile);
		}
		
		return properties;
	}
}
