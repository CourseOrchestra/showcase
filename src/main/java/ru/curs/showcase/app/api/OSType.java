package ru.curs.showcase.app.api;

import com.google.gwt.regexp.shared.*;

/**
 * Класс определяет тип операционной системы на основе данные из User-Agent http
 * заголовка.
 * 
 * @author bogatov
 * 
 */
public enum OSType {
	/**
	 * Not defined.
	 */
	UNDEFINED("Not defined"),
	/**
	 * Microsoft Internet Explorer.
	 */
	WINDOWS("Microsoft Window"),
	/**
	 * Apple Mac.
	 */
	MAC("Apple Mac"),
	/**
	 * Apple Mac.
	 */
	UNIX("Unix"),
	/**
	 * Apple Mac.
	 */
	ANDROID("Google Android"),
	/**
	 * Apple Mac.
	 */
	IPHONE("Apple IPhone");
	/**
	 * Имя браузера.
	 */
	private String name;

	OSType(final String aName) {
		name = aName;
	}

	public String getName() {
		return name;
	}

	/**
	 * Определяет тип ОС по UserAgent.
	 * 
	 * @param userAgent
	 *            - userAgent.
	 * @return - тип ОС.
	 */
	public static OSType detect(final String userAgent) {
		if (findCI(userAgent, "windows")) {
			return WINDOWS;
		} else if (findCI(userAgent, "mac")) {
			return MAC;
		} else if (findCI(userAgent, "x11")) {
			return UNIX;
		} else if (findCI(userAgent, "android")) {
			return ANDROID;
		} else if (findCI(userAgent, "iphone")) {
			return IPHONE;
		} else {
			return UNDEFINED;
		}
	}

	/**
	 * Определяет версию OS по строке userAgent.
	 * 
	 * @param userAgent
	 *            - userAgent.
	 * @return - версия.
	 */
	public static String detectVersion(final String userAgent) {
		OSType osType = detect(userAgent);
		switch (osType) {
		case WINDOWS:
			return findVersion(userAgent, "windows (.*?);");
		case MAC:
			return findVersion(userAgent, "mac (.*?);");
		case UNIX:
			return findVersion(userAgent, "x11 (.*?);");
		case ANDROID:
			return findVersion(userAgent, "android (.*?);");
		case IPHONE:
			return findVersion(userAgent, "iphone (.*?);");
		default:
			return UNDEFINED.getName();
		}

	}

	private static boolean findCI(final String source, final String regex) {
		RegExp pattern = RegExp.compile(".*" + regex + ".*", "i");
		return pattern.test(source);
	}

	private static String findVersion(final String source, final String regex) {
		RegExp pattern = RegExp.compile(regex, "i");
		MatchResult res = pattern.exec(source);
		if (res != null) {
			if (res.getGroupCount() == 2) {
				return res.getGroup(1);
			}
		}
		return null;
	}
}
