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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.params.ClientPNames;
//import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.epop.dataprovider.DataProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.github.bfour.fpjcommons.lang.Tuple;
import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.AuthorService;

public class GoogleScholarProvider extends DataProvider {
	// Try the regex here: http://regex101.com/r/xR1aS1/3
	private static final Pattern citespattern = Pattern.compile("(\\d+)");

	private static final List<String> malformed = new LinkedList<String>();
	private static final int MAX_NUM_PAPERS = 20;
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
	protected Reader getHTMLDoc(String htmlParams, int pageTurnLimit) {
		// connect to the server
		// build the parameter list
		URI uri;
		String responseBody = null;

		try {

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
				while (newResponseBody
						.contains("<b style=\"display:block;margin-left:50px\">Next</b>")) {
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
						counter++;
					} else {
						break;
					}
					if (counter >= pageTurnLimit) // TODO make param
						break;
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
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
	protected List<Literature> parsePage(Reader page, AuthorService authServ)
			throws DatalayerException {
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
			System.out.println("Grossi problemi");
			e.printStackTrace();
		} // for (Document doc : docs) {

		for (Element article : doc.select(".gs_ri")) {
			try {

				LiteratureBuilder litBuilder = new LiteratureBuilder();

				String title = article.select(".gs_rt").text();
				title = title.replaceAll("\\[PDF\\]\\[PDF\\] ", "");
				if (title.isEmpty())
					throw new DatalayerException(
							"title retrieved by parsing is empty");
				litBuilder.setTitle(title);

				String rawHTML = article.select(".gs_a").html();

				// split by " - " (authors - publication, year - publisher)
				StringTokenizer dashTokenizer = new StringTokenizer(rawHTML,
						" - ");
				if (dashTokenizer.countTokens() != 3)
					throw new DatalayerException(
							"dashTokenizer should have three sections (authors - publication, year - publisher), found "
									+ dashTokenizer.countTokens()
									+ "; maybe Google Scholar layout has changed");
				String namesHTML = dashTokenizer.nextToken();
				String publicationHTML = dashTokenizer.nextToken();
				String publisherHTML = dashTokenizer.nextToken();

				// authors
				List<Author> authors = getAuthorsFromHTMLSection(namesHTML,
						authServ);
				litBuilder.setAuthors(authors);

				// publication
				StringTokenizer commaTokenizer = new StringTokenizer(
						publicationHTML, ", ");
				if (commaTokenizer.countTokens() == 2) {
					String publication = commaTokenizer.nextToken();
					try {
						Integer year = Integer.parseInt(commaTokenizer.nextToken());
					} catch (NumberFormatException e) {
						// throw new ServiceException(
						// "publicationHTML subsection has invalid format: failed to parse publication year");
						// TODO (low) logging
						
					}
				} else {
					// TODO logging/notify user
				}

				// publisher

				String citedby = article.select(".gs_fl a[href*=cites]").text();
				Matcher cm = citespattern.matcher(citedby);
				int cites = cm.find() ? Integer.parseInt(cm.group(1)) : 0;

				// Paper paper = new Paper(authors, title, place.replaceAll(
				// "(\u0097|…)", ""), year, cites);

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

	private List<Author> getAuthorsFromHTMLSection(String htmlSection,
			AuthorService authServ) throws ServiceException {

		htmlSection = htmlSection.replace("…", "");

		List<Author> authors = new ArrayList<>();

		StringTokenizer commaTokenizer = new StringTokenizer(htmlSection, ", ");

		// only one author
		if (commaTokenizer.countTokens() == 0) {
			authors.add(getAuthorFromHTMLSubSection(htmlSection, authServ));
			return authors;
		}

		// more than one author
		while (commaTokenizer.hasMoreTokens()) {
			authors.add(getAuthorFromHTMLSubSection(commaTokenizer.nextToken(),
					authServ));
		}
		return authors;

	}

	private Author getAuthorFromHTMLSubSection(String subsection,
			AuthorService authServ) throws ServiceException {

		Pattern authorWithIDPattern = Pattern
				.compile("citations\\?user=(.*?)&.*?>(.*?)<");

		Matcher matcher = authorWithIDPattern.matcher(subsection);
		if (matcher.find()) {
			String gScholarID = matcher.group(1);
			String name = matcher.group(2);
			// check if author already in DB
			Author authInDB = authServ.getByGScholarID(gScholarID);
			if (authInDB != null) {
				return authInDB;
			} else {
				// otherwise create new one
				Tuple<String, String> tuple = getFirstAndLastNameFromNameString(name);
				return new Author(tuple.getA(), tuple.getB(), gScholarID, null);
			}
		} else {
			// no ID for this author
			Tuple<String, String> tuple = getFirstAndLastNameFromNameString(subsection);
			return new Author(tuple.getA(), tuple.getB(), null, null);
		}

	}

	public Tuple<String, String> getFirstAndLastNameFromNameString(String name)
			throws ServiceException {
		name = name.trim();
		// first token is first name, others are last name
		StringTokenizer spaceTokenizer = new StringTokenizer(name, " ");
		if (!spaceTokenizer.hasMoreTokens())
			throw new ServiceException(
					"getFirstAndLastNameFromNameString failed: no tokens found");
		String first = spaceTokenizer.nextToken();
		StringBuilder last = new StringBuilder();
		while (spaceTokenizer.hasMoreTokens()) {
			last.append("-");
			last.append(spaceTokenizer.nextToken());
		}
		return new Tuple<String, String>(first, last.toString());
	}

	// TODO substitute system.out with logger
	// private Paper extractPaper(Segment element) {
	// // navigate the element
	// // get the title in the H3 element
	// List<Element> titles = element.getAllElements(HTMLElementName.H3);
	// assert titles.size() == 1;
	// List<Element> childH3 = titles.get(0).getChildElements();
	// Element title = childH3.get(0);
	// System.out.println("---");
	// // sometimes the tag contains a PDF link
	// // skip "<span class=gs_ctc>[PDF]</span>"
	// if (title.getStartTag().getName().equals(HTMLElementName.SPAN)) {
	// // assert childH3.size() > 1: "childH3.size() = " + childH3.size() +
	// // "\ntitles = " +titles.get(0).getContent().toString();
	// if (childH3.size() > 1) {
	// title = childH3.get(1);
	// }
	// }
	// // get the paper title from inside the tag
	// String paperTitle = StringUtils.formatInLineSingleSpace(title
	// .getContent().toString());
	// // sometimes the first entry is the user profiles - to skip
	// if (paperTitle.startsWith("User profiles for")) {
	// // get the ID
	//
	// return null;
	// }
	// // get the URL from the tag
	// System.out.println("URL: " + title.getStartTag());
	// System.out.println("title: " + paperTitle);
	// // get the authors and publication
	// // search for <span class=gs_a>
	// Pattern gs_a = Pattern.compile("(.)*gs_a(.)*");
	// List<Element> authors = element.getAllElements("class", gs_a);
	// assert (authors.size() == 1);
	// Element author = authors.get(0).getAllElements().get(0);
	// String authorVenue = author.getContent().toString()
	// .replaceAll("&hellip;", "");
	// // several formats:
	// // 1. <author> &hellip; - <place> , <year> -
	// // 2. <author> &hellip; - <place> -
	// // 3. <author> &hellip; - <year> -
	// // note that place may contain -
	// // split author form venue
	// String[] authorVenues = splitFirst(authorVenue, '-');
	// // remove HTML links if necessary
	// Source htmlSource = new Source(authorVenues[0]);
	// String paperAuthors = StringUtils.formatInLineSingleSpace(htmlSource
	// .getTextExtractor().toString());
	// System.out.println("authors: " + paperAuthors);
	// // get the venue and the year (just before -)
	// String venueYear = authorVenues[1];
	// String paperPlace;
	// // if it contains the venue
	// // it may not contain the place
	// // <place>, <year> or just <year> or just <place>
	// String[] data = splitLast(venueYear, ',');
	// System.out.println("venue+year:" + Arrays.toString(data));
	// if (data[1].length() != 0) {
	// // it contains both
	// paperPlace = StringUtils.formatInLineSingleSpace(data[0]);
	// // venue or year
	// if (data[1].contains("-")) {
	// venueYear = data[1].substring(0, data[1].indexOf('-')).trim();
	// if (!Pattern.matches("\\d+", venueYear)) {
	// paperPlace = paperPlace + ", " + venueYear;
	// venueYear = "-1";
	// }
	// } else {
	// venueYear = data[1].trim();
	// }
	// } else {
	// int min_pos = data[0].indexOf('-');
	// if (min_pos < 0) {
	// paperPlace = venueYear;
	// venueYear = "-1";
	// } else {
	// // only one of the two ..which one?
	// // if it is a number
	// venueYear = data[0].substring(0, min_pos).trim();
	// if (Pattern.matches("\\d+", venueYear)) {
	// // System.out.println('"'+ venueYear +'"');
	// paperPlace = "";
	// } else {
	// paperPlace = venueYear;
	// venueYear = "-1";
	// }
	// }
	// }
	// System.out.println("place: " + paperPlace);
	// System.out.println("year:" + venueYear);
	// // assert titles.size() == 1;
	// // get the number of citations
	// Pattern gs_fl = Pattern.compile("gs_fl");
	// List<Element> citedinfos = element.getAllElements("class", gs_fl);
	// // assert (citedinfos.size() == 1): "citedinfos.size() = " +
	// // citedinfos.size();
	// String citedInfo = null;
	// if (citedinfos.size() == 0) {
	// citedInfo = "0";
	// } else {
	// citedInfo = citedinfos.get(0).toString();
	// citedInfo = StringUtils.formatInLineSingleSpace(citedInfo);
	// }
	// String string = "Cited by";
	// int indexOfCitedBy = citedInfo.indexOf(string);
	// if (indexOfCitedBy > 0) {
	// citedInfo = citedInfo.substring(indexOfCitedBy);
	// citedInfo = citedInfo.substring(string.length(),
	// citedInfo.indexOf('<')).trim();
	// System.out.println("citations:" + citedInfo);
	// } else {
	// citedInfo = "0";
	// }
	// // build the paper
	// return new Paper(paperAuthors, paperTitle, paperPlace,
	// Integer.parseInt(venueYear), Integer.parseInt(citedInfo));
	// }

}
