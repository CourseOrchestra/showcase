package ru.curs.showcase.util.xml;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае, когда вместо XML файла на сервер приходят
 * данные не в XML формате.
 * 
 * @author den
 * 
 */
public class NotXMLException extends BaseException {

	private static final String ERROR_MES = "Файл '%s' не является файлом в формате XML";

	private static final long serialVersionUID = -7588997404313831091L;

	/**
	 * Имя файла.
	 */
	private final String fileName;

	public String getFileName() {
		return fileName;
	}

	public NotXMLException(final SAXException e, final String aFileName) {
		super(ExceptionType.USER, String.format(ERROR_MES, aFileName), e);
		fileName = aFileName;
	}
}
