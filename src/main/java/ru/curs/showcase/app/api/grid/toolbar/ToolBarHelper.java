package ru.curs.showcase.app.api.grid.toolbar;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.DataServiceAsync;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.api.Constants;

/**
 * Помощник загрузки и формирования панели инструментов.
 * 
 */
public class ToolBarHelper {

	// private static final String TOOLBAR_HEIGHT = "31px";
	private static final String TOOLBAR_HEIGHT = "29px";

	private static final String CLASS_NAME = "className";
	private static final String STYLE = "style";

	private Timer toolBarRefreshTimer = null;
	private final DataServiceAsync dataService;
	private final SimplePanel panel;
	private final JSBaseGridPluginPanel jsBaseGridPluginPanel;
	private boolean isStaticToolBar = false;
	private boolean needStaticItems;
	public static boolean booleanWithToolBar = false;
	public static boolean booleanWithToolBar1 = false;
	public static boolean booleanWithoutToolBar = false;
	private boolean alreadyReadyWithToolbar = false;
	private final boolean needAdjustToolBarWidth = true;

	private int blinkingCount = 0;
	private boolean blinkingStartTimer = false;

	private final HashMap<String, Action> actions = new HashMap<String, Action>();

	/**
	 * 
	 * @param oDataService
	 *            - имплементация DataService.
	 * @param oPanel
	 *            - панель на которую помещается ToolBar.
	 */
	public ToolBarHelper(final DataServiceAsync aDataService,
			final JSBaseGridPluginPanel aJSBaseGridPluginPanel) {
		dataService = aDataService;
		jsBaseGridPluginPanel = aJSBaseGridPluginPanel;

		panel = new SimplePanel();

		String toolbarClassName =
			jsBaseGridPluginPanel.getGridMetadata().getUISettings().getToolbarClassName();
		String toolbarStyle =
			jsBaseGridPluginPanel.getGridMetadata().getUISettings().getToolbarStyle();
		if ((toolbarClassName == null) && (toolbarStyle == null)) {
			panel.setHeight(TOOLBAR_HEIGHT);
		} else {
			if (toolbarClassName != null) {
				panel.addStyleName(toolbarClassName);
			}
			if (toolbarStyle != null) {
				panel.getElement().setAttribute(STYLE, toolbarStyle);
			}
		}

	}

