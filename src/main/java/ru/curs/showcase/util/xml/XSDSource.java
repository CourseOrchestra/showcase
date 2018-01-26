package ru.curs.showcase.util.xml;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Источник XSD схем.
 * 
 * @author den
 * 
 */
public interface XSDSource {

	/**
	 * Возвращает файл схемы.
	 * 
	 * @param sourceName
	 *            - имя источника.
	 * @return - файл.
	 * @throws SAXException
	 */
	Schema getSchema(String sourceName) throws SAXException;

	/**
	 * Возвращает тип исключения при проверке схемы из данного источника.
	 * 
	 * @return - тип.
	 */
	ExceptionType getExceptionType();
}
