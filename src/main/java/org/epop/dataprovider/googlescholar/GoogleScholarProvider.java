/*******************************************************************************
 * Copyright (c) 2012 fm&selab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     fm&selab - initial API and implementation
 ******************************************************************************/
package org.epop.dataprovider.googlescholar;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.params.ClientPNames;
//import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.epop.dataprovider.DataProvider;
import org.epop.dataprovider.PatternMismatchException;
import org.epop.dataprovider.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.github.bfour.fpjcommons.lang.Tuple;
import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;

public class GoogleScholarProvider extends DataProvider {

	private static final Pattern citespattern = Pattern.compile("(\\d+)");

	private static final List<String> malformed = new LinkedList<String>();
	private static final long DELAY = 18611;
	private static final String SCHOLAR_GOOGLE_COM = "scholar.google.com";
	//

	Logger logger = Logger.getLogger(GoogleScholarProvider.class
			.getCanonicalName());
	private DefaultHttpClient httpclient;

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

			uri = URIUtils.createURI("http", SCHOLAR_GOOGLE_COM, -1,
					"/scholar", htmlParams, null);
			HttpGet httpget = new HttpGet(uri);
			httpget.addHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322)");
			// httpget.getParams().setParameter(ClientPNames.COOKIE_POLICY,
			// CookiePolicy.IGNORE_COOKIES);

			System.out.println(httpget.getURI());
			httpclient = new DefaultHttpClient();

			responseBody = Jsoup.connect(uri.toURL().toString())
					.userAgent("Mozilla").get().html();

			int counter = 0;
			String newResponseBody = responseBody;
			try {
				while (counter < pageTurnLimit
						&& newResponseBody
								.contains("<b style=\"display:block;margin-left:50px\">Next</b>")) {
					counter++;
					Thread.sleep(DELAY);
					URI newUri = URIUtils.createURI("http", SCHOLAR_GOOGLE_COM,
							-1, "/scholar", htmlParams + "&start="
									+ (counter + 1) * 10, null);
					System.out.println(newUri);
					Document e = Jsoup.connect(newUri.toURL().toString())
							.userAgent("Mozilla").get();
					if (e != null) {
						// docs.add(e);
						newResponseBody = e.html();
						// System.out.println(newResponseBody);
						responseBody = responseBody + newResponseBody;
					} else {
						break;
					}
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
			}
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
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

				String typeString = article.select(".gs_ct2").text();
				if (typeString == null)
					typeString = "";
				if (typeString.equals("[C]"))
					continue; // skip citations
				litBuilder.setType(getLiteratureType(typeString));

				String title = article.select(".gs_rt a").text();
				title = title.replaceAll("\u0097", "-");
				title = title.replaceAll("…", "...");
				if (title.isEmpty())
					throw new DatalayerException(
							"title retrieved by parsing is empty");
				litBuilder.setTitle(title);

				String rawHTML = article.select(".gs_a").html();
				if (rawHTML.isEmpty())
					continue;

				// split by " - " (authors - publication, year - publisher)
				String[] splits = rawHTML.split(" - ");
				if (splits.length != 3)
					throw new DatalayerException(
							"dashTokenizer should have three sections (authors - publication, year - publisher), found "
									+ splits.length
									+ "; maybe Google Scholar layout has changed");
				String namesHTML = splits[0];
				String publicationHTML = splits[1];
				String publisherHTML = splits[2];

				// authors
				List<Author> authors = getAuthorsFromHTMLSection(namesHTML);
				litBuilder.setAuthors(new HashSet<>(authors));

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
				Matcher cm = citespattern.matcher(citedby);
				try {
					int cites = cm.find() ? Integer.parseInt(cm.group(1)) : 0;
					litBuilder.setgScholarNumCitations(cites);
				} catch (NumberFormatException e) {
					// TODO
				}

				// website URL
				String websiteURL = article.select(".gs_rt a").attr("href");
				litBuilder.setWebsiteURL(websiteURL);

				// fulltext
				String fulltextURL = article.select("div.gs_md_wp.gs_ttss a")
						.attr("href");
				litBuilder.setFulltextURL(fulltextURL);

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
