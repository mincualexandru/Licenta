package com.web.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.web.model.UserDevice;

public interface XmlParserService {
	boolean readAllMeasurementsFromXmlFile(Set<UserDevice> userDevices, String username) throws ParseException;

	void removeEmptyNodes(Document doc) throws XPathExpressionException;

	void getAllMeasurements(NodeList childNodes, Set<UserDevice> userDevices) throws ParseException;

	Document readXML(DocumentBuilderFactory factory, String username)
			throws ParserConfigurationException, SAXException, IOException;
}
