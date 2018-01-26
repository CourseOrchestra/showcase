package ru.curs.showcase.core.celesta;

/**
 * Набор функций при работе с celesta.
 * 
 * @author bogatov
 * 
 */
public final class CelestaUtils {

	private CelestaUtils() {

	}

	/**
	 * Получить имя функции без постфикса.
	 * 
	 * @param sProcName
	 *            имя функции celesta
	 * @return
	 */
	public static String getRealProcName(final String sProcName) {
		String procName = sProcName;
		if (procName != null) {
			procName = procName.substring(0, procName.lastIndexOf("."));
		}
		return procName;
	}
}
