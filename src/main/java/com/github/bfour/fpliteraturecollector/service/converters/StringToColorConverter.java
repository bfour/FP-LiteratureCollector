package com.github.bfour.fpliteraturecollector.service.converters;

import java.awt.Color;

import org.springframework.core.convert.converter.Converter;

public class StringToColorConverter implements Converter<String, Color> {

	@Override
	public Color convert(String source) {
		return ColorSerializer.deSerialize(source);
	}

}
