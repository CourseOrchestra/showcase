package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.plugin.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.JSONUtils;

import com.google.gwt.core.client.*;
import com.google.gwt.json.client.JSONObject;

/**
 * 
 */

/**
 * @author anlug Класс реализующий функции обратного вызова из PluginPanel.
 * 
 */
public final class PluginPanelCallbacksEvents {

	private PluginPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие клика на внешнем плагине (на PluginPanel).
	 * 
	 * @param pluginDivId
	 *            - Id тэга div элемента Plugin.
	 * @param eventId
	 *            - строка-идентификатор события. К примеру, это могут быть
	 *            координаты точки или название экранного элемента, по которому
	 *            кликнул пользователь.
	 */
	public static void pluginPanelClick(final String pluginDivId, final String eventId,
			final String overridenValue, final String replaseWhat, final String id) {// (final
																						// String
																						// pluginDivId,
																						// final
																						// String
																						// eventId)
																						// {

		// final String commonPart = "E40F6599F809__";
		//
		// String elementId =
		// pluginDivId.substring(0,
		// pluginDivId.length() - Constants.PLUGIN_DIV_ID_SUFFIX.length());
		//
		// elementId = elementId.substring(elementId.indexOf(commonPart) +
		// commonPart.length());
		//
		// Plugin pl = ((PluginPanel)
		// ActionExecuter.getElementPanelById(elementId)).getPlugin();
		//
		// List<HTMLEvent> events =
		// pl.getEventManager().getEventForLink(eventId);
		//
		// for (HTMLEvent chev : events) {
		// AppCurrContext.getInstance().setCurrentActionFromElement(chev.getAction(),
		// pl);
		// ActionExecuter.execAction();
		// }

		// ====

		final String commonPart = "E40F6599F809__";

		String elementId =
			pluginDivId.substring(0,
					pluginDivId.length() - Constants.PLUGIN_DIV_ID_SUFFIX.length());

		elementId = elementId.substring(elementId.indexOf(commonPart) + commonPart.length());

		Plugin pl = ((PluginPanel) ActionExecuter.getElementPanelById(elementId)).getPlugin();

		ActionFieldType actionFieldType = ActionFieldType.ADD_CONTEXT;
		if (replaseWhat != null) {
			actionFieldType = ActionFieldType.valueOf(replaseWhat.toUpperCase());
		}

		List<HTMLEvent> events = pl.getEventManager().getEventForLink(eventId);
		for (HTMLEvent chev : events) {

			Action ac = chev.getAction().gwtClone();
			if (overridenValue != null) {
				switch (actionFieldType) {
				case ADD_CONTEXT:
					ac.setAdditionalContext(overridenValue);
					break;
				case MAIN_CONTEXT:
					ac.setMainContext(overridenValue);
					break;
				case FILTER_CONTEXT:
					ac.filterBy(overridenValue);
					break;
				case ELEMENT_ID:
					if (ac.getDataPanelActionType() != DataPanelActionType.DO_NOTHING) {
						String elID = id;
						DataPanelElementLink link = null;
						if (elID != null) {
							link = ac.getDataPanelLink().getElementLinkById(elID);
						} else {
							if (ac.getDataPanelLink().getElementLinks().size() > 0) {
								link = ac.getDataPanelLink().getElementLinks().get(0);
							}
						}
						if (link != null) {
							link.setId(overridenValue);
						} else {
							MessageBox.showSimpleMessage("Ошибка",
									"Элемент действия для замены ID неверно определен (ID = "
											+ elID + ")");
						}
					}
					break;
				default:
					break;
				}
			}

			AppCurrContext.getInstance().setCurrentActionFromElement(ac, pl);
			ActionExecuter.execAction();
		}

		// ====

	}

	/**
	 * Статический метод для создания элемента плагина. Может быть использован
	 * непосредственно в javscript-е на форме.
	 * 
	 * @param oParams
	 *            Объект-хендлер плагина, @see {@link PluginParam)
	 */
	public static void createPlugin(final JavaScriptObject oParams) {
		final PluginParam param = (PluginParam) oParams;
		BasicElementPanel currentPanel = ActionExecuter.getElementPanelById(param.id());
		if (currentPanel != null) {
			PluginComponent pluginComponent =
				new PluginComponentImpl(currentPanel.getContext(), currentPanel.getElementInfo(),
						param);
			if (param.parentId() == null || param.parentId().isEmpty()) {
				// плагин будет отображен во всплывающем окне
				pluginComponent = new WindowPluginDecorator(pluginComponent);
			}
			try {
				pluginComponent.draw();
			} catch (JavaScriptException e) {
				if (e.getCause() != null) {
					MessageBox.showMessageWithDetails(
							// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_painting"),
							CourseClientLocalization.gettext(AppCurrContext.getInstance()
									.getDomain(), "External plugin constructing error"), e
									.getMessage(), GeneralException.generateDetailedInfo(e
									.getCause()), GeneralException.getMessageType(e.getCause()),
							GeneralException.needDetailedInfo(e.getCause()), null);
				} else {
					MessageBox.showSimpleMessage(
					// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_painting"),
							CourseClientLocalization.gettext(AppCurrContext.getInstance()
									.getDomain(), "External plugin constructing error"), e
									.getMessage());
				}
			}
		}
	}

