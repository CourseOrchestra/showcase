package ru.curs.showcase.util.xml;

/**
 * Специальный класс ошибки для передачи наверх исключения об отсутствии тега
 * <main_context> внутри тега <action>, при наличии тегов <datapanel> или
 * <server>.
 * 
 * @author s.borodanev
 * 
 */

public class XMLError extends RuntimeException {

	private static final long serialVersionUID = 5024218668352683986L;

	public XMLError(Exception e) {
		super(e);
	}

}
