/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.modules;

import org.jetbrains.annotations.NonNls;
import org.porcupine.utilities.Version;

/**
 * This class is used to store metadata about a module.
 */
public class AggregateModuleMetadata {
	private String name;
	private String description;
	private String author;
	private Version version;
	
	/**
	 * Creates a new instance of AggregateModuleMetadata.
	 */
	public AggregateModuleMetadata() {
		this("", "", "", new Version(0));
	}
	
	/**
	 * Creates a new instance of AggregateModuleMetadata with the specified values.
	 *
	 * @param name        The name of the module.
	 * @param description A description of the module.
	 * @param author      The author of the module.
	 * @param version     The {@link Version} of the module.
	 */
	public AggregateModuleMetadata(String name, String description, String author, Version version) {
		this.name = name;
		this.description = description;
		this.author = author;
		this.version = version;
	}
	
	/**
	 * @return The name of the module.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name of the module.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return A description of the module.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description A description of the module.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return The author of the module.
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @param author The author of the module.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * @return The {@link Version} of the module.
	 */
	public Version getVersion() {
		return version;
	}
	
	/**
	 * @param version The {@link Version} of the module.
	 */
	public void setVersion(Version version) {
		this.version = version;
	}
	
	@Override
	@NonNls
	public String toString() {
		return "AggregateModuleMetadata{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", author='" + author + '\'' + ", version=" + version + '}';
	}
}
