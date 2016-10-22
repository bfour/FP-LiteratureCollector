/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.gui.design;

import java.awt.Color;

/**
 * Defines the different colors.
 * 
 * @author Florian Pollak
 *
 */
public enum Colors {

	/* @formatter:off */
	CREATE_QUERY_DROPSHADOW(new Color(0,116,0)),
	EDIT_QUERY_DROPSHADOW(new Color(0,116,116)),
	QUERY_CRAWLING_DROPSHADOW(new Color(116,0,0)),
	QUERY_QUEUED_DROPSHADOW(new Color(0,0,116)),

	;
	/* @formatter:on */

	private Color color;

	/**
	 * Sets the color.
	 * 
	 * @param color
	 *            the color to set
	 */
	Colors(Color color) {
		this.color = color;
	}

	/**
	 * Return color.
	 * 
	 * @return color
	 */
	public Color getColor() {
		return this.color;
	}

}
