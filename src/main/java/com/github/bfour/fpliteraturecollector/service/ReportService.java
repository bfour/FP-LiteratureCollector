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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFileChooser;

import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.jlib.commons.logic.AndExpression;
import com.github.bfour.jlib.commons.logic.AtomicExpression;
import com.github.bfour.jlib.commons.logic.BooleanExpression;
import com.github.bfour.jlib.commons.logic.ContainsExpression;
import com.github.bfour.jlib.commons.logic.EqualsExpression;
import com.github.bfour.jlib.commons.logic.LogicException;
import com.github.bfour.jlib.commons.logic.OrExpression;
import com.github.bfour.jlib.commons.logic.translation.MathTranslator;
import com.github.bfour.jlib.commons.logic.translation.TranslationException;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.jamesmurty.utils.XMLBuilder2;

public class ReportService {

	private static ReportService instance;
	private ServiceManager servMan;

	private ReportService(ServiceManager serviceManager) {
		this.servMan = serviceManager;
	}

	public static ReportService getInstance(ServiceManager serviceManager) {
		if (instance == null)
			instance = new ReportService(serviceManager);
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

	public void exportToHTMLFile(List<Literature> literature, File file)
			throws FileNotFoundException, ServiceException,
			TranslationException, LogicException {

		PrintWriter writer = new PrintWriter(new FileOutputStream(file));
		Properties outputProperties = new Properties();
		outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");

		getHTMLBuilder(literature).toWriter(writer, outputProperties);

	}

	public XMLBuilder2 getHTMLBuilder(List<Literature> literature)
			throws ServiceException, TranslationException, LogicException {

		XMLBuilder2 builder = XMLBuilder2.create("html");
		XMLBuilder2 body = builder.e("body");

		body.e("h1").text("Overview");
		XMLBuilder2 overviewTable = body.e("table");
		XMLBuilder2 headerRow = overviewTable.e("tr");
		headerRow.e("th").text("Reference");
		headerRow.e("th").text("Title");
		headerRow.e("th").text("Year");
		headerRow.e("th").text("Topics");
		headerRow.e("th").text("Architectures");
		headerRow.e("th").text("Communication");
		headerRow.e("th").text("Web-App");
		headerRow.e("th").text("OS");
		headerRow.e("th").text("Devices");
		headerRow.e("th").text("Smart Device Components");
		headerRow.e("th").text("Wearable Types");
		headerRow.e("th").text("Wearable Locations");
		headerRow.e("th").text("Target Groups");
		headerRow.e("th").text("Evaluation Methods");

		for (Literature lit : literature) {
			XMLBuilder2 row = overviewTable.e("tr");
			row.e("td").text(
					"ADDIN ZOTERO_ITEM CSL_CITATION {\"citationItems\":[{\"id\":0,\"uris\":[\""
							+ lit.getZoteroID() + "\"],\"uri\":[\""
							+ lit.getZoteroID() + "\"],\"itemData\":{}}]}");
			row.e("td").text(lit.getTitle());
			row.e("td").text(lit.getYear() + "");
			row.e("td").text(getTagsWithPrefixAsString(lit, "Topic: "));
			row.e("td").text(getTagsWithPrefixAsString(lit, "Architecture: "));
			row.e("td").text(getTagsWithPrefixAsString(lit, "Communication: "));
			boolean hasWebApp = false;
			for (Tag t : lit.getTags())
				if (t.getName().equals("App: web-based")) {
					hasWebApp = true;
					break;
				}
			row.e("td").text((hasWebApp ? "yes" : "no"));
			row.e("td").text(getTagsWithPrefixAsString(lit, "OS: "));
			row.e("td").text(getTagsWithPrefixAsString(lit, "Device: "));
			row.e("td").text(
					getTagsWithPrefixAsString(lit,
							"Used SmartDevice Component: "));
			row.e("td").text(getTagsWithPrefixAsString(lit, "Wearable Type: "));
			row.e("td").text(
					getTagsWithPrefixAsString(lit, "Wearable Location: "));
			row.e("td").text(getTagsWithPrefixAsString(lit, "Target Group: "));
			row.e("td").text(
					getTagsWithPrefixAsString(lit, "Evaluation Method: "));
		}

		// === prepare boolean expressions ===
		Set<Set<String>> groups = new HashSet<>();
		Set<String> group = new HashSet<>();

		// communication
		group.add("Bluetooth 4.0");
		group.add("Bluetooth");
		group.add("Bluetooth Low Energy");
		group.add("Bluetooth 2.0");
		groups.add(group);
		Set<BooleanExpression> communicationExpr = getExpressionsByTagPrefixMerging(
				"Communication: ", groups);

		// architecture
		groups = new HashSet<>();
		// with backend
		group = new HashSet<>();
		group.add("smart device <> backend");
		group.add("wearable <> backend");
		group.add("wearable <> backend <> smart device");
		group.add("wearable <> smart device <> backend");
		group.add("wearable <> smart device/backend <> backend");
		groups.add(group);
		// without backend
		group = new HashSet<>();
		group.add("wearable <> smart device");
		group.add("smart device");
		groups.add(group);
		// without wearable
		group = new HashSet<>();
		group.add("smart device <> backend");
		group.add("smart device");
		groups.add(group);
		Set<BooleanExpression> archExpr = getExpressionsByTagPrefixMerging(
				"Architecture: ", groups);

		// OS
		groups = new HashSet<>();
		group = new HashSet<>();
		group.add("Android 2.2");
		group.add("Android 4.*");
		group.add("Android");
		groups.add(group);
		Set<BooleanExpression> osExpr = getExpressionsByTagPrefixMerging(
				"OS: ", groups);

		// topic
		groups = new HashSet<>();
		group = new HashSet<>();
		group.add("augmented reality");
		group.add("user interaction methods");
		groups.add(group);
		group = new HashSet<>();
		group.add("memory impairment");
		group.add("mental deficiency");
		groups.add(group);
		group = new HashSet<>();
		group.add("posture");
		group.add("orthopaedic");
		groups.add(group);
		group = new HashSet<>();
		group.add("psychology");
		group.add("stress");
		groups.add(group);
		group = new HashSet<>();
		group.add("diet");
		group.add("weight management");
		groups.add(group);
		Set<BooleanExpression> topicExpr = getExpressionsByTagPrefixMerging(
				"Topic: ", groups);

		// year
		Set<BooleanExpression> yearExpr = new HashSet<>();
		yearExpr.add(new EqualsExpression("year", 2014));
		yearExpr.add(new EqualsExpression("year", 2015));
		yearExpr.add(new EqualsExpression("year", 2016));
		yearExpr.add(new EqualsExpression("year", 2017));

		// wearable location
		Set<BooleanExpression> wearableLocationExpr = getExpressionsByTagPrefixMerging(
				"Wearable Location: ", new HashSet<>());

		// truth
		Set<BooleanExpression> truthExpr = new HashSet<>();
		truthExpr.add(new AtomicExpression(true));

		// evaluation
		body.e("h1").text("Evaluation method");
		body.importXMLBuilder(getCrossTableByFormula(
				literature,
				getExpressionsByTagPrefixMerging("Evaluation Method: ",
						new HashSet<>()), getExpressionsByTagPrefixMerging("Empirically Validated: ",
								new HashSet<>())));

		// used components total
		body.e("h1").text("Used components");
		body.importXMLBuilder(getCrossTableByFormula(
				literature,
				getExpressionsByTagPrefixMerging(
						"Used SmartDevice Component: ", new HashSet<>()),
				truthExpr));

		// devices
		body.e("h1").text("Devices");
		body.importXMLBuilder(getCrossTableByFormula(literature,
				getExpressionsByTagPrefixMerging("Device: ", new HashSet<>()),
				truthExpr));

		// arch vs comm crosstable
		body.e("h1").text("Architecture vs. Communication");
		body.importXMLBuilder(getCrossTableByFormula(literature, archExpr,
				communicationExpr));

		// wearable location vs communication#
		body.e("h1").text("Wearable Location vs. Communication");
		body.importXMLBuilder(getCrossTableByFormula(literature,
				wearableLocationExpr, communicationExpr));

		// topic vs architecture
		body.e("h1").text("Topic vs. Architecture");
		body.importXMLBuilder(getCrossTableByFormula(literature, topicExpr,
				archExpr));

		// topic vs communication
		body.e("h1").text("Topic vs. Communication");
		body.importXMLBuilder(getCrossTableByFormula(literature, topicExpr,
				communicationExpr));

		// OS vs communication
		body.e("h1").text("OS vs. Communication");
		body.importXMLBuilder(getCrossTableByFormula(literature, osExpr,
				communicationExpr));

		// year vs OS
		body.e("h1").text("Year vs. OS");
		body.importXMLBuilder(getCrossTableByFormula(literature, osExpr,
				yearExpr));

		// year vs architecture
		body.e("h1").text("Year vs. Architecture");
		body.importXMLBuilder(getCrossTableByFormula(literature, archExpr,
				yearExpr));

		// year vs communication
		body.e("h1").text("Year vs. Communication");
		body.importXMLBuilder(getCrossTableByFormula(literature,
				communicationExpr, yearExpr));

		return builder;

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

	private XMLBuilder2 getCrossTableByFormula(List<Literature> literature,
			Set<BooleanExpression> vertical, Set<BooleanExpression> horizontal)
			throws TranslationException, LogicException {

		XMLBuilder2 table = XMLBuilder2.create("table");
		MathTranslator trans = new MathTranslator(false, false);

		// header
		XMLBuilder2 headerRow = table.e("tr");
		headerRow.e("th").text("");
		headerRow.e("th").text("n");
		for (BooleanExpression e : horizontal)
			headerRow.e("th").text(e.translate(trans));

		// rows
		for (BooleanExpression vExpr : vertical) {
			XMLBuilder2 row = table.e("tr");
			row.e("td").text(vExpr.translate(trans));
			// total occurence count of this vertical expr
			row.e("td").text(countConjunctiveTruths(literature, vExpr) + "");
			// go through all horizontal expressions and count combination
			for (BooleanExpression hExpr : horizontal) {
				row.e("td").text(
						countConjunctiveTruths(literature, vExpr, hExpr) + "");
			}
		}

		// totals row
		XMLBuilder2 row = table.e("tr");
		row.e("td").text("Total");
		row.e("td").text("");
		for (BooleanExpression hExpr : horizontal) {
			row.e("td").text(countConjunctiveTruths(literature, hExpr) + "");
		}

		return table;

	}

	private XMLBuilder2 getCrossTable(List<Literature> literature,
			Set<Tag> vertical, Set<Tag> horizontal) {

		XMLBuilder2 table = XMLBuilder2.create("table");

		// header
		XMLBuilder2 headerRow = table.e("tr");
		headerRow.e("th").text("");
		headerRow.e("th").text("Total");
		for (Tag t : horizontal)
			headerRow.e("th").text(t.getName());

		// rows
		for (Tag vTag : vertical) {
			XMLBuilder2 row = table.e("tr");
			row.e("td").text(vTag.getName());
			// total occurence count of this vertical tag
			row.e("td").text(countHasAllTags(literature, vTag) + "");
			// go through all horizontal tags and count combination
			for (Tag hTag : horizontal) {
				row.e("td").text(countHasAllTags(literature, vTag, hTag) + "");
			}
		}

		// totals row
		XMLBuilder2 row = table.e("tr");
		row.e("td").text("Total");
		row.e("td").text("");
		for (Tag hTag : horizontal) {
			row.e("td").text(countHasAllTags(literature, hTag) + "");
		}

		return table;

	}

	private int countConjunctiveTruths(List<Literature> literature,
			BooleanExpression... exprAr) throws LogicException {
		int i = 0;
		List<BooleanExpression> exprs = Arrays.asList(exprAr);
		BooleanExpression conjunct = new AndExpression(exprs);
		for (Literature lit : literature) {
			conjunct.setVariables(lit.getSearchData());
			if (conjunct.evaluate())
				i++;
		}
		return i;
	}

	/**
	 * Counts the number of the given literature entries that have all of the
	 * given tags.
	 * 
	 * @param literature
	 * @param tag
	 * @return
	 */
	private int countHasAllTags(List<Literature> literature, Tag... tagAr) {
		int i = 0;
		List<Tag> tags = Arrays.asList(tagAr);
		for (Literature lit : literature)
			if (lit.getTags().containsAll(tags))
				i++;
		return i;
	}

	/**
	 * Get all tags from the given collection of Literature that start with the
	 * given prefix.
	 * 
	 * @param lits
	 * @param prefix
	 * @return
	 */
	private Set<Tag> getTagsWithPrefix(List<Literature> lits, String prefix) {
		Set<Tag> tags = new HashSet<Tag>();
		for (Literature lit : lits)
			for (Tag t : lit.getTags())
				if (t.getName().startsWith(prefix))
					tags.add(t);
		return tags;
	}

	/**
	 * Get tags in given literature that start with the given prefix as a
	 * formatted String.
	 * 
	 * @param lit
	 * @param prefix
	 * @return
	 */
	private String getTagsWithPrefixAsString(Literature lit, String prefix) {
		List<Literature> lits = new ArrayList<>(1);
		lits.add(lit);
		return tagListToStringRemovingPrefix(getTagsWithPrefix(lits, prefix),
				prefix);
	}

	/**
	 * Convert the given set of tags to a formatted string and remove the given
	 * prefix from each tag if applicable.
	 * 
	 * @param tags
	 * @param prefix
	 * @return
	 */
	private String tagListToStringRemovingPrefix(Set<Tag> tags, String prefix) {
		StringBuilder builder = new StringBuilder();
		for (Tag t : tags) {
			builder.append(t.getName().replace(prefix, ""));
			builder.append("; ");
		}
		if (!(builder.length() > 0))
			builder.append("-");
		else
			builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

	/**
	 * Gets corresponding BooleanExpressions to each of the tags that result
	 * from the given prefix. Additionally returns the BooleanExpressions merged
	 * to disjunctive formulas/expressions that correspond to the given
	 * specification of groups.
	 * 
	 * Example: the universe of tags is {aa, ab, ac, ad, ae, ba, bx}; given
	 * prefix "a" and {{"a","b"},{"c","b"}} as the groups-variable the result
	 * will be the expressions {(aa ∈ tags), (ab ∈ tags), (ac ∈ tags), (ad ∈
	 * tags), (ae ∈ tags), (aa ∈ tags ∨ ab ∈ tags), (ac ∈ tags ∨ ab ∈ tags)}
	 * 
	 * @param prefix
	 * @param groups
	 *            A set of sets of strings: Each set of strings contains the
	 *            suffixes of the names of the tags that shall be merged
	 *            together to a disjunctive expression.
	 * @return
	 * @throws ServiceException
	 */
	private Set<BooleanExpression> getExpressionsByTagPrefixMerging(
			String prefix, Set<Set<String>> groups) throws ServiceException {
		Set<BooleanExpression> exprs = new HashSet<BooleanExpression>();
		Set<Tag> prefixedTags = servMan.getTagService().getByPrefix(prefix);
		for (Tag t : prefixedTags)
			exprs.add(new ContainsExpression("tags", t));
		for (Set<String> group : groups) {
			OrExpression disjunctExpr = new OrExpression();
			for (String suffix : group) {
				for (Tag t : prefixedTags)
					if (t.getName().equals(prefix + suffix))
						disjunctExpr.addExpression(new ContainsExpression(
								"tags", t));
			}
			exprs.add(disjunctExpr);
		}
		return exprs;
	}
}
