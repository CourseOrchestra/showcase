/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.Date;

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;

import ru.curs.showcase.app.client.utils.AccessToDomModel;

/**
 * @author anlug
 * 
 *         Класс пользовательского интерфейса отвечающий за реализацию logout из
 *         прилождения в пользовательском интерфейсе (процедуру showcaseLogout
 *         нужно вызвать из java script на клиентской стороне c помощью JSNI
 *         механизма).
 * 
 */
public final class ShowcaseLogout {

	private ShowcaseLogout() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Таймаут при выходе из системы.
	 */
	private static final int LOGOUT_TIMEOUT = 5000;

	/**
	 * Функция выхода из приложения (разлогинивания), включая разлогинивание в
	 * AuthServer.
	 */
	public static void showcaseLogout() {

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				"auth/logoutServlet?nocache=" + (new Date()).getTime() + "&sesId="
						+ AppCurrContext.getInstance().getServerCurrentState().getSesId());
		builder.setTimeoutMillis(LOGOUT_TIMEOUT);
		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(final Request request, final Throwable exception) {

					Window.Location.assign(AccessToDomModel.getAppContextPath() + "/logout");
				}

				@Override
				public void onResponseReceived(final Request request, final Response response) {

					if (AppCurrContext.getInstance().getServerCurrentState().getIsESIAUser()) {

						Window.Location.assign(AppCurrContext.getInstance().getServerCurrentState()
								.getEsiaLogoutURL());

					} else {

						Window.Location.assign(AccessToDomModel.getAppContextPath() + "/logout");

					}

				}
			});

		} catch (RequestException e) {
			Window.alert("Failed to send the request: " + e.getMessage());
		}

	}
}
