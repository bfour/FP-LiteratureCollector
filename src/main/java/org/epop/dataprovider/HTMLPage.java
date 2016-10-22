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

package org.epop.dataprovider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

public class HTMLPage extends XMLPage {

	private String rawCode;

	public HTMLPage(String uri) throws ClientProtocolException, IOException,
			ParserConfigurationException, URISyntaxException {
		this(new URI(uri));
	}

	public HTMLPage(URI uri) throws ClientProtocolException, IOException,
			ParserConfigurationException {
		super();
		getCode(uri);
	}

	private void getCode(URI uri) throws ClientProtocolException,
			IOException, ParserConfigurationException {

		// HttpGet httpget = new HttpGet(uri);
		// HttpClient httpclient = new DefaultHttpClient();
		// ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// this.rawCode = httpclient.execute(httpget, responseHandler);
		//
		// TagNode tagNode = new HtmlCleaner().clean(this.rawCode);
		// return new DomSerializer(new CleanerProperties()).createDOM(tagNode);

		HttpGet request = new HttpGet(uri);

		HttpContext HTTP_CONTEXT = new BasicHttpContext();
		HTTP_CONTEXT
				.setAttribute(
						CoreProtocolPNames.USER_AGENT,
						"Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
		request.setHeader("Referer", "http://www.google.com");
		request.setHeader("User-Agent",
				"Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request, HTTP_CONTEXT);

		if (response.getStatusLine().getStatusCode() < 200
				|| response.getStatusLine().getStatusCode() >= 400) {
			throw new IOException("bad response, error code = "
					+ response.getStatusLine().getStatusCode() + " for " + uri);
		}

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			this.rawCode = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		}
		
	}

	public String getRawCode() {
		return rawCode;
	}

	@Override
	public Document getDoc() {
		if (super.getDoc() != null)
			return super.getDoc();
		TagNode tagNode = new HtmlCleaner().clean(this.rawCode);
		try {
			setDoc(new DomSerializer(new CleanerProperties())
					.createDOM(tagNode));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return super.getDoc();
	}

}
