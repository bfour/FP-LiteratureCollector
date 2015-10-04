package com.github.bfour.fpliteraturecollector.gui.design;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */


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
	
	CRAWLING_32("classpath:icons/gears_32.png"),
			
	PERSON_GROUP("classpath:icons/group.png"),
	BOOKS("classpath:icons/books.png"),
	BOOKS_20("classpath:icons/books_20.png"),
	DUPLICATE("classpath:icons/column_double.png"),
	CONFIG("classpath:icons/setting_tools_32.png"),
	
	TAG_16("classpath:icons/three_tags_16.png"),
	TAG_32("classpath:icons/three_tags_32.png"),
	
	CROSS_12("classpath:icons/cross_12.png"),
	
	ADD("classpath:icons/add.png"),
	ADD_16("classpath:icons/add_16.png"),
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
	FINISHED_20("classpath:icons/tick_20.png"),
	FINISHED_WITH_ERROR_16("classpath:icons/exclamation_16.png"),
	FINISHED_WITH_ERROR_20("classpath:icons/exclamation_20.png"),
	QUEUED_16("classpath:icons/time_16.png"),
	QUEUED_20("classpath:icons/time_20.png"),
	IDLE_16("classpath:icons/traffic_lights_red_16.png"),
	IDLE_20("classpath:icons/traffic_lights_red_20.png"),
	
	QUEUE_UP_20("classpath:icons/abstract/flatArrowUp_20.png"),
	QUEUE_DOWN_20("classpath:icons/abstract/flatArrowDown_20.png"),
	EDIT_20("classpath:icons/pencil_20.png"),
	DELETE_20("classpath:icons/bin_20.png"),
	STOP_20("classpath:icons/stop_20.png"),
	
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
