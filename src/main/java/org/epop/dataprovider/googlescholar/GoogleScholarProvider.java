/*
 * Copyright 2016 Florian Pollak
 *
 * Copyright (c) 2012 fm&selab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     fm&selab - initial API and implementation
 *     Florian Pollak - several modifications
 */
package org.epop.dataprovider.googlescholar;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.epop.dataprovider.DataProvider;
import org.epop.dataprovider.HTMLPage;
import org.epop.dataprovider.PatternMismatchException;
import org.epop.dataprovider.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
//import org.apache.http.client.params.ClientPNames;
//import org.apache.http.client.params.CookiePolicy;
import com.github.bfour.jlib.commons.services.DatalayerException;

public class GoogleScholarProvider extends DataProvider {

	private static final Pattern CITES_PATTERN = Pattern.compile("(\\d+)");
	private static final Pattern ID_PATTERN = Pattern
			.compile(".*cluster=(\\d+).*");

	private static final List<String> malformed = new LinkedList<String>();
	private static final long DELAY = 28611;
	private static final String SCHOLAR_GOOGLE_COM = "http://scholar.google.com";

	Logger logger = Logger.getLogger(GoogleScholarProvider.class
			.getCanonicalName());

	@Override
	public String getDescription() {
		return "Google Scholar";
	}

