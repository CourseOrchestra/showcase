package ru.curs.showcase.app.client;

import java.util.List;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.api.BasicElementPanelBasis;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.WebUtils;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.json.client.*;
import com.google.gwt.regexp.shared.*;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели JsForm.
 * 
 * @author bogatov
 *
 */
public class JsFormPanel extends BasicElementPanelBasis {

	private SerializationStreamFactory ssf = null;
	private JsForm jsForm = null;
	private final VerticalPanel rootPanel = new VerticalPanel();
	private final HTML rootEl = new HTML();
	private final RegExp jsRegExp = RegExp.compile(
			"<script.*?(?:src\\s*=\\s*[\"'](.*?)[\"'])?>([\\s\\S]*?)<\\/script>", "ig");
	private static final String DESERIALIZATION_ERROR = CourseClientLocalization.gettext(
			AppCurrContext.getInstance().getDomain(),
			"An error occurred while deserializing an object");

	public JsFormPanel(final CompositeContext context, final DataPanelElementInfo elementInfo) {
		this.setContext(context);
		this.setElementInfo(elementInfo);
		this.getPanel().addStyleName("jsform-element");
		this.getPanel().addStyleName("id-" + elementInfo.getId().getString());
		init(true);
	}

	public JsFormPanel(final DataPanelElementInfo elementInfo) {
		this.setContext(null);
		this.setElementInfo(elementInfo);
		this.getPanel().addStyleName("jsform-element");
		this.getPanel().addStyleName("id-" + elementInfo.getId().getString());
		init(false);
	}

	private void init(final boolean isLoad) {
		setCallbackJSNIFunction();
		rootPanel.add(rootEl);
		if (isLoad) {
			refreshPanel();
		}
	}

	@Override
	public void reDrawPanel(final CompositeContext context) {
		setContext(context);
		this.refreshPanel();
	}

	@Override
	public void refreshPanel() {
		setJsFormPanel();
	}

	@Override
	public void hidePanel() {
		rootPanel.setVisible(false);
	}

	@Override
	public void showPanel() {
		rootPanel.setVisible(true);
	}

	@Override
	public VerticalPanel getPanel() {
		return this.rootPanel;
	}

	@Override
	public JsForm getElement() {
		return this.jsForm;
	}

	private void setJsFormPanel() {
		final DataPanelElementInfo elInfo = getElementInfo();
		if (elInfo.getShowLoadingMessage()) {
			rootEl.setHTML("<div class=\"progress-bar\"></div>");
		}
		executeHttpRequests("template", null, false,
				new Callback<XHRRequestSuccessData, XHRRequestErrData>() {

					@Override
					public void onSuccess(final XHRRequestSuccessData data) {
						JsForm result = new JsForm(data.getTemplate(), elInfo);
						String deserializeEvents = data.getEvents();
						if (deserializeEvents != null && !deserializeEvents.isEmpty()) {
							try {
								@SuppressWarnings("unchecked")
								List<HTMLEvent> events =
									(List<HTMLEvent>) getObjectSerializer().createStreamReader(
											deserializeEvents).readObject();
								result.getEventManager().getEvents().addAll(events);
							} catch (SerializationException e) {
								MessageBox.showSimpleMessage("Error deserialize of JsForm event",
										DESERIALIZATION_ERROR + " Events: " + e.getMessage());
							}
						}

						setJsFormPanelData(result);

						Scheduler.get().scheduleDeferred(new Command() {
							@Override
							public void execute() {
								for (DataPanelElementInfo el : AppCurrContext.getReadyStateMap()
										.keySet()) {
									if (el.getType() == DataPanelElementType.JSFORM
											&& !AppCurrContext.getReadyStateMap().get(el)) {
										AppCurrContext.getReadyStateMap().put(el, true);
										break;
									}
								}

								if (!AppCurrContext.getReadyStateMap().containsValue(false)) {
									RootPanel.getBodyElement().addClassName("ready");
								}
							}
						});

					}

					@Override
					public void onFailure(final XHRRequestErrData err) {
						if (err.getMessage() != null && !"".equals(err.getMessage())
								&& err.getMessage().contains("<html>")) {
							int index1 =
								err.getMessage().indexOf("<strong>Error message:</strong>");
							int index2 = err.getMessage().indexOf("</body>");
							String errorMessage =
								err.getMessage()
										.substring(
												index1
														+ "<strong>Error message:</strong>"
																.length(), index2).trim();
							MessageBox.showSimpleMessage(CourseClientLocalization.gettext(
									AppCurrContext.getInstance().getDomain(),
									"JSForm construction error"), errorMessage);
						}
						String errMsg = "";
						if (err != null) {
							errMsg =
								"<h1>Error " + err.getStatus()
										+ "</h1><div>Received data:<br/><pre>"
										+ SafeHtmlUtils.htmlEscape(err.getData()) + "<pre></div>";
						}
						setJsFormPanelData(new JsForm(errMsg, elInfo));
					}
				});
	}

