/**
 * 
 */
package ru.curs.showcase.app.client.utils;

/**
 * Варианты типов в которых задается ширина.
 * 
 * @author anlug
 * 
 */
public enum SizeType {

	/**
	 * Невозможно получить значение в чем указана ширина: в пикселях или
	 * процентах.
	 */
	ERROR_OF_TYPE_RETRIEVING,

	/**
	 * Ширина указана в пикселях.
	 */
	PIXELS,

	/**
	 * Ширина указана в процентах.
	 */
	PERCENTS,

}
