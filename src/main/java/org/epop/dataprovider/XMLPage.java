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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLPage {

	private Document doc;
	private XPath xpath;

	public XMLPage(String xmlCode) throws ParserConfigurationException,
			SAXException, IOException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		this.doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(
				xmlCode.getBytes("utf-8"))));

		this.xpath = XPathFactory.newInstance().newXPath();

	}

	public XMLPage(Document doc) {
		this.doc = doc;
		this.xpath = XPathFactory.newInstance().newXPath();
	}

	protected XMLPage() {
		this.xpath = XPathFactory.newInstance().newXPath();
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getStringByXPath(String xPathString)
			throws XPathExpressionException {
		return (String) this.xpath.evaluate(xPathString, getDoc(),
				XPathConstants.STRING);
	}

	public NodeList getNodeSetByXPath(String xPathString)
			throws XPathExpressionException {
		return (NodeList) this.xpath.evaluate(xPathString, getDoc(),
				XPathConstants.NODESET);
	}

	public Node getNodeByXPath(String xPathString)
			throws XPathExpressionException {
		return (Node) this.xpath.evaluate(xPathString, getDoc(),
				XPathConstants.NODE);
	}

}
