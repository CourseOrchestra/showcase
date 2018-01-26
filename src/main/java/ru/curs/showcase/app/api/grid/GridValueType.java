package ru.curs.showcase.app.api.grid;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Типы данных для грида.
 * 
 */
public enum GridValueType implements SerializableElement {
	/**
	 * Строковый.
	 */
	STRING,
	/**
	 * Целое число.
	 */
	INT,
	/**
	 * Дробное число.
	 */
	FLOAT,
	/**
	 * Дата.
	 */
	DATE,
	/**
	 * Время.
	 */
	TIME,
	/**
	 * Дата и время.
	 */
	DATETIME,
	/**
	 * Рисунок/картинка/изображение.
	 */
	IMAGE,
	/**
	 * Гиперссылка (а также картинка-гиперссылка). Формат передачи: гиперссылка:
	 * &lt;link href="http://example.com" text="Текст ссылки"
	 * openInNewTab="true"&gt;, картинка-гиперссылка: &lt;link
	 * href="http://example.com" image="http://example.com/logo.png"
	 * [text="опциональный текст для атрибута alt тега img"]&gt; или &lt;link
	 * href="http://example.com" image="${images.in.grid.dir}/logo.png"&gt;, где
	 * ${images.in.grid.dir} указывает на каталог Showcase c картинками для
	 * грида, заданный в файле настроек приложения.
	 */
	LINK,
	/**
	 * Загрузка файла с сервера.
	 */
	DOWNLOAD;

	private static final String STRING_FOR_EXCEL = "String";
	private static final String NUMBER_FOR_EXCEL = "Number";

	/**
	 * Возвращает строковое представление типа для использования в Excel.
	 * 
	 * @return - строку с типом.
	 */
	public String toStringForExcel() {
		if (isNumber()) {
			return NUMBER_FOR_EXCEL;
		} else {
			return STRING_FOR_EXCEL;
		}
	}

	/**
	 * Является ли тип датой и\или временем.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isDate() {
		return (this == DATE) || (this == TIME) || (this == DATETIME);
	}

	/**
	 * Является ли тип числом - целым или дробным.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isNumber() {
		return (this == INT) || (this == FLOAT);
	}

	/**
	 * Является ли тип строковым.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isString() {
		return this == STRING;
	}

	/**
	 * Является ли тип "обобщенным" строковым.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isGeneralizedString() {
		return (this == STRING) || (this == LINK) || (this == DOWNLOAD) || (this == IMAGE);
	}

}
