/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.utilities;

import java.util.Objects;

public class Version {
	private final int major;
	private final int minor;
	private final int patch;
	private final String suffix;
	
	public Version(int major, int minor, int patch, String suffix) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.suffix = suffix;
	}
	
	public Version(int major, int minor, int patch) {
		this(major, minor, patch, "");
	}
	
	public Version(int major, int minor) {
		this(major, minor, 0);
	}
	
	public Version(int major) {
		this(major, 0);
	}
	
	public Version(String version) {
		String[] split = version.split("\\.");
		if (split.length == 0) {
			throw new IllegalArgumentException("Invalid version string");
		}
		
		major = Integer.parseInt(split[0]);
		minor = split.length > 1 ? Integer.parseInt(split[1]) : 0;
		patch = split.length > 2 ? Integer.parseInt(split[2]) : 0;
		suffix = split.length > 3 ? split[3] : "";
	}
	
	public int getMajor() {
		return major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public int getPatch() {
		return patch;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	@Override
	public String toString() {
		return major + "." + minor + "." + patch + suffix;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		Version version = (Version) o;
		
		if (major != version.major)
			return false;
		if (minor != version.minor)
			return false;
		if (patch != version.patch)
			return false;
		return suffix.equals(version.suffix);
	}
	
	@Override
	public int hashCode() {
		int result = major;
		result = 31 * result + minor;
		result = 31 * result + patch;
		result = 31 * result + suffix.hashCode();
		return result;
	}
}
