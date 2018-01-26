package ru.curs.showcase.util.xml;

import ru.curs.showcase.util.ObjectSerializer;

/**
 * Сериализатор объекта в XML для передачи в вебконсоль.
 * 
 * @author den
 * 
 */
public class XMLObjectSerializer implements ObjectSerializer {

	@Override
	public String serialize(final Object aObj) {
		if (aObj instanceof String) {
			return (String) aObj;
		} else {
			return XMLUtils.documentToString(XMLUtils.objectToXML(aObj));
		}
	}

}
