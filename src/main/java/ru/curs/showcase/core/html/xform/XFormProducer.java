package ru.curs.showcase.core.html.xform;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.*;
import org.w3c.dom.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.*;

/**
 * Класс, преобразующий документ в HTML-код XForm.
 */
public final class XFormProducer extends GeneralXMLHelper {
	// public static final String XF_INSTANCE = "xf:instance";
	public static final String INSTANCE = "instance";
	public static final String XFORMS_URI = "http://www.w3.org/2002/xforms";
	public static final String EVENTS_URI = "http://www.w3.org/2001/xml-events";

	private static final int DEFAULT_BUFFER_SIZE = 1024;

	private static final String MAIN_INSTANCE = "mainInstance";

	protected static final Logger LOGGER = LoggerFactory.getLogger(XFormProducer.class);

	private XFormProducer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Возвращает org.w3c.dom.Document с пустыми данными, полученными на основе
	 * шаблона.
	 * 
	 * @param template
	 *            Шаблон
	 * 
	 * @return org.w3c.dom.Document
	 * @throws ParserConfigurationException
	 * 
	 */
	public static org.w3c.dom.Document getEmptyData(final org.w3c.dom.Document template)
			throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = db.newDocument();

		Node n = getMainInstance(template).getFirstChild();

		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				break;
			}
			n = n.getNextSibling();
		}

		n = doc.importNode(n, true);
		doc.appendChild(n);

		return doc;
	}

	/**
	 * Возвращает ноду xf:instance c id=mainInstance. Такая нода должна
	 * существовать в шаблоне для того, чтобы он работал с Showcase.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - элемент mainInstance.
	 */
	public static Node getMainInstance(final org.w3c.dom.Document doc) {
		NodeList l = doc.getElementsByTagNameNS(XFORMS_URI, INSTANCE);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i).getAttributes().getNamedItem(ID_TAG);

			// if ((n != null) && (MAIN_INSTANCE.equals(n.getTextContent()))) {

			if ((n != null)
					&& (n.getTextContent().toLowerCase().contains(MAIN_INSTANCE.toLowerCase()))) {

				return l.item(i);
			}
		}
		return null;
	}

	/**
	 * Возвращает id подформы.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - id подформы.
	 */
	public static String getSubformId(final org.w3c.dom.Document doc) {
		String subformId = "subform_default_id_";

		Node mainInstance = getMainInstance(doc);
		if (mainInstance != null) {
			String s = mainInstance.getAttributes().getNamedItem(ID_TAG).getNodeValue();
			// s = s.toLowerCase().replace(MAIN_INSTANCE.toLowerCase(), "");
			s = s.replace(MAIN_INSTANCE, "");

			if (!s.trim().isEmpty()) {
				subformId = s;
			}
		}

		return subformId;
	}

	/**
	 * Возвращает строку, являющуюся HTML-фрагментом для отображения документа в
	 * виде XForm.
	 * 
	 * @param xml
	 *            документ
	 * @param tempData
	 *            временные данные документа (если эта переменная не равна null,
	 *            эти данные подставляются в MainInstance). Необходимо для
	 *            просмотра проимпортированного содержимого формы.
	 * 
	 * @return HTML-фрагмент, пригодный для отображения в браузере
	 * @throws TransformerException
	 * @throws IOException
	 * 
	 */
	public static String getHTML(final org.w3c.dom.Document xml,
			final org.w3c.dom.Document tempData) throws TransformerException, IOException {
		insertActualData(xml, tempData);

		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info("debugg_после вставки реальных данных_" + XMLUtils.documentToString(xml));
		}

		return transform(xml);
	}

	public static String getTemplateWithData(final org.w3c.dom.Document xml,
			final org.w3c.dom.Document tempData) {
		insertActualData(xml, tempData);
		return XMLUtils.documentToString(xml);
	}

	private static String transform(final org.w3c.dom.Document xml) throws TransformerException,
			IOException {
		StringWriter sw = new StringWriter(DEFAULT_BUFFER_SIZE);
		Transformer tr =
			XSLTransformerPoolFactory.getInstance().acquire(
					XSLTransformerPoolFactory.XSLTFORMS_XSL);
		try {
			tr.setParameter("baseuri", "xsltforms/");
			tr.setParameter("xsltforms_home", AppInfoSingleton.getAppInfo().getWebAppPath()
					+ "/xsltforms/");
			tr.transform(new DOMSource(xml), new StreamResult(sw));

		} finally {
			XSLTransformerPoolFactory.getInstance().release(tr,
					XSLTransformerPoolFactory.XSLTFORMS_XSL);
		}

		String ret = sw.toString();

		return ret;
	}

	private static void insertActualData(final org.w3c.dom.Document xml,
			final org.w3c.dom.Document tempData) {
		if (tempData != null) {
			NodeList nl = xml.getElementsByTagNameNS(XFORMS_URI, INSTANCE);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);

				// if
				// (MAIN_INSTANCE.equals(n.getAttributes().getNamedItem(ID_TAG).getTextContent()))
				// {

				if (n.getAttributes().getNamedItem(ID_TAG).getTextContent().toLowerCase()
						.contains(MAIN_INSTANCE.toLowerCase())) {

					if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
						LOGGER.info("debugg_mainInstance_количество детей в начале_1__"
								+ n.getChildNodes().getLength());
					}

					while (n.getFirstChild() != null) {
						n.removeChild(n.getFirstChild());
					}

					if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
						LOGGER.info("debugg_mainInstance_количество детей после первого удаления_2__"
								+ n.getChildNodes().getLength());
					}

					n.setTextContent("");

					if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
						LOGGER.info("debugg_mainInstance_количество детей после второго удаления_3__"
								+ n.getChildNodes().getLength());
					}

					Node nn = xml.importNode(tempData.getDocumentElement(), true);
					n.appendChild(nn);
					break;
				}
			}
		}
	}

}