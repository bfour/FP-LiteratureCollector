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
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.epop.dataprovider.DataProvider;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;

public class PubMedSearch extends DataProvider {

	Logger logger = Logger.getLogger(PubMedSearch.class.getCanonicalName());

	private static final String PUBMED_SEARCH = "http://www.ncbi.nlm.nih.gov/pubmed/";
	private static final long DELAY = 18611;
	private static final int searchStep = 10;

	private static final String ID_PATTERN_STRING = ".*/pubmed/(\\d+).*";
	private static Pattern ID_PATTERN;

	@Override
	public String getDescription() {
		return "PubMed";
	}

	@Override
	protected Reader getHTMLDoc(String htmlParams, int pageTurnLimit,
			boolean initialWait) {

		URI uri;
		String responseBody = "";

		try {

			if (initialWait)
				Thread.sleep(DELAY);

			uri = URIUtils.createURI("http", PUBMED_SEARCH, -1, "", htmlParams
					+ "&dispmax=200", null);
			HttpGet httpget = new HttpGet(uri);
			System.out.println(httpget.getURI());
			HttpClient httpclient = new DefaultHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);

			// if (pageTurnLimit == 0)
			return new StringReader(responseBody);

			// int counter = 1;
			// String newResponseBody = responseBody;
			// while (!newResponseBody
			// .contains("<span title=\"Inactive next page of results\" class=\"inactive page_link next\">"))
			// {
			//
			// Thread.sleep(DELAY);
			//
			// URI newUri = URIUtils.createURI(
			// "http",
			// PUBMED_SEARCH,
			// -1,
			// "",
			// htmlParams + "&start="
			// + String.valueOf((counter * searchStep) + 1)
			// + "&end="
			// + String.valueOf((counter + 1) * searchStep),
			// null);
			//
			// httpget = new HttpGet(newUri);
			// System.out.println(httpget.getURI());
			// httpclient = new DefaultHttpClient();
			// newResponseBody = httpclient.execute(httpget, responseHandler);
			// // System.out.println(newResponseBody);
			// responseBody = responseBody + newResponseBody;
			//
			// if (pageTurnLimit == counter)
			// return new StringReader(responseBody);
			//
			// counter++;
			//
			// }
			//
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
			
			Matcher matcher = ID_PATTERN.matcher(titleElem.getAttributeValue("href"));
			if (matcher.find()) {
				String id = matcher.group(1);
				String entryLink = PUBMED_SEARCH + id;
				try {
					articleToLiterature(id, litBuilder);
				} catch (URISyntaxException | IOException e) {
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
			throws URISyntaxException, ClientProtocolException, IOException {
		
		URI uri = URIUtils.createURI("http", PUBMED_SEARCH, -1, "", articleID, null);
		HttpGet httpget = new HttpGet(uri);
		System.out.println(httpget.getURI());
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);

		Source source = new Source(responseBody);
		
		
		
	}
	
}
