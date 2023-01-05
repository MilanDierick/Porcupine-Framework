/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.statistics;

public interface IStats {
	/**
	 * This method is used to refresh the statistics.
	 *
	 * @apiNote This method is called by the framework, and should not be called by the user.
	 */
	void refresh();
}
