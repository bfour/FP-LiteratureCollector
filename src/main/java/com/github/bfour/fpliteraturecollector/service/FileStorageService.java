package com.github.bfour.fpliteraturecollector.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.github.bfour.fpliteraturecollector.domain.Link;

public class FileStorageService {

	private static final File rootDirectory = new File(".\\files\\");
	
	/*
	 * Idea: literature -> has a list of full text links
	 * -> literature services says persist
	 * -> goes through list
	 * -> skip those already persisted
	 * 
	 * ?? what to choose as file name?
	 * ?? directory structure?
	 * -> literature ID as folder name
	 */
	
	public Link persist(Literature lit, URL webAddress) throws IOException {
		String fileName = webAddress.getFile();
		ReadableByteChannel rbc = Channels.newChannel(webAddress.openStream());
		FileOutputStream fos = new FileOutputStream("information.html");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

}