	/**
	 * Обновлении панели инструментов. Очистка текущей панели и формирование
	 * панели инструментов на основе полученных метаданных.
	 */
	public void fillToolBar() {
		if ((!panel.getParent().isVisible()) || isStaticToolBar) {
			return;
		}

		String params = "'" + jsBaseGridPluginPanel.getDivIdToolBar() + "'";
		disableToolBar(params);

		RootPanel.getBodyElement().removeClassName("toolbar");

		final DataPanelElementInfo elInfo = jsBaseGridPluginPanel.getElementInfo();
		if (elInfo.isToolBarProc()) {

			blinkingStartTimer = true;

			if (toolBarRefreshTimer != null) {
				toolBarRefreshTimer.cancel();
			}

			toolBarRefreshTimer = new Timer() {
				@Override
				public void run() {

					blinkingStartTimer = false;
					blinkingCount++;

					CompositeContext context = jsBaseGridPluginPanel.getContextForJSToolbar();

					dataService.getGridToolBar(context, elInfo,
							new GWTServiceCallback<GridToolBar>(
									"при получении данных панели инструментов грида с сервера") {

								@Override
								public void onSuccess(final GridToolBar result) {
									createJSToolBar(result);

									blinkingCount--;

									Scheduler.get().scheduleDeferred(new Command() {
										@Override
										public void execute() {

											if (blinkingCount == 0)
												RootPanel.getBodyElement().addClassName("toolbar");

											boolean xformRelated = false;
											for (DataPanelElementInfo elem : AppCurrContext
													.getReadyStateMap().keySet()) {
												if (elInfo.getRelated().contains(elem.getId())) {
													AppCurrContext.getReadyStateMap().put(elem,
															true);
													if (elem.getType() == DataPanelElementType.XFORMS)
														xformRelated = true;
												}
											}

											if (elInfo.getType() == DataPanelElementType.GRID
													&& !AppCurrContext.getReadyStateMap()
															.get(elInfo)) {
												AppCurrContext.getReadyStateMap().put(elInfo,
														true);
											}

											for (java.util.Map.Entry<DataPanelElementInfo, Boolean> ddd : AppCurrContext
													.getFromActionElementsMap().entrySet()) {
												if (ddd.getKey()
														.getType() == DataPanelElementType.WEBTEXT)
													AppCurrContext.getInstance()
															.setGridWithToolbarWebtextTrueStateForReadyStateMap(
																	true);
												if (ddd.getKey()
														.getType() == DataPanelElementType.CHART)
													AppCurrContext.getInstance()
															.setGridWithToolbarChartTrueStateForReadyStateMap(
																	true);
												if (ddd.getKey()
														.getType() == DataPanelElementType.GRID)
													AppCurrContext.getInstance()
															.setGridWithToolbarGridTrueStateForReadyStateMap(
																	true);
												if (ddd.getKey()
														.getType() == DataPanelElementType.GEOMAP)
													AppCurrContext.getInstance()
															.setGridWithToolbarGeoMapTrueStateForReadyStateMap(
																	true);
												if (ddd.getKey()
														.getType() == DataPanelElementType.PLUGIN)
													AppCurrContext.getInstance()
															.setGridWithToolbarPluginTrueStateForReadyStateMap(
																	true);
											}

											if (!booleanWithToolBar) {
												if (!AppCurrContext.getReadyStateMap()
														.containsValue(false)) {
													RootPanel.getBodyElement()
															.addClassName("ready");
													alreadyReadyWithToolbar = true;
													if (!xformRelated && AppCurrContext
															.getInstance()
															.getGridWithToolbarGridTrueStateForReadyStateMap())
														booleanWithToolBar = true;
												}
											}

											if (!booleanWithToolBar1 && !alreadyReadyWithToolbar) {
												boolean innerBool = false;
												for (DataPanelElementInfo el : AppCurrContext
														.getReadyStateMap().keySet()) {
													if ((el.getHideOnLoad() || el
															.getContext(
																	AppCurrContext.getInstance()
																			.getCurrentAction())
															.doHiding())
															&& !AppCurrContext.getReadyStateMap()
																	.get(el))
														innerBool = true;
													else if ((el.getHideOnLoad() || el
															.getContext(
																	AppCurrContext.getInstance()
																			.getCurrentAction())
															.doHiding())
															&& AppCurrContext.getReadyStateMap()
																	.get(el))
														innerBool = false;
												}
												if (innerBool) {
													RootPanel.getBodyElement()
															.addClassName("ready");
													if (!xformRelated && AppCurrContext
															.getInstance()
															.getGridWithToolbarGridTrueStateForReadyStateMap())
														booleanWithToolBar1 = true;
												}
											}
										}
									});
								}
							});
				}
			};
			toolBarRefreshTimer.schedule(Constants.GRID_SELECTION_DELAY);
		} else {
			isStaticToolBar = true;

			createJSToolBar(null);

			toolBarRefreshTimer = new Timer() {
				@Override
				public void run() {

					dataService.fakeRPC(new GWTServiceCallback<Void>("Error") {
						@Override
						public void onSuccess(final Void result) {

							Scheduler.get().scheduleDeferred(new Command() {
								@Override
								public void execute() {
									for (DataPanelElementInfo elem : AppCurrContext
											.getReadyStateMap().keySet()) {
										if (elInfo.getRelated().contains(elem.getId())) {
											AppCurrContext.getReadyStateMap().put(elem, true);
										}
									}

									if (elInfo.getType() == DataPanelElementType.GRID
											&& !AppCurrContext.getReadyStateMap().get(elInfo)) {
										AppCurrContext.getReadyStateMap().put(elInfo, true);
									}
									for (java.util.Map.Entry<DataPanelElementInfo, Boolean> ddd : AppCurrContext
											.getFromActionElementsMap().entrySet()) {
										if (ddd.getKey().getType() == DataPanelElementType.WEBTEXT)
											AppCurrContext.getInstance()
													.setGridWithoutToolbarWebtextTrueStateForReadyStateMap(
															true);
										if (ddd.getKey().getType() == DataPanelElementType.CHART)
											AppCurrContext.getInstance()
													.setGridWithoutToolbarChartTrueStateForReadyStateMap(
															true);
										if (ddd.getKey().getType() == DataPanelElementType.GEOMAP)
											AppCurrContext.getInstance()
													.setGridWithoutToolbarGeoMapTrueStateForReadyStateMap(
															true);
										if (ddd.getKey().getType() == DataPanelElementType.PLUGIN)
											AppCurrContext.getInstance()
													.setGridWithoutToolbarPluginTrueStateForReadyStateMap(
															true);
									}

									if (!AppCurrContext.getReadyStateMap().containsValue(false)) {
										RootPanel.getBodyElement().addClassName("ready");
									}

									if (!booleanWithoutToolBar) {
										boolean innerBool = false;
										for (DataPanelElementInfo el : AppCurrContext
												.getReadyStateMap().keySet()) {
											if ((el.getHideOnLoad() || el.getContext(AppCurrContext
													.getInstance().getCurrentAction()).doHiding())
													&& !AppCurrContext.getReadyStateMap().get(el))
												innerBool = true;
											else if ((el.getHideOnLoad()
													|| el.getContext(AppCurrContext.getInstance()
															.getCurrentAction()).doHiding())
													&& AppCurrContext.getReadyStateMap().get(el))
												innerBool = false;
										}
										if (innerBool) {
											RootPanel.getBodyElement().addClassName("ready");
											booleanWithoutToolBar = true;
										}
									}
								}
							});
						}
					});
				}
			};
			toolBarRefreshTimer.schedule(2000);
		}
	}

