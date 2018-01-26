package ru.curs.showcase.util.xml;

/**
 * Исключение, возникающее при отсутствии тега <main_context> внутри тега
 * <action>, при наличии тегов <datapanel> или <server>.
 * 
 * @author s.borodanev
 * 
 */

public class NoMainContextException extends RuntimeException {

	private static final long serialVersionUID = 87089400663341234L;

	public NoMainContextException() {
		super();
	}

}
