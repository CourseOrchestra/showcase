package ru.curs.showcase.app.server;

/**
 * Возможные действия, обрабатываемые FilesFrontController.
 * 
 * @author den
 * 
 */
public enum FilesFrontControllerAction {
	/**
	 * Экспорт содержимого грида в Excel.
	 */
	GRIDTOEXCEL,
	/**
	 * Скачивание файла из грида.
	 */
	GRIDFILEDOWNLOAD,
	/**
	 * Скачивание файла с сервера.
	 */
	DOWNLOAD,
	/**
	 * Закачивание файла на сервер.
	 */
	UPLOAD,
	/**
	 * Экспорт карты.
	 */
	GEOMAPEXPORT
}