	public void fillToolBarImmediately() {

		if (!panel.getParent().isVisible()) {
			return;
		}

		String htmlForPlugin = "<div id='" + jsBaseGridPluginPanel.getDivIdToolBar() + "'></div>";

		HTML pluginHTML = new HTML(htmlForPlugin);

		panel.clear();
		panel.add(pluginHTML);

		String params = "'" + jsBaseGridPluginPanel.getElementInfo().getId().toString() + "'"
				+ ", '" + jsBaseGridPluginPanel.getDivIdToolBar() + "'";

		JSONObject metadata = new JSONObject();

		JSONObject common = new JSONObject();

		String toolbarClassName =
			jsBaseGridPluginPanel.getGridMetadata().getUISettings().getToolbarClassName();
		String toolbarStyle =
			jsBaseGridPluginPanel.getGridMetadata().getUISettings().getToolbarStyle();

		if (toolbarClassName != null) {
			common.put(CLASS_NAME, new JSONString(toolbarClassName));
		}
		if (toolbarStyle != null) {
			common.put(STYLE, new JSONString(toolbarStyle));
		}

		metadata.put("common", common);

		params = params + ", " + metadata;

		runToolBarImmediately(params);

	}

	// CHECKSTYLE:OFF
	private void createJSToolBar(final GridToolBar gridToolBar) {

		String htmlForPlugin = "<div id='" + jsBaseGridPluginPanel.getDivIdToolBar() + "'></div>";

		HTML pluginHTML = new HTML(htmlForPlugin);

		// if (needAdjustToolBarWidth) {
		// needAdjustToolBarWidth = false;
		// panel.setWidth(String.valueOf(panel.getOffsetWidth() + 2) + "px");
		// }

		panel.clear();
		panel.add(pluginHTML);

		JSONObject metadata = new JSONObject();

		JSONObject common = new JSONObject();
		if (gridToolBar != null) {
			if (gridToolBar.getStyle() != null) {
				common.put(STYLE, new JSONString(gridToolBar.getStyle()));
			}
			if (gridToolBar.getClassName() != null) {
				common.put(CLASS_NAME, new JSONString(gridToolBar.getClassName()));
			}
		}
		metadata.put("common", common);

		String params = "'" + jsBaseGridPluginPanel.getElementInfo().getId().toString() + "'"
				+ ", '" + jsBaseGridPluginPanel.getDivIdToolBar() + "'";

		actions.clear();
		needStaticItems = true;
		if (gridToolBar != null) {
			JSONObject jsonDynamicItems = new JSONObject();
			int id = 0;
			for (AbstractToolBarItem obj : gridToolBar.getItems()) {

				id++;

				JSONObject jsonItem = new JSONObject();
				jsonItem.put("id", new JSONString(String.valueOf(id)));

				if (obj instanceof ToolBarItem) {
					final ToolBarItem item = (ToolBarItem) obj;

					if (!item.isVisible()) {
						continue;
					}

					jsonItem.put("type", new JSONString(String.valueOf("item")));
					createJSBaseItem(jsonItem, item);

					actions.put(String.valueOf(id), item.getAction());

				} else if (obj instanceof ToolBarGroup) {
					ToolBarGroup group = (ToolBarGroup) obj;

					if (!group.isVisible()) {
						continue;
					}

					jsonItem.put("type", new JSONString(String.valueOf("group")));
					createJSBaseItem(jsonItem, group);

					int id2;
					int id2Inc = 0;
					for (AbstractToolBarItem item : group.getItems()) {
						id2Inc++;
						id2 = 100 * id + id2Inc;

						JSONObject jsonItem2 = new JSONObject();
						jsonItem2.put("id", new JSONString(String.valueOf(id2)));

						if (item instanceof ToolBarItem) {
							final ToolBarItem item2 = (ToolBarItem) item;

							if (!item2.isVisible()) {
								continue;
							}

							jsonItem2.put("type", new JSONString(String.valueOf("item")));
							createJSBaseItem(jsonItem2, item2);

							actions.put(String.valueOf(id2), item2.getAction());

						} else if (item instanceof ToolBarSeparator) {
							createJSSeparator(jsonItem2);
						}

						jsonItem.put(String.valueOf(id2), jsonItem2);

					}

				} else if (obj instanceof ToolBarSeparator) {
					createJSSeparator(jsonItem);
				}

				jsonDynamicItems.put(String.valueOf(id), jsonItem);

			}
			metadata.put("dynamicItems", jsonDynamicItems);
		}

		if (needStaticItems) {
			JSONObject jsonStaticItems = new JSONObject();
			jsBaseGridPluginPanel.addStaticItemToJSToolBar(jsonStaticItems);
			metadata.put("staticItems", jsonStaticItems);
		}

		params = params + ", " + metadata;

		runToolBar(params);

	}

