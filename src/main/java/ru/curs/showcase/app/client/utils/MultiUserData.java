package ru.curs.showcase.app.client.utils;

import java.util.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Поддержка работы с несколькими userdata в клиентской части.
 * 
 */
public final class MultiUserData {
	/**
	 * SOLUTIONS.
	 */
	private static final String SOLUTIONS = "solutions";

	private MultiUserData() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Возвращает подкорректированный путь с учетом текущей userdata. Например,
	 * "html/welcome.jsp" --> "solutions/default/html/welcome.jsp"
	 * 
	 * @param path
	 *            исходный путь
	 * 
	 * @return - путь, с учетом текущей userdata
	 */
	public static String getPathWithUserData(final String path) {

		String userdataId =
			com.google.gwt.user.client.Window.Location
					.getParameter(ExchangeConstants.URL_PARAM_PERSPECTIVE);

		if ((userdataId == null) || ("".equals(userdataId))) {
			userdataId =
				com.google.gwt.user.client.Window.Location
						.getParameter(ExchangeConstants.URL_PARAM_USERDATA);
		}

		if ((userdataId == null) || ("".equals(userdataId))) {
			userdataId = ExchangeConstants.DEFAULT_USERDATA;
		}

		return SOLUTIONS + "/" + userdataId + "/" + path;
	}

	/**
	 * Формирует текущий контекст из URL. Необходима для вызова ряда функции
	 * GWT-RPC: getNavigator, getServerCurrentState...
	 */
	public static CompositeContext getCurrentContextFromURL() {
		Map<String, List<String>> params =
			com.google.gwt.user.client.Window.Location.getParameterMap();
		CompositeContext context;
		context = new CompositeContext(params);
		return context;
	}

}
