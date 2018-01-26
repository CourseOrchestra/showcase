package ru.curs.showcase.util.xml;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.*;

/**
 * Преобразует данные для SAX валидации в требуемый формат.
 * 
 * @author den
 * 
 */
public final class SAXExtractor implements XMLExtractor {

	@Override
	public Source extract(final XMLSource source) throws SAXException {
		return new SAXSource(source.getSaxParser().getXMLReader(), new InputSource(
				source.getInputStream()));
	}

}
