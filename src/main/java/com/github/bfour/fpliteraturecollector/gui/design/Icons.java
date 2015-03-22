package com.github.bfour.fpliteraturecollector.gui.design;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.github.bfour.fpjgui.components.CachedImageIcon;

public enum Icons {

	APP_256("classpath:icons/app/icon_256.png"), APP_128(
			"classpath:icons/app/icon_128.png"), APP_32(
			"classpath:icons/app/icon_32.png"), APP_24(
			"classpath:icons/app/icon_24.png"), APP_16(
			"classpath:icons/app/icon_16.png"),	
	
	PERSON_GROUP("classpath:icons/group.png"),
	BOOKS("classpath:icons/books.png"),
	DUPLICATE("classpath:icons/column_double.png"),
	
	ADD("classpath:icons/add.png"),
	ADD_24("classpath:icons/add_24.png"),
	
	PLAY("classpath:icons/control_play_blue.png"),
	PLAY_24("classpath:icons/control_play_blue_24.png"),
	STOP("classpath:icons/control_stop_blue.png"),
	STOP_24("classpath:icons/control_stop_blue_24.png"),
	PAUSE("classpath:icons/control_pause_blue.png"),
	PAUSE_24("classpath:icons/control_pause_blue_24.png"),
	RERUN("classpath:icons/control_repeat_blue.png"),
	RERUN_24("classpath:icons/control_repeat_blue_24.png"),
	
	FINISHED_16("classpath:icons/tick_16.png"),
	QUEUED_16("classpath:icons/time_16.png"),
	IDLE_16("classpath:icons/traffic_lights_red_16.png")
	
	;
	
	private String path;
	private ImageIcon icon;

	/**
	 * Whether this icon shall be loaded once the first icon is retrieved or on
	 * demand (false).
	 */
	private boolean prefetch;

	/**
	 * Creates an icon.
	 * 
	 * @param path
	 *            The path of the icon picture.
	 */
	Icons(String path) {
		this(path, false);
	}

	/**
	 * Creates an icon.
	 * 
	 * @param path
	 *            The path of the icon picture.
	 * @param prefetch
	 *            Whether this icon shall be loaded once the first icon is
	 *            retrieved (true) or on demand (false).
	 */
	Icons(String path, boolean prefetch) {
		this.path = path;
		this.prefetch = prefetch;
		if (prefetch) {
			this.icon = CachedImageIcon.get(path);
		}
	}

	/**
	 * Gets path associated with this icon.
	 * 
	 * @return path, format compatible with
	 *         {@link org.springframework.core.io.DefaultResourceLoader}
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Gets the icon.
	 * 
	 * @return the icon
	 */
	public ImageIcon getIcon() {
		if (prefetch) {
			return this.icon;
		} else {
			this.icon = CachedImageIcon.get(path);
			this.prefetch = true;
			return this.icon;
		}
	}

	/**
	 * Loads the application icons.
	 * 
	 * @return a list of application icons, each for a particular size-range
	 */
	public static List<Image> getAppIcons() {
		List<Image> appIcons = new ArrayList<Image>();
		if (Icons.APP_256.getIcon() != null) {
			appIcons.add(Icons.APP_256.getIcon().getImage());
		}
		if (Icons.APP_128.getIcon() != null) {
			appIcons.add(Icons.APP_128.getIcon().getImage());
		}
		if (Icons.APP_32.getIcon() != null) {
			appIcons.add(Icons.APP_32.getIcon().getImage());
		}
		if (Icons.APP_24.getIcon() != null) {
			appIcons.add(Icons.APP_24.getIcon().getImage());
		}
		if (Icons.APP_16.getIcon() != null) {
			appIcons.add(Icons.APP_16.getIcon().getImage());
		}
		return appIcons;
	}	
	
}