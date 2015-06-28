package com.github.bfour.fpliteraturecollector.service.converters;

import java.awt.Color;

import org.springframework.core.convert.converter.Converter;

public class ColorToStringConverter implements Converter<Color, String> {

	@Override
	public String convert(Color source) {
		return ColorSerializer.serialize(source);
	}

}
