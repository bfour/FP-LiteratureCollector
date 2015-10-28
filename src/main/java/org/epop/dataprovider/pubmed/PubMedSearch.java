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
package org.epop.dataprovider.pubmed;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.ClientProtocolException;
import org.epop.dataprovider.DataProvider;
import org.epop.dataprovider.HTMLPage;
import org.epop.dataprovider.XMLPage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;

public class PubMedSearch extends DataProvider {

	private static enum PubMedDisplayUnits {
		FIVE(5), TEN(10), TWENTY(20), FIFTY(50), ONE_HUNDRED(100), TWO_HUNDRED(
				200);

		private int maxDisplayedResults;

		PubMedDisplayUnits(int maxDisplayedResults) {
			this.maxDisplayedResults = maxDisplayedResults;
		}

		public int getMaxDisplayedResults() {
			return maxDisplayedResults;
		}

		public static PubMedDisplayUnits getDispmaxParamByDesiredResults(
				int desiredResultNum) {
			for (PubMedDisplayUnits unit : values()) {
				if (unit.getMaxDisplayedResults() <= desiredResultNum)
					return unit;
			}
			return null;
		}

	}

	Logger logger = Logger.getLogger(PubMedSearch.class.getCanonicalName());

	private static final String PUBMED_SEARCH = "http://www.ncbi.nlm.nih.gov/pubmed/";
	private static final long DELAY = 18611;
	private static final int SEARCH_STEP = 10;

	private static final String ID_PATTERN_STRING = ".*/pubmed/(\\d+).*";
	private static Pattern ID_PATTERN;

	@Override
	public String getDescription() {
		return "PubMed";
	}

