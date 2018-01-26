/**
 * 
 */
package ru.curs.showcase.app.client.utils;

/**
 * 
 * Набор функций для работы со сторкой в которой заданы размер в пикселях или
 * процентах и ее анализом.
 * 
 * @author anlug
 * 
 */
public final class SizeParser {

	private SizeParser() {

	}

	/**
	 * Процедура, возвращающая абсолютный (число) размер в процентах или
	 * пикселях из строки.
	 * 
	 * @param s
	 *            - строка.
	 * @return - размер в Integer.
	 */
	public static Integer getSize(final String s) {
		String temp;
		switch (getSizeType(s)) {

		case PIXELS:
			int firstInsexOfPx = s.indexOf("px");
			temp = String.copyValueOf(s.toCharArray(), 0, firstInsexOfPx);
			return Integer.valueOf(temp);

		case PERCENTS:
			int firstInsexOfPercents = s.indexOf("%");
			temp = String.copyValueOf(s.toCharArray(), 0, firstInsexOfPercents);
			return Integer.valueOf(temp);

		default:
			return -1;

		}

	}

	/**
	 * Процедура, получающая тип (проценты или пикселы) значения, обозначающего
	 * размер.
	 * 
	 * @param s
	 *            - строка.
	 * @return - WidthType
	 */
	public static SizeType getSizeType(final String s) {

		if (s.endsWith("px")) {
			return SizeType.PIXELS;
		} else if (s.endsWith("%")) {
			return SizeType.PERCENTS;
		} else {
			return SizeType.ERROR_OF_TYPE_RETRIEVING;
		}

	}

}
