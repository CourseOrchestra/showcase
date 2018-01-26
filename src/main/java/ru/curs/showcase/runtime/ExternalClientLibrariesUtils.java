package ru.curs.showcase.runtime;

import java.util.*;

/**
 * Класс для подключения к клиентской части и работы с внешними клиентскими
 * библиотеками (jquery и т.п.).
 * 
 */
public final class ExternalClientLibrariesUtils {

	private ExternalClientLibrariesUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Функция, которая генерит текст для главного файла index.jsp по вставке в
	 * дом модель сторонних файлов, библиотек (js-файлов).
	 * 
	 * 
	 * @return текст для главного файла index.jsp по вставке в дом модель
	 *         сторонних файлов.
	 */
	public static String addExternalLinksByStaticMetod(final String userdataId) {
		String result = "";
		String beginScritpTag = "<script src='";
		String endScritpTag = "'></script>";
		String pathPrefix = "js";
		String auserdataId = userdataId;

		if ((auserdataId == null) || (auserdataId.isEmpty())) {
			auserdataId = "default";
		}

		List<String> strList =
			UserDataUtils.getImportStaticExternalJsLibrariesByUserdataId(auserdataId);

		ArrayList<String> listOfLinks = new ArrayList<String>(strList);

		for (int i = 0; i < listOfLinks.size(); i++) {

			result += beginScritpTag + pathPrefix + listOfLinks.get(i) + endScritpTag;

		}

		return result;
	}

	/**
	 * Функция, которая генерит текст для главного файла index.jsp по вставке в
	 * дом модель сторонних файлов-стилей (css-файлов).
	 * 
	 * 
	 * @return текст для главного файла index.jsp по вставке в дом модель
	 *         сторонних файлов - стилей.
	 */
	public static String addExternalCSSByStaticMetod(final String userdataId) {
		String result = "";
		String beginScritpTag = "<link rel='stylesheet' href='";
		String endScritpTag = "'/>";
		String pathPrefix = "css";
		String auserdataId = userdataId;

		if ((auserdataId == null) || (auserdataId.isEmpty())) {
			auserdataId = "default";
		}

		List<String> strList = UserDataUtils.getImportStaticExternalCssByUserdataId(auserdataId);

		ArrayList<String> listOfLinks = new ArrayList<String>(strList);

		for (int i = 0; i < listOfLinks.size(); i++) {

			result += beginScritpTag + pathPrefix + listOfLinks.get(i) + endScritpTag;

		}

		return result;
	}
}
