package ru.curs.showcase.app.api.plugin;

import java.util.*;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.StringSize;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.*;

/**
 * Класс компоненты плагина.
 * 
 * @author bogatov
 * 
 */
public class PluginComponentImpl implements PluginComponent {
	private static final String MESSAGE_RENDERTO_ISEMPTY =
		"Не задан DOM идентификатор родительского элемента.";
	private static final String MESSAGE_RENDERTOELEMENT_ISEMPTY =
		"Не найден DOM родительского элемента.";
	private final CompositeContext context;
	private final DataPanelElementInfo elInfo;
	private final PluginParam param;
	private final DataServiceAsync dataService = GWT.create(DataService.class);
	private final List<DrawPluginCompleteHandler> drawPluginCompleteHandlerList =
		new LinkedList<DrawPluginCompleteHandler>();

	public PluginComponentImpl(final CompositeContext oContext, final DataPanelElementInfo oElInfo,
			final PluginParam oParam) {
		super();
		this.context = oContext;
		this.elInfo = oElInfo;
		this.param = oParam;
	}

	public CompositeContext getContext() {
		return context;
	}

	public DataPanelElementInfo getElInfo() {
		return this.elInfo;
	}

	@Override
	public PluginParam getParam() {
		return param;
	}

	public String getRenderTo() {
		return this.param.parentId();
	}

	@Override
	public void draw() {
		final String renderToId = getRenderTo();
		if (renderToId == null || renderToId.isEmpty()) {
			throw new JavaScriptException(MESSAGE_RENDERTO_ISEMPTY);
		}
		final Element renderToEl = DOM.getElementById(renderToId);
		if (renderToEl == null) {
			throw new JavaScriptException(MESSAGE_RENDERTOELEMENT_ISEMPTY);
		}

		final PluginParam pluginParam = getParam();
		final String elementPanelId = PluginComponent.PLUGININFO_ID_PREF + renderToId;
		PluginInfo pluginElInfo =
			(PluginInfo) this.elInfo.getTab().getElementInfoById(elementPanelId);
		if (pluginElInfo == null) {
			pluginElInfo =
				new PluginInfo(elementPanelId, pluginParam.plugin(), pluginParam.proc());
			String postProcessProc = pluginParam.postProcessProc();
			if (postProcessProc != null && !postProcessProc.isEmpty()) {
				pluginElInfo.addPostProcessProc(PluginComponent.ELEMENTPROC_ID_PREF + renderToId,
						postProcessProc);
			}
			pluginElInfo.setGetDataProcName(pluginParam.getDataProcName());

			// this.elInfo.getTab().add(pluginElInfo);

		}

		final Element waitElement = addWaitBlock(renderToId);

		RequestData requestData = new RequestData();

		JavaScriptObject addparams = pluginParam.generalFilters();
		if (!(addparams == null)) {
			String xml = getXMLByXPathArray2(addparams);
			getContext().setFilter(xml);
		}

		requestData.setContext(getContext());
		requestData.setElInfo(pluginElInfo);
		if (param.params() != null) {
			JSONObject json = new JSONObject(param.params());
			requestData.setXmlParams(JSONUtils.createXmlByJSONValue("params", json));
		}

		dataService.getPlugin(requestData,
				new GWTServiceCallback<Plugin>(
						// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_data_retrieving_from_server"))
						// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving external plugin data from server")) {

					@Override
					public void onFailure(final Throwable caught) {
						removeWaitBlock(waitElement);
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(final Plugin oPlugin) {
						if (oPlugin != null) {

							super.onSuccess(oPlugin);

							final String pluginTargetId;
							StringSize size = oPlugin.getStringSize();
							if (size != null && !size.getAutoSize()) {
								pluginTargetId = HTMLPanel.createUniqueId();
								SimplePanel cellholder = new SimplePanel();
								cellholder.getElement().setId(pluginTargetId);
								if (size.getWidth() != null) {
									cellholder.setWidth(size.getWidth());
								}
								if (size.getHeight() != null) {
									cellholder.setHeight(size.getHeight());
								}
								renderToEl.appendChild(cellholder.getElement());
							} else {
								pluginTargetId = renderToId;
							}

							StringBuilder sb = new StringBuilder();
							List<String> paramList = oPlugin.getParams();
							for (int i = 0; i < paramList.size(); i++) {
								String itemParam = paramList.get(i);
								if (i != 0) {
									sb.append(",");
								}
								sb.append(itemParam.trim());
							}
							final String params = sb.toString();

							for (String css : oPlugin.getRequiredCSS()) {
								AccessToDomModel.addCSSLink(css);
							}
							JsInjector.getInstance().addScriptTag(oPlugin.getRequiredJS(),
									new JsInjector.CallbackLoadResource() {
										@Override
										public void onComplete() {
											removeWaitBlock(waitElement);
											JSONObject options =
												new JSONObject(pluginParam.params());
											options.put("all", new JSONObject(pluginParam));
											options.put("elementPanelId", new JSONString(
													getElInfo().getId().getString()));
											options.put("generalFilters",
													new JSONString(getXMLByXPathArray(
															pluginParam.generalFilters())));
											drawPlugin(oPlugin.getCreateProc(), pluginTargetId,
													options.getJavaScriptObject(), params);
										}

									});

						} else {
							removeWaitBlock(waitElement);
						}
					}
				});
	}

	@Override
	public void addDrawPluginCompleteHandler(final DrawPluginCompleteHandler handler) {
		drawPluginCompleteHandlerList.add(handler);
	}

	private Element addWaitBlock(final String renderToId) {
		Element waitElement = null;
		Element element = DOM.getElementById(renderToId);
		if (element != null) {
			VerticalPanel generalPluginPanel = new VerticalPanel();
			// generalPluginPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalPluginPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
			waitElement = generalPluginPanel.getElement();
			element.appendChild(waitElement);
		}
		return waitElement;
	}

	private void removeWaitBlock(final Element waitElement) {
		if (waitElement != null) {
			waitElement.removeFromParent();
		}
	}

	// CHECKSTYLE:OFF
	public native void drawPlugin(final String procName, final String parentId,
			final JavaScriptObject pluginParams, final String dynamicParams) /*-{
		var this_ = this;
		pluginParams.onDrawPluginComplete = $entry(function(val) {
			this_.@ru.curs.showcase.app.api.plugin.PluginComponentImpl::onDrawPluginCompleteHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(val);
		});
		$wnd[procName](parentId, pluginParams, dynamicParams);
	}-*/;

	/**
	 * Получить xml по XPath.
	 * 
	 * @param xpathArray
	 *            массив XPath
	 * @return xml
	 */
	private native String getXMLByXPathArray(Object xpathArray) /*-{
		if (xpathArray == undefined) {
			return "";
		}
		return $wnd.getXMLByXPathArray(xpathArray, false);
	}-*/;

	private static native String getXMLByXPathArray2(final Object xpathArray) /*-{
		return $wnd.getXMLByXPathArray(xpathArray);
	}-*/;

	private void onDrawPluginCompleteHandler(final JavaScriptObject o) {
		for (DrawPluginCompleteHandler handler : drawPluginCompleteHandlerList) {
			handler.onDrawComplete(o);
		}
	}

}
