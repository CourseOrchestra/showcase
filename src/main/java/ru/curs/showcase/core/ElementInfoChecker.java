package ru.curs.showcase.core;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.util.ObjectSerializer;
import ru.curs.showcase.util.xml.XMLObjectSerializer;

/**
 * Класс для проверки элементов информационной панели.
 * 
 * @author den
 * 
 */
public class ElementInfoChecker {

	private static final String NO_ELEMENT_INFO_ERROR =
		"Не передано описание элемента в шлюз к данным";

	/**
	 * Функция проверки переданных в шлюз данных.
	 * 
	 * @param element
	 *            - информация о загружаемом элементе.
	 */
	public void check(final DataPanelElementInfo element,
			final DataPanelElementType expectedElementType) {
		if (element == null) {
			throw new IncorrectElementException(NO_ELEMENT_INFO_ERROR);
		}
		if ((element.getType() != expectedElementType) || !element.isCorrect()) {
			ObjectSerializer serializer = new XMLObjectSerializer();
			throw new IncorrectElementException("Некорректное описание элемента: "
					+ serializer.serialize(element));
		}
		if (element.wrongRelated()) {
			throw new IncorrectElementException(
					"Для элемента информационной панели c id=\""
							+ element.getId().toString()
							+ "\", находящегося на вкладке c id=\""
							+ element.getTab().getId()
							+ "\", задано свойство related, "
							+ "ссылающееся на элемент, для которого установлен атрибут neverShowInPanel=\"true\", "
							+ "что не позволит отобразиться элементу c id=\""
							+ element.getId().toString() + "\".");
		}
	}

}