	private void setJsFormPanelData(final JsForm jsFormPanelData) {
		this.jsForm = jsFormPanelData;
		if (jsForm != null && jsForm.getTemplate() != null) {
			String html = jsRegExp.replace(jsForm.getTemplate(), "");
			rootEl.setHTML(html);
			// detect and inject javascript
			MatchResult m;
			while ((m = jsRegExp.exec(jsForm.getTemplate())) != null) {
				int indexOpenComment = -1;
				int indexCloseComment = -1;
				int indexOpenScriptTag = -1;
				int indexCloseScriptTag = -1;
				do {
					indexOpenComment = jsForm.getTemplate().indexOf("<!--");
					indexCloseComment = jsForm.getTemplate().indexOf("-->", indexOpenComment);
					indexOpenScriptTag = jsForm.getTemplate().indexOf("<script", indexOpenComment);
					indexCloseScriptTag =
						jsForm.getTemplate().indexOf("</script>", indexOpenScriptTag);
					if (indexOpenComment != -1 && indexCloseComment != -1
							&& indexOpenComment < indexOpenScriptTag
							&& indexOpenScriptTag < indexCloseScriptTag
							&& indexCloseScriptTag < indexCloseComment)
						jsForm.setTemplate(jsForm.getTemplate().substring(0, indexOpenComment)
								+ jsForm.getTemplate().substring(
										indexCloseComment + "-->".length(),
										jsForm.getTemplate().length()));
				} while (indexOpenComment != -1 && indexCloseComment != -1
						&& indexOpenComment < indexOpenScriptTag
						&& indexOpenScriptTag < indexCloseScriptTag
						&& indexCloseScriptTag < indexCloseComment);

				/*
				 * ScriptInjector.fromString(m.getGroup(1)).setRemoveTag( false)
				 * .setWindow(ScriptInjector.TOP_WINDOW).inject();
				 */
				ScriptElement script = Document.get().createScriptElement();
				script.setType("text/javascript");
				if (m.getGroup(1) != null) {
					script.setSrc(m.getGroup(1));
				}
				if (m.getGroup(2) != null) {
					script.setText(m.getGroup(2));
				}
				rootEl.getElement().appendChild(script);
			}
		} else {
			rootEl.setHTML("");
		}
	}

	/**
	 * Возвращает "сериализатор" для gwt объектов.
	 * 
	 * @return - SerializationStreamFactory.
	 */
	private SerializationStreamFactory getObjectSerializer() {
		if (ssf == null) {
			ssf = WebUtils.createStdGWTSerializer();
		}
		return ssf;
	}

