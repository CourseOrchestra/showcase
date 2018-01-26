package ru.curs.showcase.util;

import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Класс, содержащий общие утилиты для работы с приложением.
 * 
 * @author anlug
 * 
 */
public final class GeneralUtils {

	/**
	 * Возвращает абсолютный путь к месту, где в сервере приложений или в
	 * контейнете сервлетов находится дириктория с приложением Showcase (путь к
	 * варнику)
	 */

	public static String getWebAppPath() {

		return AppInfoSingleton.getAppInfo().getWebAppPath();
	}

}
