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
	QUERY_CRAWLING_DROPSHADOW(new Color(116,116,0)),
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
