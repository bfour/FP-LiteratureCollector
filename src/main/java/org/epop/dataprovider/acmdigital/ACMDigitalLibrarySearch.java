/*******************************************************************************
 * Copyright (c) 2012 fm&selab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     fm&selab - initial API and implementation
 *     
 *     TESI DI Claudio Capelli c.capelli2@studenti.unibg.it
 *     
 ******************************************************************************/
package org.epop.dataprovider.acmdigital;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.epop.dataprovider.DataProvider;
import org.epop.dataprovider.HTMLPage;
import org.epop.dataprovider.PatternMismatchException;
import org.epop.dataprovider.Utils;
import org.epop.utils.StringUtils;
import org.w3c.dom.Node;

import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;

public class ACMDigitalLibrarySearch extends DataProvider {

	private static final String DOMAIN = "http://dl.acm.org/";
	private static final String BASE_URL = "http://dl.acm.org/results.cfm";
	private static final long DELAY = 18611;
	private static final String AUTHOR_ID_PATTERN_STRING = ".*author_page\\.cfm\\?id=(\\d+).*";
	private static Pattern AUTHOR_ID_PATTERN;
	private static final String CITATION_COUNT_PATTERN_STRING = ".*Citation Count: (\\d+).*";
	private static Pattern CITATION_COUNT_PATTERN;
	private static final String ID_PATTERN_STRING = ".*[?&]id=(\\d+).*";
	private static Pattern ID_PATTERN;

	static private Logger logger = Logger
			.getLogger(ACMDigitalLibrarySearch.class);

	@Override
	public String getDescription() {
		return "ACM Digital Library";
	}

