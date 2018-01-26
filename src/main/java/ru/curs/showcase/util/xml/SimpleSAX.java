package ru.curs.showcase.util.xml;

import java.io.*;

import javax.xml.parsers.SAXParser;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.util.exception.BaseException;

/**
 * Оболочка около SAX парсера с улучшенной обработкой ошибок.
 * 
 * @author den
 * 
 */
public class SimpleSAX {
	private final InputStream stream;
	private final DefaultHandler saxHandler;
	private final String errorMes;

	public SimpleSAX(final InputStream aStream, final DefaultHandler aSaxHandler,
			final String aErrorMes) {
		super();
		stream = aStream;
		saxHandler = aSaxHandler;
		errorMes = aErrorMes;
	}

	public boolean parse() {
		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(stream, saxHandler);
		} catch (SAXException | IOException | BaseException | XMLError e) {
			stdSAXErrorHandler(e);
			return false;
		}
		return true;
	}

	/**
	 * Стандартный обработчик ошибки в SAX парсере, корректно обрабатывающий
	 * специальный тип SAXError. Если в функцию передано специально прерывающее
	 * SAX парсер исключение - функция позволяет продолжить выполнение
	 * программы.
	 * 
	 */
	private void stdSAXErrorHandler(final Throwable e) {
		Throwable realExc = e;
		if (realExc instanceof XMLError) {
			throw new XMLHandlingException(realExc);
		}
		if (realExc instanceof BreakSAXLoopException) {
			return;
		}
		if (e.getClass() == SAXError.class) {
			realExc = e.getCause();
		}
		if (realExc instanceof BaseException) {
			throw (BaseException) realExc;
		}
		throw new XMLFormatException(errorMes, realExc);
	}
}
