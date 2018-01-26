package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.AccessToDomModel;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с внешним плагином.
 */
public class PluginPanel extends BasicElementPanelBasis {

	private static final String PLEASE_WAIT_DATA_ARE_LOADING = "please_wait_data_are_loading";

	private static final String ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER =
	// "error_of_plugin_data_retrieving_from_server";
		"when retrieving external plugin data from server";

	public PluginPanel(final CompositeContext context1, final DataPanelElementInfo element1) {
		this.setContext(context1);
		this.setElementInfo(element1);

		setCollbackJSNIFunction();

		generalPluginPanel = new VerticalPanel();
		generalHp = new HorizontalPanel();

		this.getPanel().addStyleName("plugin-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			// generalPluginPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get(PLEASE_WAIT_DATA_ARE_LOADING)));
			generalPluginPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		} else {
			generalPluginPanel.add(new HTML(""));
		}

		// generalPluginPanel.add(new HTML(AppCurrContext.getInstance()
		// .getInternationalizedMessages().please_wait_data_are_loading()));

		dataService = GWT.create(DataService.class);

		setPluginPanel();

	}

	public PluginPanel(final DataPanelElementInfo element1) {

		// я бы убрал этот код-начало
		this.setElementInfo(element1);
		generalHp = new HorizontalPanel();
		this.setContext(null);

		// я бы убрал этот код-конец
		// createChildPanels();

		setCollbackJSNIFunction();

		generalPluginPanel = new VerticalPanel();

		this.getPanel().addStyleName("plugin-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			// generalPluginPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get(PLEASE_WAIT_DATA_ARE_LOADING)));
			generalPluginPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		} else {
			generalPluginPanel.add(new HTML(""));
		}

		// generalPluginPanel.add(new HTML(AppCurrContext.getInstance()
		// .getInternationalizedMessages().please_wait_data_are_loading()));

	}

