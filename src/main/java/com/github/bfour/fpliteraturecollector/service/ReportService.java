/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;

import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.jamesmurty.utils.XMLBuilder2;

public class ReportService {

	private static ReportService instance;

	private ReportService() {
		// TODO Auto-generated constructor stub
	}

	public static ReportService getInstance() {
		if (instance == null)
			instance = new ReportService();
		return instance;
	}

	public void exportToMODSFile(List<Literature> literature, File file)
			throws FileNotFoundException {

		PrintWriter writer = new PrintWriter(new FileOutputStream(file));
		Properties outputProperties = new Properties();
		outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");

		getXMLBuilder(literature).toWriter(writer, outputProperties);

	}

	private XMLBuilder2 getXMLBuilder(List<Literature> literature) {
		XMLBuilder2 builder = XMLBuilder2.create("modsCollection");
		builder.a("xmlns:xlink", "http://www.w3.org/1999/xlink");
		builder.a("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		builder.a("xmlns", "http://www.loc.gov/mods/v3");
		builder.a("xsi:schemaLocation",
				"http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-6.xsd");
		for (Literature lit : literature)
			getXMLBuilder(lit, builder);
		return builder;
	}

	private XMLBuilder2 getXMLBuilder(Literature literature,
			XMLBuilder2 existingBuilder) {

		XMLBuilder2 builder = existingBuilder;
		if (builder == null)
			builder = XMLBuilder2.create("mods");
		else
			builder = builder.e("mods");

		builder.a("version", "3.6");

		builder.e("titleInfo").e("title").text(literature.getTitle());

		if (literature.getAbstractText() != null)
			builder.e("abstract").text(literature.getAbstractText());

		builder.e("typeOfResource").text("text");
		if (literature.getType() != null)
			builder.e("genre").text(literature.getType().getTellingName());

		for (Author auth : literature.getAuthors()) {
			XMLBuilder2 authorBuilder = builder.e("name").a("type", "personal");
			if (auth.getFirstName() != null)
				authorBuilder.e("namePart").a("type", "given")
						.text(auth.getFirstName());
			if (auth.getMiddleName() != null)
				authorBuilder.e("namePart").a("type", "given")
						.text(auth.getMiddleName());
			if (auth.getLastName() != null)
				authorBuilder.e("namePart").a("type", "family")
						.text(auth.getLastName());
		}

		if (literature.getDOI() != null)
			builder.e("identifier").a("type", "doi").a("displayLabel", "DOI")
					.text(literature.getDOI());

		if (literature.getISBN() != null)
			builder.e("identifier").a("type", "isbn").a("displayLabel", "ISBN")
					.text(literature.getISBN().getV13String());

		if (literature.getYear() != null
				|| literature.getPublicationContext() != null
				|| literature.getPublisher() != null) {
			XMLBuilder2 hostBuilder = builder.e("relatedItem")
					.a("type", "host");
			XMLBuilder2 originBuilder = hostBuilder.e("originInfo");
			if (literature.getYear() != null)
				originBuilder.e("dateIssued").text(literature.getYear() + "");
			if (literature.getPublisher() != null)
				originBuilder.e("publisher").text(literature.getPublisher());
			if (literature.getPublicationContext() != null)
				originBuilder.e("titleInfo").e("title")
						.text(literature.getPublicationContext());
		}

		if (literature.getWebsiteURLs() != null
				&& !literature.getWebsiteURLs().isEmpty()) {
			XMLBuilder2 locationBuilder = builder.e("location");
			for (Link website : literature.getWebsiteURLs()) {
				locationBuilder.e("url").a("displayLabel", website.getName())
						.text(website.getUri().toString());
			}
		}

		if (literature.getNotes() != null)
			builder.e("note").text(literature.getNotes());

		return builder;

	}

	public void exportToXML(Query query) throws FileNotFoundException {

		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			return;
		File file = fileChooser.getSelectedFile();

		PrintWriter writer = new PrintWriter(new FileOutputStream(file));
		Properties outputProperties = new Properties();
		outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");

		getXMLBuilder(query).toWriter(writer, outputProperties);

	}

	private XMLBuilder2 getXMLBuilder(Query query) {

		XMLBuilder2 builder = XMLBuilder2.create("query");
		builder.a("ID", query.getID() + "");
		builder.a("creationTime", query.getCreationTime() + "");
		builder.a("lastChangeTime", query.getLastChangeTime() + "");
		builder.a("name", query.getName());
		builder.a("queuePosition", query.getQueuePosition() + "");
		builder.a("status", query.getStatus().getTellingName());

		XMLBuilder2 atomReqsBuilder = builder.e("atomicRequests");
		for (AtomicRequest atomReq : query.getAtomicRequests()) {
			XMLBuilder2 atomReqBuilder = atomReqsBuilder.e("atomicRequest");
			atomReqBuilder.a("crawler", atomReq.getCrawler().toString());
			atomReqBuilder.a("searchString", atomReq.getSearchString());
			atomReqBuilder.a("processed", atomReq.isProcessed() + "");
			if (atomReq.getProcessingError() != null)
				atomReqBuilder.a("processingError",
						atomReq.getProcessingError());
			if (atomReq.getResults() != null)
				for (Literature result : atomReq.getResults())
					atomReqBuilder.e("result").a("ID", result.getID() + "");
		}

		return builder;

	}

}
