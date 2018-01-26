package ru.curs.showcase.app.client.utils;

import java.util.List;

import com.google.gwt.core.client.*;

/**
 * Добавляет javascript в DOM HTML документа.
 * 
 * @author bogatov
 * 
 */
public final class JsInjector {
	/**
	 * Интервейс обратного вызова.
	 * 
	 */
	public interface CallbackLoadResource {
		/**
		 * Вызывается при завершение асинхронного вызова.
		 */
		void onComplete();
	}

	/**
	 * Счетчик.
	 * 
	 */
	private class Counter {
		private int val;

		public Counter(final int iVal) {
			super();
			this.val = iVal;
		}
	}

	private static JsInjector instance = new JsInjector();

	private JsInjector() {
	}

	public static JsInjector getInstance() {
		return instance;
	}

	/**
	 * Динамически вставляет в страницу ссылуку нa javascript-file, формируя тег
	 * script с атрибутом src.
	 * 
	 * @param links
	 *            - список адресов ссылок на javascript
	 * @param links
	 *            callback - вызывается после завершения загрузки всех
	 *            javascript
	 */
	public void addScriptTag(final List<String> links, final CallbackLoadResource callback) {
		final Counter counter = new Counter(links.size());
		for (String js : links) {
			if (!AccessToDomModel.isAddedScriptLink(js)) {
				ScriptInjector.fromUrl(js).setWindow(getWindow())
						.setCallback(new Callback<Void, Exception>() {
							@Override
							public void onFailure(final Exception reason) {
								onComplete();
							}

							@Override
							public void onSuccess(final Void result) {
								onComplete();
							}

							private void onComplete() {
								if (counter.val == 1) {
									callback.onComplete();
								} else {
									counter.val--;
								}
							}
						}).inject();
			} else {
				counter.val--;
			}
		}
		if (counter.val == 0) {
			callback.onComplete();
		}
	};

	/**
	 * @return - возвращает Window
	 */
	private static native JavaScriptObject getWindow() /*-{
		return $wnd;
	}-*/;
}
