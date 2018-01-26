package ru.curs.showcase.util.xml;

/**
 * Ошибка, соответвутсвующая отсутствию тега <main_context> внутри тега
 * <action>, при наличии тегов <datapanel> или <server>.
 * 
 * @author s.borodanev
 * 
 */

public class XMLHandlingException extends RuntimeException {

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES =
		"Ошибка при разборе XML данных: "
				+ "отсутствует тег <main_context> внутри тега <action>, при наличии тегов <datapanel> или <server>";

	private static final long serialVersionUID = 7758790066616491234L;

	public XMLHandlingException(final Throwable cause) {
		super(ERROR_MES, cause);
	}

}
