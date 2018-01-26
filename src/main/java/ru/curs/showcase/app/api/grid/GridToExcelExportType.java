package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Типы экспорта в Excel.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum GridToExcelExportType implements SerializableElement {
	/**
	 * Только текущую страницу.
	 */
	CURRENTPAGE,
	/**
	 * Весь грид.
	 */
	ALL
}
