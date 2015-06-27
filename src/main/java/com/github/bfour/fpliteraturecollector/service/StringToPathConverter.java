package com.github.bfour.fpliteraturecollector.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.convert.converter.Converter;

public class StringToPathConverter implements Converter<String, Path> {

	@Override
	public Path convert(String source) {
		return Paths.get(source);
	}

}
