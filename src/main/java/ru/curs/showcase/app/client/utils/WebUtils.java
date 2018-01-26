package ru.curs.showcase.app.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.ui.Frame;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.MessageBox;

/**
 * Различные web-утилиты.
 */
public final class WebUtils {

	private WebUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Frame downloadFrame.
	 */
	private static Frame downloadFrame;

	/**
	 * Инициирует загрузку файла в браузере.
	 * 
	 * @param url
	 *            адрес загружаемого файла
	 */
	public static void downloadFile(final String url) {
		downloadFrame.setUrl(url);
		// Window.open(url, "_blank", "");
	}

	/**
	 * Устанавливает IFRAME, используемый для скачивания файлов.
	 * 
	 * @param frame
	 *            IFRAME
	 */
	public static void setDownloadFrame(final Frame frame) {
		downloadFrame = frame;
	}

	/**
	 * Убирает текущий IFRAME для скачивания файлов, если frame является текущим
	 * IFRAME'ом.
	 * 
	 * @param frame
	 *            IFRAME
	 */
	public static void clearDownloadFrame(final Frame frame) {
		if (downloadFrame == frame) {
			downloadFrame = null;
		}
	}

	/**
	 * Открывает веб-страницу в новом окне.
	 * 
	 * @param url
	 *            адрес открываемой страницы
	 */
	public static void openURL(final String url) {
		openURL("url", true);
	}

	/**
	 * Открывает веб-страницу.
	 * 
	 * @param url
	 *            адрес открываемой страницы
	 * @param blank
	 *            true - открывает url в новом окне, false - в текущем
	 */
	public static void openURL(final String url, final boolean blank) {
		if (blank) {
			Window.open(url, "_blank", "");
		} else {
			Window.Location.assign(url);
		}
	}

	/**
	 * Устанавливает значение name=value в хэше URL. Сейчас поддерживается
	 * установка только одного значения и полной перезаписью хэша.
	 * 
	 * @param name
	 *            имя
	 * @param value
	 *            значение
	 */
	public static void setHashValue(final String name, final String value) {
		String href = Window.Location.getHref();
		int i = href.indexOf('#');
		if (i != -1) {
			href = href.substring(0, i);
		}
		Window.Location.replace(href + "#" + name + "=" + value);
	}

	/**
	 * Возвращает значение в хэше для имени name. Сейчас поддерживается только
	 * наличие одного значения в хэше.
	 * 
	 * @param name
	 *            имя
	 * @return значение или null, если значения для name нет
	 */
	public static String getHashValue(final String name) {
		String hash = Window.Location.getHash();
		if (hash == null || hash.trim().isEmpty()) {
			return null;
		}

		if (hash.startsWith("#")) {
			hash = hash.substring(1);
		}

		String[] pp = hash.split("=");
		if (pp.length > 1 && pp[0].equals(name)) {
			return pp[1];
		}

		return null;
	}

	/**
	 * Создает стандартную фабрику GWT для ручной сериализации объектов.
	 * 
	 * @return - фабрику.
	 */
	public static SerializationStreamFactory createStdGWTSerializer() {
		return (SerializationStreamFactory) GWT.create(DataService.class);
	}

	public static SerializationStreamFactory createAddGWTSerializer() {
		return (SerializationStreamFactory) GWT.create(FakeService.class);
	}

	/**
	 * Показывает сообщение об ошибке.
	 * 
	 * @param caught
	 *            исключение
	 * @param msgErrorCaption
	 *            заголовок сообщения по умолчанию
	 */
	public static void onFailure(final Throwable caught, final String msgErrorCaption) {
		if (caught.getMessage().contains(ExchangeConstants.SESSION_NOT_AUTH_SIGN)) {
			Window.Location.assign(AccessToDomModel.getAppContextPath() + "/sestimeout.jsp");
		} else {

			if ((GeneralException.getOriginalExceptionClass(caught) != null) && GeneralException
					.getOriginalExceptionClass(caught).contains("ValidateException")) {

				String textMessage = caught.getMessage();
				if ((textMessage == null) || textMessage.isEmpty()) {
					textMessage = "";
				}

				MessageType typeMessage = GeneralException.getMessageType(caught);
				if (typeMessage == null) {
					typeMessage = MessageType.ERROR;
				}

				String captionMessage = GeneralException.getMessageCaption(caught);
				if (captionMessage == null) {
					captionMessage = msgErrorCaption;
				}

				String subtypeMessage = GeneralException.getMessageSubtype(caught);

				MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage,
						false, subtypeMessage);

			} else {
				String str = GeneralException.getMessageType(caught).getName();
				if (GeneralException.getMessageType(caught) == MessageType.ERROR) {
					str = msgErrorCaption;
				}

				if (GeneralException.generateDetailedInfo(caught)
						.contains("com.google.gwt.user.client.rpc.StatusCodeException")) {
					MessageBox.showMessageWithDetails("Нет связи с сервером",
							"Проверьте наличие связи с сервером или обратитесь к администратору вашей сети",
							GeneralException.generateDetailedInfo(caught), MessageType.ERROR, true,
							null);
				} else {
					MessageBox.showMessageWithDetails(str, caught.getMessage(),
							GeneralException.generateDetailedInfo(caught),
							GeneralException.getMessageType(caught),
							GeneralException.needDetailedInfo(caught), null);
				}
			}

		}

	}

}
