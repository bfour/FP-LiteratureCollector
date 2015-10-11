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
