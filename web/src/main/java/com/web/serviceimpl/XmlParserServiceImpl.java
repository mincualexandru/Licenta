package com.web.serviceimpl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.web.model.Measurement;
import com.web.model.UserDevice;
import com.web.service.XmlParserService;
import com.web.utils.BandTypeMeasurement;
import com.web.utils.ScaleTypeMeasurement;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class XmlParserServiceImpl implements XmlParserService {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public boolean readAllMeasurementsFromXmlFile(Set<UserDevice> userDevices, String username) throws ParseException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			Document doc = readXML(factory, username);
			Node parentNode = doc.getDocumentElement();
			removeEmptyNodes(doc);
			NodeList childNodes = parentNode.getChildNodes();
			getAllMeasurements(childNodes, userDevices);
			int lenght = childNodes.getLength();
			if (lenght != 0) {
				return true;
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Document readXML(DocumentBuilderFactory factory, String username)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = factory.newDocumentBuilder();
		Path currentPath = Paths.get(".");
		Path absolutePath = currentPath.toAbsolutePath();
		Document doc = builder.parse("file:///" + absolutePath + "/src/main/resources/static/uploads/" + username
				+ "/export/export_sănătate_apple/export.xml");
		doc.getDocumentElement().normalize();
		return doc;
	}

	@Override
	public void removeEmptyNodes(Document doc) throws XPathExpressionException {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");
		NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < emptyTextNodes.getLength(); i++) {
			Node emptyTextNode = emptyTextNodes.item(i);
			emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}
	}

	@Override
	public NodeList getAllMeasurements(NodeList childNodes, Set<UserDevice> userDevices) throws ParseException {
		for (int i = 2; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				String name = element.getAttribute("type");
				String valueOfMeasurement = element.getAttribute("value");
				String unitOfMeasurement = element.getAttribute("unit");
				String startDate = element.getAttribute("startDate");
				String endDate = element.getAttribute("endDate");
				String value = element.getAttribute("value");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				Date parsedStartDate = dateFormat.parse(startDate);
				Timestamp startTimestamp = new java.sql.Timestamp(parsedStartDate.getTime());
				Date parsedEndDate = dateFormat.parse(endDate);
				Timestamp endTimestamp = new java.sql.Timestamp(parsedEndDate.getTime());
				boolean goodValue = true;
				if (value.equals("HKCategoryValueSleepAnalysisAwake")
						|| value.equals("HKCategoryValueSleepAnalysisInBed")) {
					goodValue = false;
				}
				if (goodValue) {
					Measurement measure = new Measurement();
					measure.setStartDate(startTimestamp);
					measure.setEndDate(endTimestamp);
					measure.setUnitOfMeasurement(unitOfMeasurement);
					measure.setName(name);
					measure.setFromXml(true);
					if (name.equals("HKCategoryTypeIdentifierSleepAnalysis")) {
						Integer difference = (int) (parsedEndDate.getTime() - parsedStartDate.getTime());
						Integer differenceMinutes = difference / (60 * 1000);
						measure.setValue(differenceMinutes);
						if (differenceMinutes > 1) {
							measure.setUnitOfMeasurement("minutes");
						} else {
							measure.setUnitOfMeasurement("minute");
						}
					} else {
						measure.setValue(Float.parseFloat(valueOfMeasurement));
					}
					for (UserDevice userDevice : userDevices) {
						if (userDevice.getDevice().getName().equals("Bratara") && BandTypeMeasurement.contains(name)) {
							measure.setUserDevice(userDevice);
						} else if (userDevice.getDevice().getName().equals("Cantar Inteligent")
								&& ScaleTypeMeasurement.contains(name)) {
							measure.setUserDevice(userDevice);
						}
					}
					if (!(measure.getUserDevice() == null)) {
						insertMeasure(measure);
					}
				}
			}
		}
		return childNodes;
	}

	@Transactional
	public void insertMeasure(Measurement measure) {
		entityManager.createNativeQuery(
				"INSERT INTO measurements (user_device_id, name, value, unit_of_measurement, start_date, end_date, from_xml) VALUES (?,?,?,?,?,?,?)")
				.setParameter(1, measure.getUserDevice()).setParameter(2, measure.getName())
				.setParameter(3, measure.getValue()).setParameter(4, measure.getUnitOfMeasurement())
				.setParameter(5, measure.getStartDate()).setParameter(6, measure.getEndDate())
				.setParameter(7, measure.getFromXml()).executeUpdate();
	}

}