	@Override
	/**getHTMLDoc
	 * connect to the server and return the response at the query, call the method "getParameters"
	 * in order to have parameters to create the uri
	 * @return responseBody A string that contains the response at the query
	 */
	protected Reader getHTMLDoc(String htmlParams, int pageTurnLimit,
			boolean initialWait) {

		boolean exit = false; // flag to exit if the author was not found

		URI uri;
		String responseBody = "";

		try {

			if (initialWait)
				Thread.sleep(DELAY);

			uri = new URI(BASE_URL + "?" + htmlParams);
			HTMLPage page = new HTMLPage(uri);
			responseBody = page.getRawCode();

			if (pageTurnLimit == 0)
				return new StringReader(responseBody);

			int counter = 1; // there are 20 records for page
			String newResponseBody = responseBody;

			// Verifies if the author was not found
			if (responseBody.contains("was not found.")) {
				exit = true;
			}

			// take all the pages if the author was found
			while (newResponseBody.contains("next</a>") && !exit) {

				Thread.sleep(DELAY);

				URI newUri = new URI(BASE_URL + "?" + htmlParams + "&start="
						+ String.valueOf((counter * 20) + 1));
				newResponseBody = new HTMLPage(newUri).getRawCode();
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
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// logger.debug("responseBody =\n" + responseBody);
		// return the response
		return new StringReader(responseBody);

	}

	@Override
	/**parsePage
	 * Parse a page and call the method "extractPaper" in order to build a list of papers
	 * @param page Reader object that contains the html page
	 * @return result List<Paper> that contains a list of papers
	 */
	protected List<Literature> parsePage(Reader page) {

		List<Literature> result = new ArrayList<Literature>();

		try {
			Source source = new Source(page); // defines the source to parse
			/**
			 * get elements with TR, style=padding-bottom: 15px and elements
			 * style=padding-bottom: 5px; for notFound
			 */
			Iterator<Element> elementList = source.getAllElements(
					HTMLElementName.TD).iterator();
			// iterates elements with tag "td"
			while (elementList.hasNext()) {
				Element element = elementList.next();
				StartTag startTag = element.getStartTag();
				Attribute Attr = startTag.getAttributes().get("style");

				// call the method notFound to verify if the query is found
				if (Attr != null
						&& Attr.getValue().equals("padding-bottom: 5px;")) {
					if (notFound(element)) {
						return result;
					}
				}
				// style="padding-bottom: 15px" is common for every record in
				// the page
				if (Attr != null
						&& Attr.getValue().equals("padding-bottom: 15px")) {
					// Logger.debug(element.toString());
					Literature extractedPaper = extractPaper(element);
					// logger.debug(extractedPaper);

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

	/**
	 * extractPaper Extracts the parameters
	 * 
	 * @param element
	 *            Element that contains the HTML element to analyze
	 * @return Paper Object that contains the extracted parameters
	 */
	private Literature extractPaper(Element element) {

		try {
			if (AUTHOR_ID_PATTERN == null)
				AUTHOR_ID_PATTERN = Pattern.compile(AUTHOR_ID_PATTERN_STRING);
			if (CITATION_COUNT_PATTERN == null)
				CITATION_COUNT_PATTERN = Pattern
						.compile(CITATION_COUNT_PATTERN_STRING);
		} catch (PatternSyntaxException e) {
			// TODO implement error handling
			e.printStackTrace();
			return null;
		}

		LiteratureBuilder builder = new LiteratureBuilder();

		/**
		 * extract paperTitle analyze all the elements with tag "a", attribute
		 * class="medium-text" and extract the title
		 */
		URI pageURI = null;
		for (Element a : element.getAllElements(HTMLElementName.A)) {
			Attribute classAttr = a.getStartTag().getAttributes().get("class");
			if (classAttr != null) {
				if (classAttr.getValue().equals("medium-text")) {
					Source htmlSource = new Source(a.getContent().toString());
					String title = StringUtils
							.formatInLineSingleSpace(htmlSource
									.getTextExtractor().toString());
					logger.debug(title);
					builder.setTitle(title);
					String href = a.getAttributeValue("href");
					if (builder.getWebsiteURLs() == null)
						builder.setWebsiteURLs(new HashSet<Link>());
					try {
						pageURI = new URI(DOMAIN + href + "&preflayout=flat");
						builder.getWebsiteURLs().add(new Link("ACM", pageURI));
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (ID_PATTERN == null)
							ID_PATTERN = Pattern.compile(ID_PATTERN_STRING);
						Matcher idMatcher = ID_PATTERN.matcher(href);
						if (idMatcher.find())
							builder.setAcmID(idMatcher.group(1));
						// else
						// TODO error handling
					} catch (PatternSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * extract paperAuthors analyze all the elements with tag "div",
		 * attribute class="authors" and extract the authors
		 */
		for (Element s : element.getAllElements(HTMLElementName.DIV)) {
			Attribute classAttr = s.getStartTag().getAttributes().get("class");
			if (classAttr != null) {
				if (classAttr.getValue().equals("authors")) {
					Set<Author> authors = new HashSet<>();
					for (Element q : s.getAllElements(HTMLElementName.A)) {
						Source htmlSource = new Source(q.getContent()
								.toString());
						AuthorBuilder authBuilder = new AuthorBuilder();
						String paperAuthor = StringUtils
								.formatInLineSingleSpace(htmlSource
										.getTextExtractor().toString());
						try {
							Utils.setFirstMiddleLastNameFromNameString(
									authBuilder, paperAuthor);
							Matcher matcher = AUTHOR_ID_PATTERN.matcher(q
									.getAttributeValue("href"));
							if (matcher.find()) {
								authBuilder.setAcmID(matcher.group(1));
							} else {
								// TODO error handling
							}
							authors.add(authBuilder.getObject());
						} catch (PatternMismatchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					builder.setAuthors(authors);
					/**
					 * extract paperPlace analyze all the elements with tag
					 * "div", attribute class="addinfo" and extract the place
					 */
				} else if (classAttr.getValue().equals("addinfo")) {
					for (Element g : s.getAllElements(HTMLElementName.DIV)) {
						Source htmlSource = new Source(g.getContent()
								.toString());
						String paperPlace = StringUtils
								.formatInLineSingleSpace(htmlSource
										.getTextExtractor().toString());
						logger.debug(paperPlace);
						builder.setPublicationContext(paperPlace);
					}
				}
			}
		}

		/**
		 * extract citedInfo analyze all the elements with tag "td" that
		 * contains the string "Bibliometrics", replace keywords that identify
		 * the parameters with a separator, used to split the string.The
		 * unnecessary text is deleted calling the "replace" method of the class
		 * String.Finally is extracted the info using StringTokenizer
		 */
		for (Element y : element.getAllElements(HTMLElementName.TD)) {
			if (y.toString().contains("Bibliometrics")) {
				Matcher matcher = CITATION_COUNT_PATTERN
						.matcher(y.getContent());
				if (matcher.find()) {
					builder.setAcmNumCitations(Integer.parseInt(matcher
							.group(1)));
				} else {
					// TODO error handling
				}
			}

			/**
			 * extract venueYear analyze all the elements with tag "td" that
			 * contains a month and call the method ExtractYear to extract the
			 * year
			 */
			if (y.toString().contains("January")
					|| y.toString().contains("February")
					|| y.toString().contains("March")
					|| y.toString().contains("April")
					|| y.toString().contains("May")
					|| y.toString().contains("June")
					|| y.toString().contains("July")
					|| y.toString().contains("August")
					|| y.toString().contains("September")
					|| y.toString().contains("October")
					|| y.toString().contains("November")
					|| y.toString().contains("December")) {
				String s = y.getContent().toString();
				String venueYear = ExtractYear(s);
				logger.debug(venueYear);
				if (!venueYear.isEmpty()) {
					builder.setYear(Integer.parseInt(venueYear));
				}

			}
		}

		try {
			HTMLPage entryPage = new HTMLPage(pageURI);
			try {
				Node abstractTextNode = entryPage
						.getNodeByXPath("//*[@id='fback']/div[3]/div[1]");
				if (abstractTextNode != null)
					builder.setAbstractText(abstractTextNode.getTextContent());
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Node fullTextLinkNode = entryPage
						.getNodeByXPath("//*[@id='divmain']/table[1]/tbody/tr/td[1]/table[1]/tbody/tr/td[2]/a[@name='FullTextPDF']/@href");
				if (fullTextLinkNode != null) {
					String href = fullTextLinkNode.getTextContent(); // ft_gateway.cfm?id=1150304&ftid=371641&dwn=1&CFID=553795364&CFTOKEN=65402853
					if (builder.getFulltextURLs() == null)
						builder.setFulltextURLs(new HashSet<Link>());
					URI fulltextURI = new URI(DOMAIN + href);
					builder.getFulltextURLs().add(new Link("ACM", fulltextURI));
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return a new Paper if a title and a paper was found
		if (builder.getTitle() != null && builder.getAuthors() != null) {
			return builder.getObject();
		} else {
			return null;
		}

	}

	/**
	 * ExtractYear Extracts the year contained in a date
	 * 
	 * @param date
	 *            String that contains the year
	 * @return year String containing only the year
	 */
	public String ExtractYear(String date) {

		int c = 0;// number of character
		String year = "";// string with the year
		String t = "";// temporary string

		for (int i = 0; i < date.length(); i++) {
			if (isInt(String.valueOf(date.charAt(i)))) {

				c++;
				t = t + String.valueOf(date.charAt(i));

				if (t.length() == 4)
					year = t;

				if (c > 4) {
					c = 0;
					t = "";
					year = "";
				}

			} else {
				c = 0;
				t = "";
				if (year.length() == 4)
					return year;
			}
		}

		return year;
	}

	/**
	 * isInt Verifies if a string is an integer
	 * 
	 * @param s
	 *            String to verify
	 * @return true if the input is an integer
	 * @return false if the input is not an integer
	 */
	public boolean isInt(String s) {
		// call the method parseInt to verify that the string is an integer
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * notFound Verifies if the query was not found
	 * 
	 * @param element
	 *            Element that contains the HTML element to analyze
	 * @return true if the query was not found
	 * @return false if the query was found
	 */
	public boolean notFound(Element element) {
		// Take all the elements with tag "font"
		for (Element c : element.getAllElements(HTMLElementName.FONT)) {
			// attribute size=+1
			Attribute Attr3 = c.getStartTag().getAttributes().get("size");
			if (Attr3 != null) {
				if (Attr3.getValue().equals("+1")) {
					// verify if there's the string "was not found"
					if (c.toString().contains("was not found")) {
						return true;
					}
				}
			}
		}
		return false;
	}

}