package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.WebUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Базовый класс для асинхронных колбэков при вызове сервисов gwt.
 * 
 * @param <T>
 *            тип результата
 */
public abstract class GWTServiceCallback<T> implements AsyncCallback<T> {

	public GWTServiceCallback(final String amsgErrorCaption) {
		super();
		this.msgErrorCaption = amsgErrorCaption;
	}

	/**
	 * Сообщение, которое будет показано в случае, если в процессе вызова
	 * сервиса GWT произошла ошибка.
	 */
	private final String msgErrorCaption;

	@Override
	public void onFailure(final Throwable caught) {

		WebUtils.onFailure(caught, msgErrorCaption);

	}

	@Override
	public void onSuccess(final T dataPanelElement) {
		UserMessage okMessage = null;
		if (dataPanelElement instanceof DataPanelElement) {
			okMessage = ((DataPanelElement) dataPanelElement).getOkMessage();
		}
		if (dataPanelElement instanceof VoidElement) {
			okMessage = ((VoidElement) dataPanelElement).getOkMessage();
		}
		if (okMessage == null) {
			return;
		}

		String textMessage = okMessage.getText();
		if ((textMessage == null) || textMessage.isEmpty()) {
			return;
		}

		MessageType typeMessage = okMessage.getType();
		if (typeMessage == null) {
			typeMessage = MessageType.INFO;
		}

		String captionMessage = okMessage.getCaption();
		if (captionMessage == null) {
			captionMessage =
			// AppCurrContext.getInstance().getBundleMap().get("okMessage");
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Message");
		}

		String subtypeMessage = okMessage.getSubtype();

		MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage, false,
				subtypeMessage);

	}
}
