package ru.curs.showcase.util.exception;

/**
 * Тип Файла настроек приложения.
 * 
 * @author den
 * 
 */
public enum SettingsFileType {
	GENERAL_PROPERTIES("Файл с общими настройками приложения"),
	APP_PROPERTIES("Файл с настройками userdata"),
	GRID_PROPERTIES("Профайл настроек грида"),
	VERSION("Файл версии приложения"),
	DATAPANEL("Файл инф. панели", "datapanelstorage"),
	NAVIGATOR("Файл навигатора", "navigatorstorage"),
	XFORM("Файл шаблона XForm", "xforms"),
	JSFORM("Файл шаблона JSForm", "jsForms"),
	XSLT("Файл XSL трансформации", "xslttransforms"),
	SOLUTION_MESSAGES("Файл с сообщениями решения для пользователя"),
	SCHEMA("Файл XSD схемы"),
	FRAME("Фрейм главной страницы"),
	SQLSCRIPT("Файл SQL скрипта"),
	GEOMAP_PROPERTIES("Профайл настроек карты"),
	JYTHON("Jython скрипт"),
	XM_DATA("XML-файл с данными"),
	DATA("Файл с данными"),
	GEOMAP_KEYS("Файл с ключами API для карт"),
	IMPORT_LIST("Список импортируемых компонент для плагина"),
	PLUGIN_ADAPTER("Адаптер для плагина"),
	LIBRARY_ADAPTER("Файл библиотеки"),
	GENERAL_APP_PROPERTIES("Файл с общими настройками userdata"),
	SQL("SQL скрипт");

	/**
	 * Название файла.
	 */
	private String name;

	/**
	 * Фиксированное имя каталога для файлов данного типа. Задается только для
	 * тех типов, для которых оно формируется нестандартным образом.
	 */
	private String dir = null;

	SettingsFileType(final String aName) {
		name = aName;
	}

	SettingsFileType(final String aName, final String aDir) {
		name = aName;
		dir = aDir;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	public String getFileDir() {
		if (dir != null) {
			return dir;
		}
		return this.name().toLowerCase().replace("_", "");
	}

}
