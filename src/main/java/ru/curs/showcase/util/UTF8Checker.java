package ru.curs.showcase.util;

/**
 * Класс для проверки того, что не английский текст в UTF8 был неправильно
 * закодирован.
 * 
 * @author den
 * 
 */
public class UTF8Checker {
	/**
	 * Значение по умолчанию для порогового числа подозрительных символов в
	 * исследуемой последовательности.
	 */
	private static final int DEF_THR_LEN = 5;

	/**
	 * Число подозрительных символов в исследуемой последовательности, при
	 * достижении которого строка считается неправильно закодированной.
	 */
	private long thresholdLen = DEF_THR_LEN;

	/**
	 * Номер символа, с которого нужно начинать проверку. Используется для того,
	 * чтобы пропустить BOM.
	 */
	private int startFrom = 2;

	/**
	 * Возможные символы, содержащийся в первом байте букв национального
	 * алфавита в UTF8.
	 */
	private char[] signs;

	public UTF8Checker(final char[] aSigns) {
		super();
		signs = aSigns;
	}

	public char[] getSigns() {
		return signs;
	}

	public void setSigns(final char[] aSigns) {
		signs = aSigns;
	}

	public long getThresholdLen() {
		return thresholdLen;
	}

	public void setThresholdLen(final long aThresholdLen) {
		thresholdLen = aThresholdLen;
	}

	public void setSequenceLen(final int aSequenceLen) {
		thresholdLen = aSequenceLen;
	}

	public void setStartFrom(final int aStartFrom) {
		startFrom = aStartFrom;
	}

	/**
	 * Функция проверки.
	 * 
	 * @param str
	 *            - проверяемая строка.
	 * @return - результат проверки.
	 */
	public boolean check(final String str) {
		if (str.length() <= startFrom) {
			return false;
		}

		long founded = 0;
		long posibleStes = Math.round((str.length() - startFrom) / 2.0);
		long curThreshold = Math.min(thresholdLen, posibleStes);
		for (int i = startFrom; i < str.length(); i = i + 2) {
			char testCh = str.charAt(i);

			for (char j : signs) {
				if (testCh == j) {
					founded++;
					break;
				}
			}
			if (founded >= curThreshold) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Определяет - не произошла ли ошибка при установке кодировки строки -
	 * вместо UTF8 установлена другая кодировка и возвращает правильную
	 * кодировку.
	 * 
	 * @param str
	 *            - строка для проверки.
	 * @return - правильная кодировка.
	 */
	public static String getRealEncoding(final String str) {
		char[] chars = { 'Р', 'С' }; // 0xD0 и 0xD1 в CP1251
		if (check(str, chars)) {
			return "CP1251";
		}
		chars = new char[] { 'Ð', 'Ñ' }; // 0xD0 и 0xD1 в ISO-8859-1
		if (check(str, chars)) {
			return "ISO-8859-1";
		}
		return "UTF8";
	}

	/**
	 * Функция проверки для случая, когда набор символов для первого байта
	 * динамический.
	 * 
	 * @param str
	 *            - проверяемая строка.
	 * @return - результат проверки.
	 * 
	 * @param aSigns
	 *            - символы для первого байта.
	 */
	public static boolean check(final String str, final char[] aSigns) {
		UTF8Checker checker = new UTF8Checker(aSigns);
		return checker.check(str);
	}

	public UTF8Checker() {
		super();
	}
}
