package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Возможные типы элементов информационной панели.
 * 
 * @author den
 * 
 */
public enum DataPanelElementType implements SerializableElement {
	/**
	 * Грид.
	 */
	GRID,
	/**
	 * График.
	 */
	CHART,
	/**
	 * Текст с активными элементами.
	 */
	WEBTEXT,
	/**
	 * Карта.
	 */
	GEOMAP,
	/**
	 * XForm - форма для редактирования или фильтрации.
	 */
	XFORMS,
	/**
	 * UI плагин - произвольная внешняя компонента JavaScript или Flash.
	 */
	PLUGIN,
	/**
	 * jsForm - форма предоставляющая произвольный набор JavaScript компонент
	 * (dojo) и/или html.
	 */
	JSFORM;

	/**
	 * Возвращает имя схемы для проверки общих настроек элемента.
	 */
	public String getSettingsSchemaName() {
		return name().toLowerCase() + "Settings.xsd";
	}

	/**
	 * Возвращает имя схемы для проверки настроек отдельных групп значения
	 * элемента (строк или столбцов таблицы).
	 */
	public String getPropsSchemaName() {
		return name().toLowerCase() + "Properties.xsd";
	}
}
