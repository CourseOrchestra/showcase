package ru.curs.showcase.app.client.api;

/**
 * Интерфейс обработчика окончания загрузки файлов на сервер.
 * 
 * @author den
 * 
 */
public interface CompleteHandler {
	/**
	 * Обработчик окончания загрузки файлов на сервер.
	 * 
	 * @param aRes
	 *            - был ли выбран файл.
	 */
	void onComplete(boolean aRes);
}
