package ru.curs.showcase.app.client.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.plugin.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.WebUtils;

/**
 * Загрузчик данных для плагина.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginHelper {
	/**
	 * Интерфейс, посредством которого происходит оповещает о завершении
	 * процесса получение данных.
	 */
	public interface PluginListener {
		/**
		 * Вызывается компонентой при закрытии как по кнопке ОК, так и по кнопке
		 * Отменить.
		 * 
		 * @param selector
		 *            Компонента-селектор.
		 */
		void onComplete(ResponceData responce);
	}

	private final DataServiceAsync getDataService = GWT.create(DataService.class);
	private RequestData requestData;
	private PluginListener listener;

	public GetDataPluginHelper() {

	}

	public GetDataPluginHelper(final RequestData oRequestData, final PluginListener oListener) {
		this.requestData = oRequestData;
		this.listener = oListener;
	}

	public RequestData setRequestData(final RequestData oRequestData) {
		return this.requestData = oRequestData;
	}

	public PluginListener setListener(final PluginListener oListener) {
		return this.listener = oListener;
	}

	public void getData() {
		getDataService.getPluginData(requestData, new AsyncCallback<ResponceData>() {

			@Override
			public void onFailure(final Throwable caught) {
				if (listener != null) {
					listener.onComplete(null);
				}

				WebUtils.onFailure(caught, "Error");
			}

			@Override
			public void onSuccess(final ResponceData responce) {
				if (listener != null) {
					listener.onComplete(responce);
				}

				if (responce.getOkMessage() != null) {
					showMessage(responce.getOkMessage());
				}
			}
		});
	}

	private void showMessage(final UserMessage um) {

		String textMessage = um.getText();
		if ((textMessage == null) || textMessage.isEmpty()) {
			return;
		}

		MessageType typeMessage = um.getType();
		if (typeMessage == null) {
			typeMessage = MessageType.INFO;
		}

		String captionMessage = um.getCaption();
		if (captionMessage == null) {
			captionMessage =
				// AppCurrContext.getInstance().getBundleMap().get("okMessage");
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Message");
		}

		String subtypeMessage = um.getSubtype();

		MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage, false,
				subtypeMessage);

	}

}
