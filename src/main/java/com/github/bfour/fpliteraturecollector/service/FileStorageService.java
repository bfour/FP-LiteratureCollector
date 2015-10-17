package com.github.bfour.fpliteraturecollector.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;

public class FileStorageService {

	private static FileStorageService instance;

	private static final File rootDirectory = new File("./files/");

	private FileStorageService() {
	}

	public static FileStorageService getInstance() {
		if (instance == null)
			instance = new FileStorageService();
		return instance;
	}

	public Link persist(URL webAddress, Literature lit) throws IOException {

		String fileName = getFileNameForLiterature(lit);
		fileName += ".pdf";
		// + FilenameUtils.getExtension(webAddress.getFile()).substring(0,
		// 3);
		File file = new File(rootDirectory.getAbsolutePath() + "/"
				+ lit.getID() + "/" + fileName);

		URLConnection conn = webAddress.openConnection();
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
		conn.connect();
		FileUtils.copyInputStreamToFile(conn.getInputStream(), file);

		return new Link(fileName, file.toURI(), webAddress.toString());

	}

	private String getFileNameForLiterature(Literature lit) {

		// take title, removing all special characters
		String name = Normalizer.normalize(lit.getTitle(), Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");

		name = name.replaceAll("[^A-z\\s]", "");

		// remove unnecessary words
		name = name.replaceAll("\\sa\\s", " ");
		name = name.replaceAll("\\sthe\\s", " ");
		name = name.replaceAll("\\sA\\s", " ");
		name = name.replaceAll("\\sThe\\s", " ");

		// trim
		if (name.length() > 68)
			name = name.substring(0, 68);

		// add kind-of GUID
		name += "_" + Long.toHexString(new Date().getTime());

		return name;

	}

}
