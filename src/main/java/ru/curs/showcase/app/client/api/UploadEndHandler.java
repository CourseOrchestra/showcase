package ru.curs.showcase.app.client.api;

/**
 * Интерфейс события при закрытии окна выбора файла.
 * 
 * @author den
 * 
 */
public interface UploadEndHandler {

	/**
	 * Обработчик события закрытия окна.
	 * 
	 * @param aRes
	 *            - был ли выбран файл.
	 * @param aFileName
	 *            - имя файла.
	 */
	void onEnd(boolean aRes, String aFileName);

}