	// CHECKSTYLE:ON

	private void createJSBaseItem(final JSONObject jsonItem, final BaseToolBarItem item) {
		jsonItem.put("disable", new JSONString(String.valueOf(item.isDisable())));
		jsonItem.put("text", new JSONString(item.getText()));
		jsonItem.put("hint", new JSONString(item.getHint()));
		jsonItem.put(STYLE, new JSONString(item.getStyle()));
		jsonItem.put(CLASS_NAME, new JSONString(item.getClassName()));
		jsonItem.put("iconClassName", new JSONString(item.getIconClassName()));

		if (item.getId() != null) {
			jsonItem.put("id", new JSONString(item.getId()));
			needStaticItems = false;
		}

		if (item.getPopupText() != null) {
			jsonItem.put("popupText", new JSONString(item.getPopupText()));
		}

		if (item instanceof ToolBarItem) {
			final ToolBarItem toolBarItem = (ToolBarItem) item;

			Action ac = toolBarItem.getAction();
			if ((ac != null) && (ac.containsServerActivity())) {
				jsonItem.put("needEnableDisableState", new JSONString(String.valueOf(true)));
			}

			if (toolBarItem.getDownloadLinkId() != null) {
				jsonItem.put("downloadLinkId", new JSONString(toolBarItem.getDownloadLinkId()));
			} else {
				if (toolBarItem.getFileName() != null) {
					jsonItem.put("downloadLinkId", new JSONString(toolBarItem.getFileName()));
				}
			}

		}

	}

	private void createJSSeparator(final JSONObject jsonItem) {
		jsonItem.put("type", new JSONString(String.valueOf("separator")));
	}

	private native void disableToolBar(final String params) /*-{

		try {
			$wnd.eval("disableGridToolBar(" + params + ");");
		} catch (e) {
			$wnd.safeIncludeJS("js/ui/grids/toolbar.js");
			$wnd.eval("disableGridToolBar(" + params + ");");
		}

	}-*/;

	private native void runToolBar(final String params) /*-{

		try {
			$wnd.eval("createGridToolBar(" + params + ");");
		} catch (e) {
			$wnd.safeIncludeJS("js/ui/grids/toolbar.js");
			$wnd.eval("createGridToolBar(" + params + ");");
		}

	}-*/;

	private native void runToolBarImmediately(final String params) /*-{

		try {
			$wnd.eval("createGridToolBarImmediately(" + params + ");");
		} catch (e) {
			$wnd.safeIncludeJS("js/ui/grids/toolbar.js");
			$wnd.eval("createGridToolBarImmediately(" + params + ");");
		}

	}-*/;

	public Action getAction(final String actionId) {
		return actions.get(actionId);
	}

	public boolean needBlinking() {
		return blinkingStartTimer || (blinkingCount > 0);
	}

	public Panel getToolBarPanel() {
		return panel;
	}

}