	@Override
	protected Reader getHTMLDoc(String htmlParams, int pageTurnLimit,
			boolean initialWait) throws URISyntaxException, IOException {

		URI uri;
		String responseBody = null;

		try {

			if (initialWait)
				Thread.sleep(DELAY);

			uri = new URI(SCHOLAR_GOOGLE_COM + "/scholar?" + htmlParams);
			HTMLPage page = new HTMLPage(uri);
			responseBody = page.getRawCode();

			int counter = 0;
			String newResponseBody = responseBody;
			try {
				while (counter < pageTurnLimit
						&& newResponseBody.contains("\">Next</b>")) {
					counter++;
					Thread.sleep(DELAY);
					URI newUri = new URI(SCHOLAR_GOOGLE_COM + "/scholar?"
							+ htmlParams + "&start=" + (counter * 10)
							+ htmlParams);
					page = new HTMLPage(newUri);
					responseBody = responseBody + page.getRawCode();
					;
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
			}
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ParserConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// return the result as string
		// System.out.println("responseBody =\n" + responseBody);
		return new StringReader(responseBody);
	}

	// private List<NameValuePair> getParameters(Query q, int counter) {
	// List<NameValuePair> qparams;
	// qparams = new ArrayList<NameValuePair>();
	// qparams.add(new BasicNameValuePair("start", String.valueOf(counter
	// * MAX_NUM_PAPERS)));
	// qparams.add(new BasicNameValuePair("as_sauthors", "\""
	// + q.getCompleteAuthorName('+') + "\""));
	// qparams.add(new BasicNameValuePair("as_q", ""));
	// // qparams.add(new BasicNameValuePair("as_sauthors",
	// // q.getCompleteAuthorName()));
	// // qparams.add(new BasicNameValuePair("btnG", "Search"));
	// qparams.add(new BasicNameValuePair("num", MAX_NUM_PAPERS + ""));
	// // qparams.add(new BasicNameValuePair("as_vis", "1"));
	// // qparams.add(new BasicNameValuePair("as_sdt", "1,5"));
	// return qparams;
	// }

	@Override
	protected List<Literature> parsePage(Reader page) throws DatalayerException {
		List<Literature> papers = new ArrayList<Literature>();
		Document doc = null;
		try {
			StringBuilder builder = new StringBuilder();
			int charsRead = -1;
			char[] chars = new char[100];
			do {
				charsRead = page.read(chars, 0, chars.length);
				// if we have valid chars, append them to end of string.
				if (charsRead > 0)
					builder.append(chars, 0, charsRead);
			} while (charsRead > 0);
			doc = Jsoup.parse(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} // for (Document doc : docs) {

		for (Element article : doc.select(".gs_r")) {
			try {

				LiteratureBuilder litBuilder = new LiteratureBuilder();

				// type
				String typeString = article.select(".gs_ct2").text();
				if (typeString == null)
					typeString = "";
				if (typeString.equals("[C]"))
					continue; // skip citations
				litBuilder.setType(getLiteratureType(typeString));

				// title
				String title = article.select(".gs_rt a").text();
				title = title.replaceAll("\u0097", "-");
				title = title.replaceAll("…", "...");
				if (title.isEmpty())
					throw new DatalayerException(
							"title retrieved by parsing is empty");
				litBuilder.setTitle(title);

				// website URL
				if (litBuilder.getWebsiteURLs() == null)
					litBuilder.setWebsiteURLs(new HashSet<Link>());
				try {
					String linkURL = article.select(".gs_rt a").attr("href");
					litBuilder.getWebsiteURLs().add(new Link(linkURL));
				} catch (URISyntaxException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					// cluster link
					String googleLinkURL = "http://scholar.google.com"
							+ article.select(".gs_fl .gs_nph").attr("href");
					litBuilder.getWebsiteURLs().add(
							new Link("Google Scholar", googleLinkURL));
					// scholar ID
					Matcher idMatcher = ID_PATTERN.matcher(googleLinkURL);
					if (idMatcher.find())
						litBuilder.setgScholarID(idMatcher.group(1));
					// else
					// TODO error handling
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				String abstractText = article.select(".gs_rs").text();
				litBuilder.setAbstractText(abstractText);

				String rawHTML = article.select(".gs_a").html();
				if (rawHTML.isEmpty()) // no authors
					continue;

				// split by " - " (authors - publication, year - publisher)
				String[] splits = rawHTML.split(" - ");
//				if (splits.length != 3)
//					throw new DatalayerException(
//							"dashTokenizer should have three sections (authors - publication, year - publisher), found "
//									+ splits.length
//									+ "; maybe Google Scholar layout has changed");
				String namesHTML = "", publicationHTML = "", publisherHTML = "";
				if (splits.length > 0) {
					namesHTML = splits[0];
					namesHTML = namesHTML.replace("…, ", "");
				}
				if (splits.length == 2) {
					publisherHTML = splits[1];
				}
				if (splits.length > 3) {
					publicationHTML = splits[1];
					publisherHTML = splits[2];
				}

				// authors
				try {
					List<Author> authors = getAuthorsFromHTMLSection(namesHTML);
					litBuilder.setAuthors(new HashSet<>(authors));
				} catch (PatternMismatchException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// publication
				String[] commaSplit = publicationHTML.split(", ");
				if (commaSplit.length == 2) {
					String publication = commaSplit[0];
					publication = publication.replaceAll("\u0097", "-");
					publication = publication.replaceAll("…", "...");
					litBuilder.setPublicationContext(publication);
					try {
						Integer year = Integer.parseInt(commaSplit[1]);
						litBuilder.setYear(year);
					} catch (NumberFormatException e) {
						// throw new ServiceException(
						// "publicationHTML subsection has invalid format: failed to parse publication year");
						// TODO (low) logging

					}
				} else {
					// TODO logging/notify user
				}

				// publisher
				litBuilder.setPublisher(publisherHTML);

				// citations
				String citedby = article.select(".gs_fl a[href*=cites]").text();
				Matcher cm = CITES_PATTERN.matcher(citedby);
				try {
					int cites = cm.find() ? Integer.parseInt(cm.group(1)) : 0;
					litBuilder.setgScholarNumCitations(cites);
				} catch (NumberFormatException e) {
					// TODO
				}

				// fulltext
				String fulltextURL = article.select("div.gs_md_wp.gs_ttss a")
						.attr("href");
				Set<Link> fullLinks = new HashSet<>();
				try {
					fullLinks.add(new Link(fulltextURL));
					litBuilder.setFulltextURLs(fullLinks);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				papers.add(litBuilder.getObject());

			} catch (Exception e) {
				malformed.add(e.getMessage());
			}
		}
		// }

		// if (headerContent.startsWith("User profiles")) {
		// // only the first part
		// Element hrefPart = seg.getAllElements().get(0);
		// String link = hrefPart.getAttributeValue("href");
		// assert link.startsWith("/citations");
		// String[] data = link.split("[?|&|=]");
		// System.out.println("id found for user " + data[2]);
		// return GoogleScholarGetterFromId.getFromId(data[2]);
		//
		// docs.clear();
		System.err.println(malformed + " " + malformed.size());

		return papers;
	}

	private LiteratureType getLiteratureType(String gScholarString) {
		switch (gScholarString) {
		case "[PDF]":
			break;
		case "[B]":
			return LiteratureType.BOOK;
		case "[HTML]":
			break;
		case "":
			break;
		}
		return LiteratureType.UNKNOWN;
	}

	private List<Author> getAuthorsFromHTMLSection(String htmlSection)
			throws PatternMismatchException {

		htmlSection = htmlSection.replace("…", "");

		List<Author> authors = new ArrayList<>();

		String[] commaSplit = htmlSection.split(", ");

		// only one author
		if (commaSplit.length == 0) {
			Author auth = getAuthorFromHTMLSubSection(htmlSection);
			if (auth != null)
				authors.add(auth);
			return authors;
		}

		// more than one author
		for (String part : commaSplit) {
			Author auth = getAuthorFromHTMLSubSection(part);
			if (auth != null)
				authors.add(auth);
		}
		return authors;

	}

	private Author getAuthorFromHTMLSubSection(String subsection)
			throws PatternMismatchException {

		Pattern authorWithIDPattern = Pattern
				.compile("citations\\?user=(.*?)&.*?>(.*?)<");

		AuthorBuilder builder = new AuthorBuilder();
		Matcher matcher = authorWithIDPattern.matcher(subsection);
		if (matcher.find()) {
			String gScholarID = matcher.group(1);
			builder.setgScholarID(gScholarID);
			String name = matcher.group(2);
			Utils.setFirstMiddleLastNameFromNameString(builder, name);
			return builder.getObject();
		} else {
			// no ID for this author
			Utils.setFirstMiddleLastNameFromNameString(builder, subsection);
			return builder.getObject();
		}

	}

}
