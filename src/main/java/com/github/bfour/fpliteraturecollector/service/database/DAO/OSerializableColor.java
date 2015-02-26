package com.github.bfour.fpliteraturecollector.service.database.DAO;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.nio.ByteBuffer;

import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.serialization.OSerializableStream;

public class OSerializableColor extends Color implements OSerializableStream {

	private static final long serialVersionUID = -4948645118483624549L;

	public OSerializableColor(ColorSpace instance, float[] components,
			int alpha) {
		super(instance, components, alpha/255);
	}

	public OSerializableColor(Color color) {
		this(color.getColorSpace(), color.getComponents(null),
				color.getAlpha());
	}

	@Override
	public byte[] toStream() throws OSerializationException {
		int colorSpace = getColorSpace().getType();
		float[] components = getComponents(null);
		int alpha = getAlpha();
		ByteBuffer buffer = ByteBuffer.allocate(4 + components.length * 4 + 4);
		buffer.putInt(colorSpace);
		buffer.putInt(alpha);
		for (float component : components) {
			buffer.putFloat(component);
		}
		return buffer.array();
	}

	@Override
	public OSerializableStream fromStream(byte[] bytes)
			throws OSerializationException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int colorSpace = buffer.getInt();
		int alpha = buffer.getInt();
		ByteBuffer componentsBuffer = buffer.slice();
		float[] components = new float[componentsBuffer.remaining()];
		int counter = 0;
		while (componentsBuffer.hasRemaining()) {
			components[counter] = componentsBuffer.getFloat();
			counter++;
		}
		return new OSerializableColor(ColorSpace.getInstance(colorSpace),
				components, alpha);
	}

}
