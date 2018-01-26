package ru.curs.showcase.util.xml;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

/**
 * Преобразует DOM Document в источник для валидации.
 * 
 * @author den
 * 
 */
public final class DocumentXMLExtractor implements XMLExtractor {

	@Override
	public Source extract(final XMLSource source) {
		return new DOMSource(source.getDocument());
	}

}
