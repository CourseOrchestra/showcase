package ru.curs.showcase.util.xml;

import javax.xml.transform.Source;

import org.xml.sax.SAXException;



/**
 * Преобразует обобщенный XMLSource в подходящий источник для выполнения
 * валидации.
 * 
 * @author den
 * 
 */
public interface XMLExtractor {

	/**
	 * Подготавливает источник для XSD валидатора.
	 * 
	 * @param aSource
	 *            - обобщенный :) источник.
	 * @return - источник для валидатора.
	 * @throws SAXException
	 */
	Source extract(XMLSource aSource) throws SAXException;
}
