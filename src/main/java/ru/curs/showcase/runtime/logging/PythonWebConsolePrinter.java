package ru.curs.showcase.runtime.logging;

import org.slf4j.*;

public class PythonWebConsolePrinter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PythonWebConsolePrinter.class);

	/**
	 * Функция, которая вызываетя из celesta- и python-скриптов для вывода
	 * информации как в обычную, так и в веб-консоль.
	 * 
	 * @param stringToPrint
	 *            - выводимая строка
	 */
	public static void printString(final String stringToPrint) {
		String str = stringToPrint;
		LOGGER.info(str);
	}
}