	@Override
	protected Reader getHTMLDoc(String htmlParams, int pageTurnLimit,
			boolean initialWait) {

		try {

			if (initialWait)
				Thread.sleep(DELAY);

			// TODO (low) implemented cleaner solution (desired num. of results
			// in AtomicRequest instead of page turns)
			PubMedDisplayUnits desiredResultUnit = PubMedDisplayUnits
					.getDispmaxParamByDesiredResults(pageTurnLimit
							* SEARCH_STEP);

			String uriString = PUBMED_SEARCH + "?" + htmlParams;
			if (desiredResultUnit != null)
				uriString += "&dispmax=" + desiredResultUnit;
			URI uri = new URI(uriString);
			HTMLPage page = new HTMLPage(uri);

			return new StringReader(page.getRawCode());

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

		return null;
		//
		// // return the result as string
		// return new StringReader(responseBody);

	}

	@Override
	protected List<Literature> parsePage(Reader page) {
		List<Literature> result = new ArrayList<Literature>();
		try {
			Source source = new Source(page);
			for (Element element : source
					.getAllElements("class", "rslt", false)) {
				Literature lit = extractPaper(element);
				if (lit != null)
					result.add(lit);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private Literature extractPaper(Element element) {

		if (ID_PATTERN == null)
			ID_PATTERN = Pattern.compile(ID_PATTERN_STRING);

		LiteratureBuilder litBuilder = new LiteratureBuilder();

		Element titleElem = element.getFirstElement("class", "title", false);
		if (titleElem != null) {

			Matcher matcher = ID_PATTERN.matcher(titleElem.getAllElements("a")
					.get(0).getAttributeValue("href"));
			if (matcher.find()) {
				String id = matcher.group(1);
				// String entryLink = PUBMED_SEARCH + id;
				litBuilder.setPubmedID(id);
				try {
					articleToLiterature(id, litBuilder);
				} catch (URISyntaxException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// TODO error handling
			}

		}

		return litBuilder.getObject();

	}

	private void articleToLiterature(String articleID, LiteratureBuilder builder)
			throws ClientProtocolException, IOException,
			ParserConfigurationException, URISyntaxException,
			XPathExpressionException, SAXException {

		String pubMedURL = "http://www.ncbi.nlm.nih.gov/pubmed/" + articleID;
		HTMLPage htmlPage = new HTMLPage(pubMedURL);

		HTMLPage page = new HTMLPage("http://www.ncbi.nlm.nih.gov/pubmed/"
				+ articleID + "?report=xml&format=text");
		String xmlCode = StringEscapeUtils.unescapeHtml(page
				.getStringByXPath("html/body/pre/text()"));
		XMLPage xmlPage = new XMLPage(xmlCode);

		// title
		try {
			builder.setTitle(xmlPage.getStringByXPath("//ArticleTitle/text()"));
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// abstract text
		try {
			NodeList nodes = xmlPage.getNodeSetByXPath("//Abstract");
			int i = 0;
			Node node;
			StringBuilder stringBuilder = new StringBuilder("<html>");
			while ((node = nodes.item(i)) != null) {
				if (node.getAttributes().getNamedItem("Label") != null) {
					stringBuilder.append("<b>");
					stringBuilder.append(node.getAttributes()
							.getNamedItem("Label").getTextContent());
					stringBuilder.append("</b><br/>");
				}
				stringBuilder.append(node.getTextContent().trim()
						.replaceAll("\t", "").replaceAll("\\s{2,}", " "));
				stringBuilder.append("<br/>");
				i++;
			}
			builder.setAbstractText(stringBuilder.toString() + "</html>");
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// authors
		try {
			Set<Author> authors = new HashSet<>();
			int i = 0;
			while (true) {
				i++;
				String lastName = xmlPage
						.getStringByXPath("//AuthorList/Author[" + i
								+ "]/ForeName");
				String firstName = xmlPage
						.getStringByXPath("//AuthorList/Author[" + i
								+ "]/LastName");
				if (lastName.isEmpty() && firstName.isEmpty())
					break;
				authors.add(new AuthorBuilder().setFirstName(firstName)
						.setLastName(lastName).getObject());
			}
			builder.setAuthors(authors);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// DOI
		try {
			String doi = xmlPage
					.getStringByXPath("//ArticleId[@IdType='doi']/text()");
			if (doi != null && !doi.isEmpty())
				builder.setDOI(doi);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// year
		try {
			String year = xmlPage.getStringByXPath("//DateRevised/Year/text()");
			if (year != null && !year.isEmpty())
				builder.setYear(Integer.parseInt(year));
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// publication context
		try {
			String journalTitle = xmlPage
					.getStringByXPath("//Article/Journal/Title/text()");
			if (journalTitle != null && !journalTitle.isEmpty()) {
				builder.setPublicationContext(journalTitle);
				builder.setType(LiteratureType.JOURNAL_PAPER);
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// URLs
		try {

			NodeList nodes = htmlPage
					.getNodeSetByXPath(".//*[@id='maincontent']//div[@class='linkoutlist']/ul[1]/li");
			int i = 0;
			Node node;
			Set<Link> webSiteLinks = new HashSet<>();
			Set<Link> fullTextLinkSet = new HashSet<Link>();

			// add pubmed page
			webSiteLinks.add(new Link("PubMed", pubMedURL));

			while ((node = nodes.item(i)) != null) {

				if (node.getFirstChild().getAttributes().getNamedItem("href") == null) {
					i++;
					continue;
				}

				String linkText = node.getFirstChild().getTextContent();
				String uri = node.getFirstChild().getAttributes()
						.getNamedItem("href").getTextContent();
				webSiteLinks.add(new Link(linkText, uri));

				if (linkText.equals("PubMed Central")) {
					HTMLPage pmcPage = new HTMLPage(uri);
					Node pdfLinkNode = pmcPage
							.getNodeByXPath(".//*[@id='rightcolumn']//div[@class='format-menu']/ul/li[4]/a/@href");
					String pdfLink = pdfLinkNode.getTextContent();
					fullTextLinkSet.add(new Link(linkText,
							"http://www.ncbi.nlm.nih.gov" + pdfLink));
				}

				i++;

			}
			builder.setWebsiteURLs(webSiteLinks);
			builder.setFulltextURLs(fullTextLinkSet);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
