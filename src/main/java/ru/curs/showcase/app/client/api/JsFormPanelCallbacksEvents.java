package ru.curs.showcase.app.client.api;

import java.util.List;

import com.google.gwt.core.client.*;
import com.google.gwt.json.client.*;

import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.*;

/**
 * Класс реализующий функции обратного вызова из JsForm.
 * 
 * @author bogatov
 *
 */
public final class JsFormPanelCallbacksEvents {

	private JsFormPanelCallbacksEvents() {

	}

	/**
	 * asynchronous submit the data.
	 * 
	 * @param elementId
	 *            - ID of JsForm element
	 * @param procId
	 *            - ID of proc of JsForm element
	 * @param data
	 *            - input date
	 * @param onsuccessfn
	 *            - A callback for asynchronous call that can result in success.
	 * @param onfailurefn
	 *            - A callback for asynchronous call that can result in failure.
	 * @return result data
	 */
	public static void jsFormSubmitAsynch(final String elementId, final String procId,
			final String data, final JavaScriptObject onsuccessfn,
			final JavaScriptObject onfailurefn) {
		JsFormPanel jsFormPanel = (JsFormPanel) ActionExecuter.getElementPanelById(elementId);
		JSONObject params = new JSONObject();
		params.put("procId", new JSONString(procId));
		params.put("data", new JSONString(data));
		jsFormPanel.executeHttpRequests("submit", params, false,
				new Callback<XHRRequestSuccessData, Object>() {

					@Override
					public void onSuccess(final XHRRequestSuccessData successData) {
						String data = null;
						if (successData != null) {
							data = successData.getData();
						}
						invokeCallbackFn(onsuccessfn, data);
					}

					@Override
					public void onFailure(final Object err) {
						invokeCallbackFn(onfailurefn, err);
					}
				});
	}

	/**
	 * synchronous submit the data.
	 * 
	 * @param elementId
	 *            - ID of JsForm element
	 * @param procId
	 *            - ID of proc of JsForm element
	 * @param data
	 *            - input date
	 * @return result data
	 */
	public static Object jsFormSubmitSynch(final String elementId, final String procId,
			final String data) {
		JsFormPanel jsFormPanel = (JsFormPanel) ActionExecuter.getElementPanelById(elementId);
		JSONObject params = new JSONObject();
		params.put("procId", new JSONString(procId));
		params.put("data", new JSONString(data));
		final CallbackResult result = new CallbackResult();
		jsFormPanel.executeHttpRequests("submit", params, true,
				new Callback<XHRRequestSuccessData, Object>() {

					@Override
					public void onSuccess(final XHRRequestSuccessData data) {
						if (data != null) {
							result.setResult(data.getData());
						}
					}

					@Override
					public void onFailure(final Object err) {
						result.setResult(err);
					}
				});
		return result.getResult();
	}

	/**
	 * Run action.
	 * 
	 * @param elementId
	 *            - ID of JsForm element
	 * @param linkId
	 *            - ID of action of JsForm element
	 * @return result data
	 */
	public static void jsFormAction(final String elementId, final String linkId) {
		JsForm jsForm = ((JsFormPanel) ActionExecuter.getElementPanelById(elementId)).getElement();
		if (jsForm != null) {
			HTMLEventManager eventManager = jsForm.getEventManager();
			List<HTMLEvent> events = eventManager.getEventForLink(linkId);
			for (HTMLEvent event : events) {
				AppCurrContext.getInstance().setCurrentActionFromElement(event.getAction(),
						jsForm);
				ActionExecuter.execAction();
			}
		}
	}

	/**
	 * Close current modal window.
	 */
	public static void jsFormCloseModalWindow() {
		WindowWithDataPanelElement windowWithDataPanelEl =
			AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement();
		if (windowWithDataPanelEl != null) {
			windowWithDataPanelEl.closeWindow();
		}
	}

	/**
	 * Возвращает текущую JsFormPanel.
	 * 
	 * @param jsFormId
	 *            - Id элемента jsForm.
	 * @return JsFormPanel
	 */
	public static JsFormPanel getCurrentPanel(final String jsFormId) {
		return (JsFormPanel) ActionExecuter.getElementPanelById(jsFormId);
	}

	// CHECKSTYLE:OFF
	private static final native void invokeCallbackFn(JavaScriptObject callbackFn,
			Object data)/*-{
		callbackFn(data);
	}-*/;

	private static class CallbackResult {
		private Object result;

		public Object getResult() {
			return result;
		}

		public void setResult(final Object obj) {
			this.result = obj;
		}

	}

	private static final class XHRRequestSuccessData extends JavaScriptObject {

		protected XHRRequestSuccessData() {
		}

		native String getData() /*-{
			return this.data;
		}-*/;

	}
}