	public void executeHttpRequests(final String action, final JSONObject params,
			final boolean sync, final Callback<?, ?> callback) {
		final CompositeContext contex = getContext();
		final DataPanelElementInfo elInfo = getElementInfo();
		JSONObject paramObj;
		if (params == null) {
			paramObj = new JSONObject();
		} else {
			paramObj = params;
		}
		paramObj.put("action", new JSONString(action));
		try {
			SerializationStreamFactory serializationFactory = getObjectSerializer();
			String serializeContext = contex.toParamForHttpPost(serializationFactory);
			if (serializeContext != null) {
				paramObj.put("contex", new JSONString(serializeContext));
			}
			String serializeElInfo = elInfo.toParamForHttpPost(serializationFactory);
			if (serializeElInfo != null) {
				paramObj.put("elementInfo", new JSONString(serializeElInfo));
			}
		} catch (SerializationException e) {
			paramObj.put(
					"error",
					new JSONString(CourseClientLocalization
							.gettext(AppCurrContext.getInstance().getDomain(),
									"Error serialization context or element info of JsForm.")));
		}

		RequestParam param = JsonUtils.safeEval(paramObj.toString());
		doHttpRequests(param, sync, callback);
	}

	/**
	 * Выполнение HTTP запроса.
	 * 
	 * @param sync
	 *            - если true то выполняется синхронный запрос (блокировка
	 *            дальнейшего выполнения и ожидание ответа), иначе асинхронный
	 *            запрос.
	 * @param param
	 *            - передаваемые параметры (main_context, add_context и т.д.)
	 * @param callback
	 *            - функции возврата
	 */
	// CHECKSTYLE:OFF
	private static native void doHttpRequests(final RequestParam param, final boolean sync,
			final Callback<?, ?> callback)/*-{
			$wnd.require([ 'dojo/request/xhr' ], function(xhr) {
				var xhrArgs = {
					data : param,
					sync : sync,
					handleAs: "json"
				};
				var deferred = xhr.post('secured/JsFormService', xhrArgs).then(
				function(data) {
					callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(data);
				}, function(err) {
					callback.@com.google.gwt.core.client.Callback::onFailure(Ljava/lang/Object;)({
//						message : err.message,
						message : err.response ? err.response.text : err.message,
						status : err.response ? err.response.status : '',
						data : err.response ? err.response.data : '',
						isError : true
					});
				});
			});
	}-*/;

	/**
	 * Установка процедур обратного вызова.
	 */
	// CHECKSTYLE:OFF
	private static native void setCallbackJSNIFunction() /*-{
		$wnd.jsFormSubmitAsynch = @ru.curs.showcase.app.client.api.JsFormPanelCallbacksEvents::jsFormSubmitAsynch(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)
		$wnd.jsFormSubmitSynch = @ru.curs.showcase.app.client.api.JsFormPanelCallbacksEvents::jsFormSubmitSynch(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
		$wnd.jsFormAction = @ru.curs.showcase.app.client.api.JsFormPanelCallbacksEvents::jsFormAction(Ljava/lang/String;Ljava/lang/String;);
		$wnd.jsFormCloseModalWindow = @ru.curs.showcase.app.client.api.JsFormPanelCallbacksEvents::jsFormCloseModalWindow();
	}-*/;

	private static final class RequestParam extends JavaScriptObject {

		protected RequestParam() {
		}

		native String getСontex() /*-{
			return this.contex;
		}-*/;

		native String getElementInfo() /*-{
			return this.elementInfo;
		}-*/;

		native String getData() /*-{
			return this.data;
		}-*/;

	}

	public static final class XHRRequestErrData extends JavaScriptObject {

		protected XHRRequestErrData() {
		}

		native String getMessage() /*-{
			return this.message;
		}-*/;

		native String getStatus() /*-{
			return this.status;
		}-*/;

		native String getData() /*-{
			return this.data;
		}-*/;

		native String getIsError() /*-{
			return this.isError;
		}-*/;
	}

	private static final class XHRRequestSuccessData extends JavaScriptObject {

		protected XHRRequestSuccessData() {
		}

		native String getTemplate() /*-{
			return this.template;
		}-*/;

		native String getEvents() /*-{
			return this.events;
		}-*/;
	}

}