	private void setPluginPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}
		RequestData requestData = new RequestData();
		requestData.setContext(getContext());
		requestData.setElInfo((PluginInfo) getElementInfo());

		dataService.getPlugin(
				requestData,
				new GWTServiceCallback<Plugin>(
				// AppCurrContext.getInstance().getBundleMap().get(ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER)) {

					@Override
					public void onSuccess(final Plugin aPlugin) {

						plugin = aPlugin;
						if (plugin != null) {

							super.onSuccess(plugin);

							fillPluginPanel(aPlugin);
						}

						Scheduler.get().scheduleDeferred(new Command() {
							@Override
							public void execute() {
								for (DataPanelElementInfo el : AppCurrContext.getReadyStateMap()
										.keySet()) {
									if (el.getType() == DataPanelElementType.PLUGIN
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
				});

	}

	/**
	 * Заполняет виджет плагина содержимым. TODO: сделать установку ширины и
	 * высоты div всегда
	 * 
	 * @param Plugin
	 *            plugin
	 */
	protected void fillPluginPanel(final Plugin aPlugin) {

		if (!isPartialUpdate()) {

			final String div = "<div id='";
			final String htmlForPlugin =
				div + getDivIdPlugin() + "' style='width:" + aPlugin.getStringSize().getWidth()
						+ "; height:" + aPlugin.getStringSize().getHeight() + ";'></div>";

			// MessageBox.showSimpleMessage("", htmlForPlugin);

			pluginHTML = new HTML(htmlForPlugin);

			generalPluginPanel.clear();
			generalHp.clear();

			generalPluginPanel.add(generalHp);
			generalHp.add(pluginHTML);

			generalPluginPanel.setHeight("100%");
			generalHp.setHeight("100%");
			pluginHTML.setHeight("100%");

			generalHp.setWidth("100%");

			if (AppCurrContext.getInstance()
					.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
					.indexOf(getDivIdPlugin()) < 0) {
				AppCurrContext.getInstance()
						.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
						.add(getDivIdPlugin());
				for (String param : aPlugin.getRequiredCSS()) {
					AccessToDomModel.addCSSLink(param);
				}
				for (String param : aPlugin.getRequiredJS()) {
					AccessToDomModel.addScriptLink(param);
				}
			}

		}

		String params = "'" + getDivIdPlugin() + "'";
		for (String param : aPlugin.getParams()) {
			if (aPlugin.getCreateProc().toLowerCase().contains("toolbar")) {
				param = param.replace("parentId", getDivIdPlugin());
			}

			params = params + ", " + param.trim();
		}

		if (!isNeedResetLocalContext()) {
			params = params + ", keep_user_settings=true";
		}

		try {
			drawPlugin(aPlugin.getCreateProc(), params);
		} catch (JavaScriptException e) {

			if (e.getCause() != null) {
				MessageBox.showMessageWithDetails(
						// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"External plugin constructing error"), e.getMessage(),
						GeneralException.generateDetailedInfo(e.getCause()), GeneralException
								.getMessageType(e.getCause()), GeneralException.needDetailedInfo(e
								.getCause()), null);
			} else {
				MessageBox.showSimpleMessage(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"External plugin constructing error"), e.getMessage());
			}

		}

		checkForDefaultAction();
		setupTimer();

	}

	/**
	 * VerticalPanel на которой отображен плагин.
	 */
	private final VerticalPanel generalPluginPanel;

	/**
	 * HorizontalPanel на которой отображен плагин.
	 */
	private final HorizontalPanel generalHp;

	/**
	 * Plugin plugin.
	 */
	private Plugin plugin = null;

	/**
	 * @return Возвращает текущий объект типа Plugin - данные внешнего плагина.
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Устанавливает текущий объект типа Plugin - данные внешнего плагина.
	 * 
	 * @param aPlugin
	 *            - объект типа Plugin
	 */
	public void setPlugin(final Plugin aPlugin) {
		this.plugin = aPlugin;
	}

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * HTML виждет для плагина.
	 */
	private HTML pluginHTML = null;

	/**
	 * Ф-ция, возвращающая панель с внешним плагином, если она необходима.
	 * 
	 * @return - Панель с плагином.
	 */
	@Override
	public VerticalPanel getPanel() {
		return generalPluginPanel;
	}

	/**
	 * 
	 * Процедура прорисовки плагина.
	 * 
	 * @param procName
	 *            - имя js - процедуры для прорисовки плагина
	 * @param params
	 *            - параметры js - процедуры для прорисовки плагина
	 * 
	 */
	public native void drawPlugin(final String procName, final String params) /*-{
		$wnd.eval(procName + "(" + params + ");");
	}-*/;

	@Override
	public void reDrawPanel(final CompositeContext context1) {

		this.setContext(context1);
		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		if (this.getElementInfo().getShowLoadingMessage()) {
			generalPluginPanel.clear();
			// generalPluginPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get(PLEASE_WAIT_DATA_ARE_LOADING)));
			generalPluginPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		RequestData requestData = new RequestData();
		requestData.setContext(getContext());
		requestData.setElInfo((PluginInfo) getElementInfo());

		dataService.getPlugin(
				requestData,
				new GWTServiceCallback<Plugin>(
				// AppCurrContext.getInstance().getBundleMap().get(ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER)) {

					@Override
					public void onSuccess(final Plugin aPlugin) {

						plugin = aPlugin;
						if (plugin != null) {

							super.onSuccess(plugin);

							fillPluginPanel(aPlugin);
							getPanel().setHeight("100%");

						}

						Scheduler.get().scheduleDeferred(new Command() {
							@Override
							public void execute() {
								for (DataPanelElementInfo el : AppCurrContext.getReadyStateMap()
										.keySet()) {
									if (getElementInfo().getId().getString()
											.equals(el.getId().getString())
											&& !AppCurrContext.getReadyStateMap().get(el)) {
										AppCurrContext.getReadyStateMap().put(el, true);
										break;
									}
								}

								for (DataPanelElementInfo el : AppCurrContext
										.getFromActionElementsMap().keySet()) {
									if (getElementInfo().getId().getString()
											.equals(el.getId().getString())
											&& !AppCurrContext.getFromActionElementsMap().get(el)) {
										AppCurrContext.getFromActionElementsMap().put(el, true);
										break;
									}
								}

								if (!AppCurrContext.getInstance()
										.getPluginXformTrueStateForReadyStateMap()
										&& !AppCurrContext
												.getInstance()
												.getGridWithToolbarPluginTrueStateForReadyStateMap())
									if (!AppCurrContext.getReadyStateMap().containsValue(false)) {
										RootPanel.getBodyElement().addClassName("ready");
									}
							}
						});
					}
				});

	}

	@Override
	public void hidePanel() {
		generalPluginPanel.setVisible(false);

	}

	@Override
	public void showPanel() {
		generalPluginPanel.setVisible(true);

	}

	private void checkForDefaultAction() {
		if (plugin.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(
					plugin.getActionForDependentElements(), plugin);
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return plugin;
	}

	@Override
	public void refreshPanel() {

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			generalPluginPanel.clear();
			// generalPluginPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get(PLEASE_WAIT_DATA_ARE_LOADING)));
			generalPluginPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		RequestData requestData = new RequestData();
		requestData.setContext(getContext());
		requestData.setElInfo((PluginInfo) getElementInfo());

		dataService.getPlugin(
				requestData,
				new GWTServiceCallback<Plugin>(
				// AppCurrContext.getInstance().getBundleMap().get(ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER)) {

					@Override
					public void onSuccess(final Plugin aPlugin) {

						plugin = aPlugin;
						if (plugin != null) {

							super.onSuccess(plugin);

							fillPluginPanel(aPlugin);
							getPanel().setHeight("100%");
						}

					}
				});

	}

	// CHECKSTYLE:OFF
	/**
	 * 
	 * Процедура определяющая функцию, которая будет выполняться по клику в
	 * Plugin.
	 * 
	 */
	public native void setCollbackJSNIFunction() /*-{
													$wnd.gwtPluginFunc = @ru.curs.showcase.app.client.api.PluginPanelCallbacksEvents::pluginPanelClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
													$wnd.gwtGetDataPlugin = @ru.curs.showcase.app.client.api.PluginPanelCallbacksEvents::pluginGetData(Lcom/google/gwt/core/client/JavaScriptObject;);
													}-*/;

	// CHECKSTYLE:ON

	private String getDivIdPlugin() {
		// return getElementInfo().getFullId() + Constants.PLUGIN_DIV_ID_SUFFIX;

		return "dpe_" + getElementInfo().getTab().getDataPanel().getId() + "__E40F6599F809__"
				+ getElementInfo().getId() + Constants.PLUGIN_DIV_ID_SUFFIX;
	}
}
