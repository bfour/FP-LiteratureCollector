package com.github.bfour.fpliteraturecollector.service.converters;

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


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

class ColorSerializer {

	private ColorSerializer() {
	}

	public static String serialize(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int alpha = color.getAlpha();
		return red + ";" + green + ";" + blue + ";" + alpha;
	}
	
	public static Color deSerialize(String string) {
		String[] split = string.split(";");
		List<Integer> ints = new ArrayList<>(split.length);
		for (String token : split)
			ints.add(Integer.parseInt(token));
		return new Color(ints.get(0), ints.get(1), ints.get(2), ints.get(3));
	}
	
//	public static byte[] serialize(Color color) {
//		int red = color.getRed();
//		int green = color.getGreen();
//		int blue = color.getBlue();
//		int alpha = color.getAlpha();
//		ByteBuffer buffer = ByteBuffer.allocate(4 * 4);
//		buffer.putInt(red);
//		buffer.putInt(green);
//		buffer.putInt(blue);
//		buffer.putInt(alpha);
//		return buffer.array();
//	}
//
//	public static Color deSerialize(byte[] bytes) {
//		ByteBuffer buffer = ByteBuffer.wrap(bytes);
//		int red = buffer.getInt();
//		int green = buffer.getInt();
//		int blue = buffer.getInt();
//		int alpha = buffer.getInt();
//		return new Color(red, green, blue, alpha);
//	}
	
}
