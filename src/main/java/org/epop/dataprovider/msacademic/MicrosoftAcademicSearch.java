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
package org.epop.dataprovider.msacademic;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.epop.dataprovider.DataProvider;
import org.epop.dataprovider.PatternMismatchException;
import org.epop.dataprovider.Utils;
import org.epop.utils.StringUtils;

import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;

public class MicrosoftAcademicSearch extends DataProvider {

	private static final String MS_ACADEMIC_SEARCH = "academic.research.microsoft.com/Search";
	Logger logger = Logger.getLogger(MicrosoftAcademicSearch.class
			.getCanonicalName());
	private static final long DELAY = 18611;
	private static final int searchStep = 10;

	private static final Pattern AUTHOR_ID_PATTERN = Pattern
			.compile(".*academic\\.research\\.microsoft\\.com/Author/(\\d+)/.*");

	@Override
	public String getDescription() {
		return "Microsoft Academic";
	}

	@Override
	protected Reader getHTMLDoc(String htmlParams, int pageTurnLimit,
			boolean initialWait) {

		// http://academic.research.microsoft.com/Search/?query=author:(%22Angelo%20gargantini%22)&start=1&end=10000
		// List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		// qparams.add(new BasicNameValuePair("query","author:(\""+
		// q.getCompleteAuthorName()+"\")"));//with quotation marks
		// qparams.add(new BasicNameValuePair("query", "author:("
		// + q.getCompleteAuthorName() + ")"));// without quotation marks
		// qparams.add(new BasicNameValuePair("start", "1"));
		// qparams.add(new BasicNameValuePair("end",
		// String.valueOf(searchStep)));

		URI uri;
		String responseBody = "";

		try {

			if (initialWait)
				Thread.sleep(DELAY);

			uri = URIUtils.createURI("http", MS_ACADEMIC_SEARCH, -1, "",
					htmlParams, null);
			HttpGet httpget = new HttpGet(uri);
			System.out.println(httpget.getURI());
			HttpClient httpclient = new DefaultHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);

			if (pageTurnLimit == 0)
				return new StringReader(responseBody);

			int counter = 1;
			String newResponseBody = responseBody;
			while (newResponseBody
					.contains("<a id=\"ctl00_MainContent_PaperList_Next\" title=\"Go to Next Page\" class=\"nextprev\"")) {

				Thread.sleep(DELAY);

				URI newUri = URIUtils.createURI(
						"http",
						MS_ACADEMIC_SEARCH,
						-1,
						"",
						htmlParams + "&start="
								+ String.valueOf((counter * searchStep) + 1)
								+ "&end="
								+ String.valueOf((counter + 1) * searchStep),
						null);

				httpget = new HttpGet(newUri);
				System.out.println(httpget.getURI());
				httpclient = new DefaultHttpClient();
				newResponseBody = httpclient.execute(httpget, responseHandler);
				// System.out.println(newResponseBody);
				responseBody = responseBody + newResponseBody;

				if (pageTurnLimit == counter)
					return new StringReader(responseBody);

				counter++;

			}

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return the result as string
		return new StringReader(responseBody);
	}

