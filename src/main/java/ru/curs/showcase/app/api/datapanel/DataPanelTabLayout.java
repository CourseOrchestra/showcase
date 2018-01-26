package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Тип раскладки элементов на вкладке инф. панели.
 * 
 * @author den
 * 
 */
public enum DataPanelTabLayout implements SerializableElement {
	/**
	 * При вертикальной раскладке элементы расположены один под другим в порядке
	 * их задания на вкладке. Раскладка по умолчанию.
	 */
	VERTICAL,
	/**
	 * При табличной раскладке для расположения элементов используется аналог
	 * HTML table.
	 */
	TABLE
}
