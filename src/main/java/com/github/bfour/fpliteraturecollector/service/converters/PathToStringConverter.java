package com.github.bfour.fpliteraturecollector.service.converters;

import java.nio.file.Path;

import org.springframework.core.convert.converter.Converter;

public class PathToStringConverter implements Converter<Path, String> {

	@Override
	public String convert(Path source) {
		return source.toAbsolutePath().toString();
	}

}
