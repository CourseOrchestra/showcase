package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Исключение, возникающее при конвертации SVG изображения.
 * 
 * @author den
 * 
 */
public class SVGConvertException extends BaseException {

	private static final long serialVersionUID = 1814821444847656582L;
	private static final String ERROR_MES = "При конвертации SVG изображения произошла ошибка";

	public SVGConvertException(final Throwable aCause) {
		super(ExceptionType.JAVA, ERROR_MES, aCause);
	}

}