	/**
	 * Входные параметры для getData.
	 * 
	 * @author bogatov
	 * 
	 */
	private static final class GetDataPluginParam extends JavaScriptObject {

		protected GetDataPluginParam() {
		};

		/**
		 * Id элемента панели.
		 * 
		 * @return String
		 */
		native String id()/*-{
			return this.id;
		}-*/;

		/**
		 * Id элемента панели.
		 * 
		 * @return String
		 */
		native String parentId()/*-{
			return this.parentId;
		}-*/;

		/**
		 * Дополнительные параметры передаваемые в процедуру получения данных.
		 * 
		 * @return String
		 */
		native JavaScriptObject params()/*-{
			return this.params;
		}-*/;

		/**
		 * callback функция возврата данных.
		 * 
		 * @param datas
		 *            массив данных
		 */
		native void callbackFn(final JavaScriptObject datas)/*-{
			if (this.callbackFn != null) {
				this.callbackFn(datas);
			}
		}-*/;

		/**
		 * Базовые параметры передаваемые при построение плагина.
		 * 
		 * @return String
		 */
		native PluginParam pluginParam()/*-{
			return this.pluginParam;
		}-*/;
	}

	/**
	 * Получить данные для плагина.
	 * 
	 * @param oParams
	 *            параметры, @see
	 *            {@link PluginPanelCallbacksEvents.GetDataPluginParam)
	 */
	public static void pluginGetData(final JavaScriptObject oParams) {
		if (oParams != null) {
			final GetDataPluginParam param = (GetDataPluginParam) oParams;
			BasicElementPanel currentPanel = ActionExecuter.getElementPanelById(param.id());
			if (currentPanel != null) {
				RequestData requestData = new RequestData();
				requestData.setContext(currentPanel.getContext());

				/*
				 * PluginInfo pluginInfo = (PluginInfo) currentPanel
				 * .getElementInfo() .getTab() .getElementInfoById(
				 * PluginComponent.PLUGININFO_ID_PREF + param.parentId()); if
				 * (pluginInfo == null) {
				 * MessageBox.showSimpleMessage(AppCurrContext.getInstance()
				 * .getInternationalizedMessages().error_of_plugin_getdata(),
				 * "Не найден PluginInfo"); return; }
				 */
				final String elementPanelId =
					PluginComponent.PLUGININFO_ID_PREF + param.parentId();
				PluginInfo pluginInfo =
					new PluginInfo(elementPanelId, param.pluginParam().plugin(), param
							.pluginParam().proc());
				String postProcessProc = param.pluginParam().postProcessProc();
				if (postProcessProc != null && !postProcessProc.isEmpty()) {
					pluginInfo.addPostProcessProc(
							PluginComponent.ELEMENTPROC_ID_PREF + param.parentId(),
							postProcessProc);
				}
				pluginInfo.setGetDataProcName(param.pluginParam().getDataProcName());

				requestData.setElInfo(pluginInfo);
				if (param.params() != null) {
					JSONObject json = new JSONObject(param.params());
					requestData.setXmlParams(JSONUtils.createXmlByJSONValue("params", json));
				}
				try {
					GetDataPluginHelper helper =
						new GetDataPluginHelper(requestData,
								new GetDataPluginHelper.PluginListener() {

									@Override
									public void onComplete(final ResponceData responce) {
										JavaScriptObject result = null;
										if (responce != null) {
											result = eval(responce.getJsonData());
										}
										param.callbackFn(result);
									}
								});
					helper.getData();
				} catch (JavaScriptException e) {
					if (e.getCause() != null) {
						MessageBox.showMessageWithDetails(
								// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_getdata"),
								CourseClientLocalization.gettext(AppCurrContext.getInstance()
										.getDomain(),
										"Error when retrieving data for external plugin"), e
										.getMessage(), GeneralException.generateDetailedInfo(e
										.getCause()),
								GeneralException.getMessageType(e.getCause()), GeneralException
										.needDetailedInfo(e.getCause()), null);
					} else {
						MessageBox.showSimpleMessage(
						// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_getdata"),
								CourseClientLocalization.gettext(AppCurrContext.getInstance()
										.getDomain(),
										"Error when retrieving data for external plugin"), e
										.getMessage());
					}
				}
			} else {
				MessageBox.showSimpleMessage(
						// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_getdata"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Error when retrieving data for external plugin"),
						"Не найден ElementPanel. Id=" + param.id());
			}
		}
	}

	/**
	 * Преобразовать строку в JavaScriptObject.
	 * 
	 * @param jsonStr
	 *            строка в формате json
	 * @return JavaScriptObject
	 */
	public static native JavaScriptObject eval(final String jsonStr) /*-{
		return $wnd.eval('(' + jsonStr + ')');
	}-*/;
}
