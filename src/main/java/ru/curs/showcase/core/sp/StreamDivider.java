package ru.curs.showcase.core.sp;

import java.io.OutputStream;

import javax.xml.stream.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.SAXError;

/**
 *  Делитель поля settings на 2 потока -- собственно сами настройки и
 *  данные(XML-датасет). (Вынесен из CompBasedElementSPQuery)
 * 
 * @author bogatov
 * 
 */
public class StreamDivider extends DefaultHandler {
	private static final String XML_DATASET_TAG = "records";
	private static final String XML_DATASET_TAG_GEO = "tables";

	private final XMLStreamWriter writerSettings;
	private final XMLStreamWriter writerDS;

	private boolean forDS = false;

	public StreamDivider(final OutputStream osSettings, final OutputStream osDS) {
		try {
			writerSettings =
				XMLOutputFactory.newInstance().createXMLStreamWriter(osSettings,
						TextUtils.DEF_ENCODING);
			writerDS =
				XMLOutputFactory.newInstance().createXMLStreamWriter(osDS, TextUtils.DEF_ENCODING);
		} catch (XMLStreamException e) {
			throw new SAXError(e);
		}
	}

	private XMLStreamWriter getWriter() {
		if (forDS) {
			return writerDS;
		} else {
			return writerSettings;
		}
	}

	private boolean isXmlDatasetTag(final String localName) {
		return XML_DATASET_TAG.equalsIgnoreCase(localName)
				|| XML_DATASET_TAG_GEO.equalsIgnoreCase(localName);
	}

	@Override
	public void startElement(final String uri, final String localName, final String name,
			final Attributes atts) {
		try {
			if (isXmlDatasetTag(localName)) {
				forDS = true;
			}

			getWriter().writeStartElement(localName);

			for (int i = 0; i < atts.getLength(); i++) {
				getWriter().writeAttribute(atts.getQName(i), atts.getValue(i));
			}
		} catch (XMLStreamException e) {
			throw new SAXError(e);
		}
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) {
		try {
			getWriter().writeCharacters(ch, start, length);
		} catch (XMLStreamException e) {
			throw new SAXError(e);
		}
	}

	@Override
	public void endElement(final String uri, final String localName, final String name) {
		try {
			getWriter().writeEndElement();

			if (isXmlDatasetTag(localName)) {
				forDS = false;
			}
		} catch (XMLStreamException e) {
			throw new SAXError(e);
		}
	}
}
