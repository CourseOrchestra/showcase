package ru.curs.showcase.util;

import java.io.*;

/**
 * Абстрактное действие с файлом.
 * 
 * @author den
 * 
 */
public interface FileAction {
	/**
	 * Выполнить действие над файлом.
	 * 
	 * @param file
	 *            - файл над которым нужно провести действие.
	 * @throws IOException
	 */
	void perform(File file) throws IOException;

	/**
	 * Создает новое действие для работы с вложенной папкой по отношению к
	 * исходной папке действия.
	 * 
	 * @param aChildDir
	 *            - имя вложенной папки.
	 * @return - новое действие.
	 */
	FileAction cloneForHandleChildDir(String aChildDir);
}