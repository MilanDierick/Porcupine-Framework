/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@SuppressWarnings({"DuplicateStringLiteralInspection", "AssertWithoutMessage", "ImplicitDefaultCharsetUsage"})
public class LoggerTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	
	@Test
	public void testDebug() {
		System.setOut(new PrintStream(outContent));
		
		Logger.debug("This is a debug message.");
		Assertions.assertTrue(outContent.toString().contains("This is a debug message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testDebugFormat() {
		System.setOut(new PrintStream(outContent));
		
		Logger.debug("This is a %s message.", "debug");
		Assertions.assertTrue(outContent.toString().contains("This is a debug message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testInfo() {
		System.setOut(new PrintStream(outContent));
		
		Logger.info("This is an info message.");
		Assertions.assertTrue(outContent.toString().contains("This is an info message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testInfoFormat() {
		System.setOut(new PrintStream(outContent));
		
		Logger.info("This is an %s message.", "info");
		Assertions.assertTrue(outContent.toString().contains("This is an info message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testWarn() {
		System.setOut(new PrintStream(outContent));
		
		Logger.warn("This is a warn message.");
		Assertions.assertTrue(outContent.toString().contains("This is a warn message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testWarnFormat() {
		System.setOut(new PrintStream(outContent));
		
		Logger.warn("This is a %s message.", "warn");
		Assertions.assertTrue(outContent.toString().contains("This is a warn message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testError() {
		System.setOut(new PrintStream(outContent));
		
		Logger.error("This is an error message.");
		Assertions.assertTrue(outContent.toString().contains("This is an error message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testErrorFormat() {
		System.setOut(new PrintStream(outContent));
		
		Logger.error("This is an %s message.", "error");
		Assertions.assertTrue(outContent.toString().contains("This is an error message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testFatal() {
		System.setOut(new PrintStream(outContent));
		
		Logger.fatal("This is a fatal message.");
		Assertions.assertTrue(outContent.toString().contains("This is a fatal message."));
		
		System.setOut(originalOut);
	}
	
	@Test
	public void testFatalFormat() {
		System.setOut(new PrintStream(outContent));
		
		Logger.fatal("This is a %s message.", "fatal");
		Assertions.assertTrue(outContent.toString().contains("This is a fatal message."));
		
		System.setOut(originalOut);
	}
}