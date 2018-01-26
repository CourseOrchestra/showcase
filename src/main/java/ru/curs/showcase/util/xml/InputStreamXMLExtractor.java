package ru.curs.showcase.util.xml;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * Преобразует InputStream в источник для валидации.
 * 
 * @author den
 * 
 */
public class InputStreamXMLExtractor implements XMLExtractor {

	@Override
	public Source extract(final XMLSource source) {
		return new StreamSource(source.getInputStream());
	}

}
