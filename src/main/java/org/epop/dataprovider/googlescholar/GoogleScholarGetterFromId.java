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
import java.util.Iterator;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.epop.utils.StringUtils;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;

/**
 * from google when the researcher has an ID
 * 
 * @author garganti, modified by Florian Pollak
 * 
 */
public class GoogleScholarGetterFromId {
	private final static String GOOGLE_SCHOLAR = "scholar.google.com/citations";

	static List<Literature> getFromId(String userId) {
		// http://scholar.google.it/citations?user=q21xxm4AAAAJ&pagesize=100
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("user", userId));
		qparams.add(new BasicNameValuePair("pagesize", "100"));

		URI uri;
		String responseBody = null;
		try {
			uri = URIUtils.createURI("http", GOOGLE_SCHOLAR, -1, "",
					URLEncodedUtils.format(qparams, "UTF-8"), null);
			uri = new URI(uri.toString().replace("citations/?", "citations?"));
			HttpGet httpget = new HttpGet(uri);
			System.out.println(httpget.getURI());
			HttpClient httpclient = new DefaultHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
			//System.out.println(responseBody);
			int counter = 1;
			String newResponseBody = responseBody;
			while (newResponseBody.contains("class=\"cit-dark-link\">Next &gt;</a>")) {
				URI newUri = new URI(uri.toString() + "&cstart=" + counter * 100);
				httpget = new HttpGet(newUri);
				System.out.println(httpget.getURI());
				httpclient = new DefaultHttpClient();
				newResponseBody = httpclient.execute(httpget, responseHandler);
				//System.out.println(newResponseBody);
				responseBody = responseBody + newResponseBody;
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
		}
		// return the result as string
		return parsePage(new StringReader(responseBody));
	}

	private static List<Literature> parsePage(Reader page) {
		List<Literature> result = new ArrayList<Literature>();
		try {
			Source source = new Source(page);
			Iterator<Element> elementList = source.getAllElements(
					HTMLElementName.TR).iterator();
			while (true) {
				if (!elementList.hasNext())
					break;
				Element element = elementList.next();
				//System.out.println(element.toString());
				StartTag startTag = element.getStartTag();
				Attribute classAttr = startTag.getAttributes().get("class");
				if (classAttr != null && classAttr.getValue().equals("cit-table item")) {
					Literature extractedPaper = extractPaper(element);
					System.out.println(extractedPaper);
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

	private static Literature extractPaper(Element element) {
		String paperAuthors = "";
		String paperTitle = "";
		String paperPlace = "";
		int venueYear = -1;
		int citedInfo = 0;
		//System.out.println(element.toString());
		for(Element s: element.getAllElements(HTMLElementName.TD)) {
			Attribute classAttr2 = s.getStartTag().getAttributes().get("id");
			if (classAttr2 != null && classAttr2.getValue().equals("col-title")) {
				//System.out.println(s.toString());
				List<Element> links = s.getAllElements(HTMLElementName.A);
				assert links.size() == 1: "links.size() = " + links.size();
				//System.out.println(links.get(0).getContent());
				paperTitle = StringUtils.formatInLineSingleSpace(links.get(0));
				//System.out.println(paperTitle);
				List<Element> placeAuthors = s.getAllElements(HTMLElementName.SPAN);
				assert placeAuthors.size() <= 2: "placeAuthors.size() = " + placeAuthors.size();
				if(placeAuthors.size() > 0) {
					paperAuthors = StringUtils.formatInLineSingleSpace(placeAuthors.get(0));
					//System.out.println(paperAuthors);
				}
				if(placeAuthors.size() == 2) {
					paperPlace = StringUtils.formatInLineSingleSpace(placeAuthors.get(1));
					//System.out.println(paperPlace);
				}
			}
			if (classAttr2 != null && classAttr2.getValue().equals("col-year")) {
				//System.out.println(s.toString());
				String venueYearStr = StringUtils.formatInLineSingleSpace(new Source(s.getContent().toString()).getTextExtractor().toString());
				try {
					venueYear = Integer.parseInt(venueYearStr);
				}
				catch (NumberFormatException e) {
				}
				//System.out.println(venueYear);
			}
			if (classAttr2 != null && classAttr2.getValue().equals("col-citedby")) {
				//System.out.println(s.toString());
				String citedInfoStr = StringUtils.formatInLineSingleSpace(new Source(s.getContent().toString()).getTextExtractor().toString());
				try {
					citedInfo = Integer.parseInt(citedInfoStr);
				}
				catch (NumberFormatException e) {
				}
				//System.out.println(citedInfo);
			}
		}
		//System.out.println();
		LiteratureBuilder litBuilder = new LiteratureBuilder();
		// TODO implement
		return litBuilder.getObject();
	}

}
