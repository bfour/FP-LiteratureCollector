package com.github.bfour.fpliteraturecollector.service.database.DAO;

import java.awt.Color;
import java.nio.ByteBuffer;

class ColorSerializer {

	private ColorSerializer() {
	}

	public static byte[] serialize(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int alpha = color.getAlpha();
		ByteBuffer buffer = ByteBuffer.allocate(4 * 4);
		buffer.putInt(red);
		buffer.putInt(green);
		buffer.putInt(blue);
		buffer.putInt(alpha);
		return buffer.array();
	}

	public static Color deSerialize(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int red = buffer.getInt();
		int green = buffer.getInt();
		int blue = buffer.getInt();
		int alpha = buffer.getInt();
		return new Color(red, green, blue, alpha);
	}
	
}
