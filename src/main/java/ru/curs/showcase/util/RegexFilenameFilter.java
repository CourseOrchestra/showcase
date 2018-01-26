package ru.curs.showcase.util;

import java.io.File;

/**
 * Фильтр regexp для поиска файлов.
 * 
 * @author den
 * 
 */
public class RegexFilenameFilter implements java.io.FilenameFilter {
	/**
	 * Шаблон для фильтра.
	 */
	private java.util.regex.Pattern pattern;

	/**
	 * Режим работы фильтра - на включение или на исключение.
	 */
	private final boolean include;

	public RegexFilenameFilter(final String aPattern, final boolean aInclude) {
		setPattern(aPattern);
		include = aInclude;
	}

	public final void setPattern(final String aPattern) {
		pattern = java.util.regex.Pattern.compile(aPattern);
	}

	@Override
	public boolean accept(final File dir, final String fileName) {
		final boolean res = pattern.matcher(fileName).matches();
		if (include) {
			return res;
		} else {
			return !res;
		}
	}
}