	@Override
	protected List<Literature> parsePage(Reader page) {
		List<Literature> result = new ArrayList<Literature>();
		try {
			Source source = new Source(page);
			Iterator<Element> elementList = source.getAllElements(
					HTMLElementName.LI).iterator();
			while (elementList.hasNext()) {
				Element element = elementList.next();
				// System.out.println(element.toString());
				StartTag startTag = element.getStartTag();
				Attribute classAttr = startTag.getAttributes().get("class");
				if (classAttr != null
						&& classAttr.getValue().equals("paper-item")) {
					// System.out.println(element.toString());
					Literature extractedPaper = extractPaper(element);
					// System.out.println(extractedPaper);
					if (extractedPaper != null)
						result.add(extractedPaper);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private Literature extractPaper(Element element) {

		LiteratureBuilder litBuilder = new LiteratureBuilder();

		for (Element s : element.getAllElements(HTMLElementName.DIV)) {
			Attribute classAttr = s.getStartTag().getAttributes().get("class");
			if (classAttr != null) {
				if (classAttr.getValue().equals("title-fullwidth")
						|| classAttr.getValue().equals("title")) {

					String id = s.getStartTag().getAttributes().get("id")
							.getValue().replace("divTitle", "");

					for (Element a : s.getAllElements(HTMLElementName.A)) {
						// System.out.println(a.toString());
						Attribute classAttr2 = a.getStartTag().getAttributes()
								.get("id");
						if (classAttr2 != null) {
							if (classAttr2.getValue().equals(id + "Title")) {
								Source htmlSource = new Source(a.getContent()
										.toString());
								String paperTitle = StringUtils
										.formatInLineSingleSpace(htmlSource
												.getTextExtractor().toString());
								litBuilder.setTitle(paperTitle);
							} else if (classAttr2.getValue().equals(
									id + "Citation")) {
								Source htmlSource = new Source(a.getContent()
										.toString());
								String citedInfo = StringUtils
										.formatInLineSingleSpace(htmlSource
												.getTextExtractor().toString()
												.replaceAll("Citations: ", ""));
								try {
									Integer numCitations = Integer
											.parseInt(citedInfo);
									litBuilder
											.setMsAcademicNumCitations(numCitations);
								} catch (NumberFormatException e) {
									// TODO error handling
									e.printStackTrace();
								}
							}
						}
					}
				} else if (classAttr.getValue().equals("content")) {

					// authors

					String[] parts = s.getContent().toString()
							.split("<span class=\"span-break\"\\s?>, </span>");
					for (String part : parts) {
						AuthorBuilder authBuilder = new AuthorBuilder();
						// ID
						Source htmlSource = new Source(part);
						if (htmlSource.getFirstElement("a") != null) {
							String authorURL = htmlSource.getFirstElement("a")
									.getAttributeValue("href");
							java.util.regex.Matcher m = AUTHOR_ID_PATTERN
									.matcher(authorURL);
							if (m.find()) {
								authBuilder.setMsAcademicID(m.group(1));
							} else {
								// TODO feedback
							}
						}
						// name
						String nameString = htmlSource.getTextExtractor()
								.toString();
						try {
							Utils.setFirstMiddleLastNameFromNameString(
									authBuilder, nameString);
						} catch (PatternMismatchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (litBuilder.getAuthors() == null)
							litBuilder.setAuthors(new HashSet<Author>());
						litBuilder.getAuthors().add(authBuilder.getObject());
					}

				} else if (classAttr.getValue().equals("conference")) {

					Source htmlSource = new Source(s.getContent().toString());

					if (htmlSource.getFirstElementByClass("conference-name") != null)
						litBuilder.setPublicationContext(htmlSource
								.getFirstElementByClass("conference-name")
								.getTextExtractor().toString());

					if (htmlSource.getFirstElement("span") != null) {
						String contextType = htmlSource.getFirstElement("span")
								.getTextExtractor().toString();
						if (contextType.equals("Journal:"))
							litBuilder.setType(LiteratureType.JOURNAL_PAPER);
						if (contextType.equals("Conference:"))
							litBuilder.setType(LiteratureType.CONFERENCE_PAPER);
					}

					for (Element q : s.getAllElements(HTMLElementName.SPAN)) {
						// System.out.println(q.toString());
						Attribute classAttr3 = q.getStartTag().getAttributes()
								.get("class");
						if (classAttr3 != null
								&& classAttr3.getValue().equals("year")) {
							htmlSource = new Source(q.getContent().toString());
							String venueYearStr = StringUtils
									.formatInLineSingleSpace(htmlSource
											.getTextExtractor().toString());
							venueYearStr = venueYearStr.substring(
									venueYearStr.lastIndexOf(",") + 1).trim();
							Integer venueYear;
							try {
								venueYear = Integer.parseInt(venueYearStr);
							} catch (NumberFormatException ae) {
								venueYear = null;
							}
							litBuilder.setYear(venueYear);
							break;
						}
						// class="year"
					}
				}
			}
		}

		return litBuilder.getObject();

	}
}
