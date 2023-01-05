/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.utilities;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class Logger {
	
	/**
	 * @apiNote Setting this constructor to private prevents the class from being erroneously instantiated.
	 */
	private Logger() {
	}
	
	public static void debug(String message) {
		log(LogLevel.DEBUG, message);
	}
	
	public static void debug(String format, Object... args) {
		log(LogLevel.DEBUG, format, args);
	}
	
	public static void info(String message) {
		log(LogLevel.INFO, message);
	}
	
	public static void info(String format, Object... args) {
		log(LogLevel.INFO, format, args);
	}
	
	public static void warn(String message) {
		log(LogLevel.WARN, message);
	}
	
	public static void warn(String format, Object... args) {
		log(LogLevel.WARN, format, args);
	}
	
	public static void error(String message) {
		log(LogLevel.ERROR, message);
	}
	
	public static void error(String format, Object... args) {
		log(LogLevel.ERROR, format, args);
	}
	
	public static void fatal(String message) {
		log(LogLevel.FATAL, message);
	}
	
	public static void fatal(String format, Object... args) {
		log(LogLevel.FATAL, format, args);
	}
	
	private static void log(LogLevel level, String format, Object... args) {
		String printFormat = "[%s] [%s%s%s] %s%n";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH);
		String time = java.time.LocalTime.now().format(formatter);
		String color = getColorForLevel(level);
		System.out.printf(printFormat, time, color, level, "\u001B[0m", String.format(format, args));
	}
	
	private static void log(LogLevel level, String message) {
		String printFormat = "[%s] [%s%s%s] %s%n";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH);
		String time = java.time.LocalTime.now().format(formatter);
		String color = getColorForLevel(level);
		System.out.printf(printFormat, time, color, level, "\u001B[0m", message);
	}
	
	private static String getColorForLevel(LogLevel level) {
		switch (level) {
			case DEBUG:
				return "\u001B[36m";
			case INFO:
				return "\u001B[32m";
			case WARN:
				return "\u001B[33m";
			case ERROR:
				return "\u001B[31m";
			case FATAL:
				return "\u001B[35m";
			default:
				return "\u001B[0m";
		}
	}
}
