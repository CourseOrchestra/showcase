package ru.curs.showcase.core.html.xform;

import java.io.*;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.slf4j.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.*;

/**
 * Команда получения главной xforms.
 * 
 */
public final class MainXFormGetCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainXFormGetCommand.class);

	private static final String XFORMS_CREATE_ERROR = "Ошибка при формировании главной XForms";

	private static final String MAIN_XFORM = "mainXForm.xml";

	public List<String> execute() {
		List<String> result = null;

		InputStream data = MainXFormGetCommand.class.getResourceAsStream(MAIN_XFORM);

		DocumentBuilder db = XMLUtils.createBuilder();
		try {
			Document template = db.parse(data);
			logInput(template);

			String html = XFormProducer.getHTML(template, null);
			logOutput(html);
			result = XFormCutter.xFormParts(html);
		} catch (SAXException | IOException | TransformerException | XMLStreamException e) {
			throw new XSLTTransformException(String.format(XFORMS_CREATE_ERROR, MAIN_XFORM), e);
		}

		return result;
	}

	private void logOutput(final String html) {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			Marker marker = MarkerFactory.getDetachedMarker(XMLUtils.XSL_MARKER);
			marker.add(HandlingDirection.OUTPUT.getMarker());
			marker.add(MarkerFactory.getMarker(String.format("xslTransform=%s",
					XSLTransformerPoolFactory.XSLTFORMS_XSL)));
			LOGGER.info(marker, html);
		}
	}

	private void logInput(final Document template) {
		if (!(LOGGER.isInfoEnabled() && AppInfoSingleton.getAppInfo().isEnableLogLevelInfo())) {
			return;
		}
		Marker marker = MarkerFactory.getDetachedMarker(XMLUtils.XSL_MARKER);
		marker.add(HandlingDirection.INPUT.getMarker());
		marker.add(MarkerFactory.getMarker(String.format("xslTransform=%s",
				XSLTransformerPoolFactory.XSLTFORMS_XSL)));
		LOGGER.info(marker, XMLUtils.documentToString(template));

	}
}
