package ru.curs.showcase.app.client.utils;

import java.util.List;

import com.google.gwt.core.client.GWT;

import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

/**
 * Общие утилиты для XForms. В том числе, генерация главной XForm'ы.
 */
public final class XFormsUtils {

	private static DataServiceAsync dataService = null;

	private XFormsUtils() {
		throw new UnsupportedOperationException();
	}

	public static void initXForms() {

		setCallbackJSNIFunction();

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getMainXForms(new GWTServiceCallback<List<String>>(
				// AppCurrContext.getInstance().getBundleMap().get("xformsErrorGetMainData"))
				// {
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"when retrieving data from server to the main XForm")) {
			@Override
			public void onSuccess(final List<String> mainXForm) {
				// destroy();

				if (mainXForm.size() > 0) {
					addMainXFormBody(mainXForm.get(0));
				}

				if ((mainXForm.size() > 1) && (!mainXForm.get(1).trim().isEmpty())) {
					addMainXFormCSS(mainXForm.get(1));
				}

				for (int i = 2; i < mainXForm.size(); i++) {
					addMainXFormScript(mainXForm.get(i));
				}

				initMainXForm();
			}
		});
	}

	private static native void initMainXForm() /*-{
		$wnd.xsltforms_init();
	}-*/;

	private static native void destroy() /*-{
		//Подчищаем динамические скрипты
		var div = $doc.getElementById('target');
		while (div.childNodes.length > 0)
			div.removeChild(div.firstChild);

		//Подчищаем динамические стили
		var hdr = $doc.getElementsByTagName('head')[0];
		var ss1 = $doc.getElementById('target_style');
		if (ss1 != null)
			hdr.removeChild(ss1);
	}-*/;

	/**
	 * Установка процедур обратного вызова.
	 */
	// CHECKSTYLE:OFF
	private static native void setCallbackJSNIFunction() /*-{
															$wnd.gwtXFormSave = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::xFormPanelClickSave(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;);
															$wnd.gwtXFormUpdate = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::xFormPanelClickUpdate(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;);
															
															//$wnd.showSelector =	@ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::showSelector(Lcom/google/gwt/core/client/JavaScriptObject;);
															//$wnd.showMultiSelector = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::showMultiSelector(Lcom/google/gwt/core/client/JavaScriptObject;);
															
															$wnd.gwtXFormDownload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::downloadFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtXFormUpload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::uploadFile(Lcom/google/gwt/core/client/JavaScriptObject;);
															$wnd.gwtXFormSimpleUpload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::simpleUpload(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtXFormOnSubmitComplete = @ru.curs.showcase.app.client.utils.InlineUploader::onSubmitComplete(Ljava/lang/String;);
															$wnd.gwtXFormOnChooseFiles = @ru.curs.showcase.app.client.utils.InlineUploader::onChooseFiles(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/String;);
															$wnd.gwtCreatePlugin =	@ru.curs.showcase.app.client.api.PluginPanelCallbacksEvents::createPlugin(Lcom/google/gwt/core/client/JavaScriptObject;);
															$wnd.gwtGetDataPlugin = @ru.curs.showcase.app.client.api.PluginPanelCallbacksEvents::pluginGetData(Lcom/google/gwt/core/client/JavaScriptObject;);
															$wnd.gwtXFormGetStringContext = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::getStringContext(Ljava/lang/String;);
															$wnd.gwtXFormShowMessage = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::showMessage(Ljava/lang/String;);
															$wnd.gwtXFormShowErrorMessage = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::showErrorMessage(Ljava/lang/String;);
															$wnd.gwtSelectorGetHttpParams = @ru.curs.showcase.app.client.JSSelector::pluginGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtTreeSelectorGetHttpParams = @ru.curs.showcase.app.client.JSSelector::pluginTreeGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtSelectorGetLocalizedParams = @ru.curs.showcase.app.client.JSSelector::pluginGetLocalizedParams(Ljava/lang/String;);
															$wnd.gwtSelectorShowMessage = @ru.curs.showcase.app.client.JSSelector::pluginShowMessage(Ljava/lang/String;);
															$wnd.gwtSelectorShowErrorMessage = @ru.curs.showcase.app.client.JSSelector::pluginShowErrorMessage(Ljava/lang/String;);
															$wnd.gwtSelectorShowTextMessage = @ru.curs.showcase.app.client.JSSelector::pluginShowTextMessage(Ljava/lang/String;);															
															}-*/;

	// CHECKSTYLE:ON

	/**
	 * Динамически вставляет в страницу CSS для главной XForm'ы.
	 * 
	 * @param cssdef
	 *            - текст CSS.
	 */
	private static native void addMainXFormCSS(final String cssdef) /*-{
		var ss1 = $doc.createElement('style');
		ss1.setAttribute('type', 'text/css');
		ss1.setAttribute('id', 'target_style');
		if (ss1.styleSheet) { // IE
			ss1.styleSheet.cssText = cssdef;
		} else { // the world
			var tt1 = $doc.createTextNode(cssdef);
			ss1.appendChild(tt1);
		}
		var hh1 = $doc.getElementsByTagName('head')[0];
		hh1.appendChild(ss1);
	}-*/;

	/**
	 * Динамически вставляет в страницу скрипт, используемый главной XForm'ой.
	 * 
	 * @param code
	 *            Javascript-код, который необходимо вставить
	 */
	private static native void addMainXFormScript(final String code) /*-{
		var newscript = $doc.createElement('script');
		newscript.text = code;
		newscript.type = "text/javascript";
		var div = $doc.getElementById('target');
		div.appendChild(newscript);
	}-*/;

	/**
	 * Динамически вставляет в страницу содержимое главной XForm'ы.
	 * 
	 * @param mainXForm
	 *            содержимое главной XForm'ы
	 */
	private static native void addMainXFormBody(final String mainXForm) /*-{
		var div = $doc.getElementById('mainXForm');
		div.innerHTML = mainXForm;

		$wnd.safeIncludeJS("js/ui/selectors/selector.js");
		$wnd.safeIncludeJS("js/ui/selectors/multiSelector.js");
		$wnd.safeIncludeJS("js/ui/selectors/treeSelector.js");
	}-*/;

}