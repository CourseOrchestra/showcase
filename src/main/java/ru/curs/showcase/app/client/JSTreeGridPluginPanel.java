package ru.curs.showcase.app.client;

import java.util.*;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.ToolBarHelper;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.*;

/**
 * Класс-адаптер панели с внешним плагином типа JSTreeGrid.
 */
public class JSTreeGridPluginPanel extends JSBaseGridPluginPanel {
	private static final String PROC100 = "100%";

	private static final String STRING_SELECTED_RECORD_IDS_SEPARATOR = "D13&82#9g7";

	private static final String URL_FILE_DOWNLOAD = "/gridFileDownload";

	private static final String JSGRID_DESERIALIZATION_ERROR =
		CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
				"An error occurred while deserializing an object");

	private static final String AFTER_HTTP_POST_FROM_PLUGIN = "afterHttpPostFromPlugin";

	private static final String ERROR_WHEN_DOWNLOADING_FILE = "Error when downloading file";

	private static final String JSGRID_ERROR_GETTABLE =
		// "gridErrorGetTable";
		CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
				"when retrieving table data from server");

	private final VerticalPanel p = new VerticalPanel();
	private final HorizontalPanel generalHp = new HorizontalPanel();
	/**
	 * HTML виждет для плагина.
	 */
	private HTML pluginHTML = null;

	private final HorizontalPanel hpHeader = new HorizontalPanel();
	private final HorizontalPanel hpToolbar = new HorizontalPanel();
	private final HorizontalPanel hpFooter = new HorizontalPanel();

	/**
	 * Основная фабрика для GWT сериализации.
	 */
	private SerializationStreamFactory ssf = null;
	/**
	 * Вспомогательная фабрика для GWT сериализации.
	 */
	private SerializationStreamFactory addSSF = null;

	private Timer selectionTimer = null;
	private Timer clickTimer = null;
	private boolean doubleClick = false;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;

	public GridContext getLocalContext() {
		return localContext;
	}

	private GridMetadata gridMetadata = null;

	@Override
	public GridMetadata getGridMetadata() {
		return gridMetadata;
	}

	private String stringSelectedRecordIds = null;
	private boolean isFirstLoading = true;

	private boolean isFirstLoading() {
		return isFirstLoading;
	}

	private void setFirstLoading(final boolean aIsFirstLoading) {
		isFirstLoading = aIsFirstLoading;
	}

	private boolean isRefreshLoading = false;

	private boolean needRestoreAfterShowLoadingMessage = false;

	private ToolBarHelper toolBarHelper = null;

	@Override
	public VerticalPanel getPanel() {
		return p;
	}

	@Override
	public void hidePanel() {
		p.setVisible(false);
	}

	@Override
	public void showPanel() {
		p.setVisible(true);
	}

	@Override
	public DataPanelElement getElement() {
		return gridMetadata;
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

	private SerializationStreamFactory getAddObjectSerializer() {
		if (addSSF == null) {
			addSSF = WebUtils.createAddGWTSerializer();
		}
		return addSSF;
	}

	/**
	 * Установка процедур обратного вызова.
	 */
	// CHECKSTYLE:OFF
	private static native void setCallbackJSNIFunction() /*-{
															$wnd.gwtGetHttpParamsTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtEditorGetHttpParamsTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginEditorGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterLoadDataTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginAfterLoadData(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterPartialUpdateTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginAfterPartialUpdate(Ljava/lang/String;Ljava/lang/String;);																														$wnd.gwtAfterPartialUpdate = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginAfterPartialUpdate(Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterClickTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginAfterClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtAfterDoubleClickTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginAfterDoubleClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtProcessFileDownloadTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginProcessFileDownload(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtShowMessageTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginShowMessage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
                                                            $wnd.gwtShowErrorMessageTree = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginShowErrorMessage(Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtUpdateParents = @ru.curs.showcase.app.client.api.JSTreeGridPluginPanelCallbacksEvents::pluginUpdateParents(Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtToolbarRunAction = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginToolbarRunAction(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															}-*/;

	// CHECKSTYLE:ON

	public JSTreeGridPluginPanel(final DataPanelElementInfo element) {
		setContext(null);
		setElementInfo(element);
		setFirstLoading(true);
		setCallbackJSNIFunction();
		this.getPanel().addStyleName("jstreegrid-element");
		this.getPanel().addStyleName("id-" + element.getId().getString());
	}

	public JSTreeGridPluginPanel(final CompositeContext context,
			final DataPanelElementInfo element) {
		setContext(context);
		setElementInfo(element);
		setFirstLoading(true);
		setCallbackJSNIFunction();
		this.getPanel().addStyleName("jstreegrid-element");
		this.getPanel().addStyleName("id-" + element.getId().getString());

		refreshPanel();
	}

	@Override
	public void reDrawPanel(final CompositeContext context) {
		setContext(context);

		refreshPanel();
	}

	@Override
	public void refreshPanel() {
		if (isFirstLoading()) {
			if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
				// p.add(new HTML(AppCurrContext.getInstance().getBundleMap()
				// .get("please_wait_data_are_loading")));
				p.add(new HTML("<div class=\"progress-bar\"></div>"));
			} else {
				p.add(new HTML(""));
			}

		} else {
			p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
			if (this.getElementInfo().getShowLoadingMessage()) {
				needRestoreAfterShowLoadingMessage = true;
			}
		}

		if (isFirstLoading() || isNeedResetLocalContext()) {
			localContext = null;
			setFirstLoading(true);
			setDataGridPanel();
		} else {

			setFirstLoading(false);

			if (isPartialUpdate()) {
				partialUpdateGridPanel();
			} else {

				if (isCurrentLevelUpdate() || isChildLevelUpdate()) {
					String params = "'" + getElementInfo().getId().toString() + "'" + ", " + "'"
							+ getDivIdPlugin() + "'";
					if (isCurrentLevelUpdate()) {
						pluginProc(gridMetadata.getJSInfo().getCurrentLevelUpdate(), params);
					}
					if (isChildLevelUpdate()) {
						pluginProc(gridMetadata.getJSInfo().getChildLevelUpdate(), params);

					}
				} else {
					gridMetadata.getEventManager().getEvents().clear();

					isRefreshLoading = true;

					String params = "'" + getDivIdPlugin() + "'";
					pluginProc(gridMetadata.getJSInfo().getRefreshProc(), params);
				}
			}

		}

	}

	private void partialUpdateGridPanel() {

		final GridContext gc = getDetailedContext();
		gc.setPartialUpdate(true);

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getGridData(gc, getElementInfo(),
				new GWTServiceCallback<GridData>(
						// AppCurrContext.getInstance().getBundleMap().get(JSGRID_ERROR_GETTABLE))
						// {
						JSGRID_ERROR_GETTABLE) {

					@Override
					public void onFailure(final Throwable caught) {
						gc.setPartialUpdate(false);
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(final GridData aLiveGridData) {
						gc.setPartialUpdate(false);
						super.onSuccess(aLiveGridData);
						partialUpdateGridPanelByGrid(aLiveGridData);
					}
				});
	}

	private void partialUpdateGridPanelByGrid(final GridData aLiveGridData) {

		String params = "'" + getElementInfo().getId().toString() + "'" + ", " + "'"
				+ getDivIdPlugin() + "', " + aLiveGridData.getData();

		pluginProc(gridMetadata.getJSInfo().getPartialUpdate(), params);

	}

	private void setDataGridPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		GridContext gc = getDetailedContext();

		dataService.getGridMetadata(gc, getElementInfo(),
				new GWTServiceCallback<GridMetadata>(
						// AppCurrContext.getInstance().getBundleMap().get(JSGRID_ERROR_GETTABLE))
						// {
						JSGRID_ERROR_GETTABLE) {

					@Override
					public void onSuccess(final GridMetadata aGridMetadata) {

						super.onSuccess(aGridMetadata);

						setDataGridPanelByGrid(aGridMetadata);

					}
				});
	}

	private void setDataGridPanelByGrid(final GridMetadata aGridMetadata) {

		gridMetadata = aGridMetadata;

		beforeUpdateGrid();

		updateGridFull();

		p.setHeight(PROC100);

	}

	@Override
	protected void internalResetLocalContext() {
		localContext = null;
	}

	private void beforeUpdateGrid() {

		resetLocalContext();

		resetGridSettingsToCurrent();

	}

	// CHECKSTYLE:OFF
	private void updateGridFull() {

		// MessageBox.showSimpleMessage("",
		// "gridMetadata.getUISettings().getGridWidth() = "
		// + gridMetadata.getUISettings().getGridWidth()
		// + ", gridMetadata.getUISettings().getGridHeight() = "
		// + gridMetadata.getUISettings().getGridHeight());

		final String div = "<div id='";
		final String htmlForPlugin = div + getDivIdPlugin() + "' style='width:"
				+ gridMetadata.getUISettings().getGridWidth() + "; height:"
				+ gridMetadata.getUISettings().getGridHeight() + "px'></div>";

		pluginHTML = new HTML(htmlForPlugin);

		String params =
			"'" + getElementInfo().getId().toString() + "'" + ", " + "'" + getDivIdPlugin() + "'";

		if (AppCurrContext.getInstance()
				.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
				.indexOf(getDivIdPlugin()) < 0) {

			AppCurrContext.getInstance()
					.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
					.add(getDivIdPlugin());

			// for (String param : aPlugin.getRequiredCSS()) {
			// AccessToDomModel.addCSSLink(param);
			// }

			// for (String param : gridMetadata.getJSInfo().getRequiredJS()) {
			// AccessToDomModel.addScriptLink(param);
			// }

		}

		// ----------------------------------------

		JSONObject metadata = new JSONObject();

		JSONObject common = new JSONObject();
		common.put("limit", new JSONString(String.valueOf(gridMetadata.getLiveInfo().getLimit())));

		String selectionModel = "CELL";
		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			selectionModel = "RECORDS";
		}
		common.put("selectionModel", new JSONString(selectionModel));

		Cell selected = getStoredRecordId();
		if (selected.recId != null) {
			common.put("selRecId", new JSONString(selected.recId));
		}
		if (selected.colId != null) {
			common.put("selColId", new JSONString(selected.colId));
		}

		if (gridMetadata.getUISettings().isVisibleColumnsHeader()) {
			common.put("isVisibleColumnsHeader", new JSONString("true"));
		}

		if (gridMetadata.getUISettings().isAllowTextSelection()) {
			common.put("isAllowTextSelection", new JSONString("true"));
		}

		common.put("loadingMessage",
				new JSONString(
						// AppCurrContext.getInstance().getBundleMap().get("jsGridLoadingMessage")));
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Loading...")));

		common.put("noDataMessage",
				new JSONString(
						// AppCurrContext.getInstance().getBundleMap().get("jsGridNoRecordsMessage")));
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"No records")));

		common.put("stringSelectedRecordIdsSeparator",
				new JSONString(STRING_SELECTED_RECORD_IDS_SEPARATOR));

		if (gridMetadata.getUISettings().getHaColumnHeader() != null) {
			common.put("haColumnHeader", new JSONString(
					gridMetadata.getUISettings().getHaColumnHeader().toString().toLowerCase()));
		}

		if ((getElementInfo().getProcByType(DataPanelElementProcType.ADDRECORD) == null)
				&& (getElementInfo().getProcByType(DataPanelElementProcType.SAVE) == null)) {
			common.put("readonly", new JSONString("true"));
		}

		if ((gridMetadata.getGridSorting() != null)
				&& (gridMetadata.getGridSorting().getSortColId() != null)) {
			common.put("sortColId", new JSONString(gridMetadata.getGridSorting().getSortColId()));
			common.put("sortColDirection", new JSONString(
					gridMetadata.getGridSorting().getSortColDirection().toString()));
		}

		if (gridMetadata.isExpandAllRecords()) {
			common.put("expandAllRecords", new JSONString("true"));
		}

		if (gridMetadata.getUISettings().isVisibleFieldSave()) {
			common.put("fieldSave", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "Save")));
		}

		metadata.put("common", common);

		JSONObject columns = new JSONObject();
		for (final GridColumnConfig egcc : gridMetadata.getColumns()) {

			if (!egcc.isVisible()) {
				continue;
			}

			JSONObject column = new JSONObject();
			column.put("id", new JSONString(egcc.getId()));
			if (egcc.getParentId() != null) {
				column.put("parentId", new JSONString(egcc.getParentId()));
			}
			column.put("caption", new JSONString(egcc.getCaption()));

			if (egcc.isReadonly()) {
				column.put("readonly", new JSONString(String.valueOf("true")));
			}

			String valueType = "";
			if (egcc.getValueType() != null) {
				valueType = egcc.getValueType().toString();
			}
			column.put("valueType", new JSONString(valueType));

			column.put("editor", new JSONString(getColumnEditor(egcc)));

			column.put("style", new JSONString(getCommonColumnStyle() + getColumnStyle(egcc)));

			column.put("urlImageFileDownload",
					new JSONString(gridMetadata.getUISettings().getUrlImageFileDownload()));

			if (egcc.getFirstSortDirection() == Sorting.DESC) {
				column.put("firstSortDirectionDescending", new JSONString("true"));
			}

			columns.put(egcc.getId(), column);

		}
		metadata.put("columns", columns);

		if (gridMetadata.getVirtualColumns() != null) {
			JSONObject virtualColumns = new JSONObject();
			for (final VirtualColumn vc : gridMetadata.getVirtualColumns()) {
				JSONObject virtualColumn = new JSONObject();
				virtualColumn.put("id", new JSONString(vc.getId()));
				if (vc.getParentId() != null) {
					virtualColumn.put("parentId", new JSONString(vc.getParentId()));
				}
				if (vc.getWidth() != null) {
					virtualColumn.put("width", new JSONString(vc.getWidth()));
				}
				if (vc.getStyle() != null) {
					virtualColumn.put("style", new JSONString(vc.getStyle()));
				}
				virtualColumn.put("virtualColumnType",
						new JSONString(vc.getVirtualColumnType().toString()));

				virtualColumns.put(vc.getId(), virtualColumn);
			}
			metadata.put("virtualColumns", virtualColumns);
		}

		params = params + ", " + metadata;

		// ----------------------------------------

		hpHeader.clear();
		HTML header = new HTML();
		if (!gridMetadata.getHeader().isEmpty()) {
			header.setHTML(gridMetadata.getHeader());
		}
		hpHeader.add(header);

		hpFooter.clear();
		HTML footer = new HTML();
		if (!gridMetadata.getFooter().isEmpty()) {
			footer.setHTML(gridMetadata.getFooter());
		}
		hpFooter.add(footer);

		p.setSize(PROC100, PROC100);

		hpHeader.setSize(PROC100, PROC100);
		hpFooter.setSize(PROC100, PROC100);

		generalHp.clear();
		p.clear();
		p.add(hpHeader);
		// ----------------------------------------

		toolBarHelper = getToolBarHelper();

		// if (gridMetadata.getUISettings().getGridWidth().contains("px")) {
		// int ind = gridMetadata.getUISettings().getGridWidth().indexOf("px");
		// String str = gridMetadata.getUISettings().getGridWidth().substring(0,
		// ind).trim();
		// int number = Integer.parseInt(str);
		// number = number + 2;
		//
		// hpToolbar.setWidth(number + "px");
		// toolBarHelper.getToolBarPanel().setWidth(number + "px");
		// } else {
		// hpToolbar.setWidth(gridMetadata.getUISettings().getGridWidth());
		// }
		hpToolbar.setWidth(gridMetadata.getUISettings().getGridWidth());

		generalHp.setWidth("100%");
		hpToolbar.add(toolBarHelper.getToolBarPanel());
		p.add(hpToolbar);
		hpToolbar.setVisible(gridMetadata.getUISettings().isVisibleToolBar());

		// ----------------------------------------
		p.add(generalHp);
		generalHp.add(pluginHTML);
		p.add(hpFooter);

		// ----------------------------------------

		try {

			if (gridMetadata.getUISettings().isToolbarCreateImmediately()) {
				getToolBarHelper().fillToolBarImmediately();
			}

			toolBarHelper.fillToolBar();

			runGrid(gridMetadata.getJSInfo().getCreateProc(), params);

		} catch (JavaScriptException e) {
			if (e.getCause() != null) {
				MessageBox.showMessageWithDetails(
						// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"External plugin constructing error"),
						e.getMessage(), GeneralException.generateDetailedInfo(e.getCause()),
						GeneralException.getMessageType(e.getCause()),
						GeneralException.needDetailedInfo(e.getCause()), null);
			} else {
				MessageBox.showSimpleMessage(
						// AppCurrContext.getInstance().getBundleMap().get("error_of_plugin_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"External plugin constructing error"),
						e.getMessage());
			}
		}

	}

	// CHECKSTYLE:ON

	private native void runGrid(final String procName, final String params) /*-{

		try {
			$wnd.eval(procName + "(" + params + ");");
		} catch (e) {
			$wnd.safeIncludeJS("js/ui/grids/treeDGrid.js");
			$wnd.eval(procName + "(" + params + ");");
		}

	}-*/;

	/**
	 * 
	 * Вызов процедуры в плагине.
	 * 
	 * @param procName
	 *            - имя js - процедуры
	 * @param params
	 *            - параметры js - процедуры
	 * 
	 */
	private native String pluginProc(final String procName, final String params) /*-{
		return $wnd.eval(procName + "(" + params + ");");
	}-*/;

	public void pluginProcessFileDownload(final String recId, final String colId,
			final String downloadFileByGetMethod) {

		if ("true".equalsIgnoreCase(downloadFileByGetMethod)) {
			String colLinkId = null;
			for (GridColumnConfig lgcc : gridMetadata.getColumns()) {
				if (colId.equals(lgcc.getId())) {
					colLinkId = lgcc.getLinkId();
					break;
				}
			}

			if (colLinkId != null) {
				DownloadHelper dh = DownloadHelper.getInstance();
				dh.setEncoding(FormPanel.ENCODING_URLENCODED);
				dh.clear();

				dh.setErrorCaption(CourseClientLocalization.gettext(
						AppCurrContext.getInstance().getDomain(), ERROR_WHEN_DOWNLOADING_FILE));
				dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + URL_FILE_DOWNLOAD);

				for (String key : getContext().getSessionParamsMap().keySet()) {
					if ("perspective".equalsIgnoreCase(key) || "userdata".equalsIgnoreCase(key)) {
						dh.addParam(key,
								URL.encode(getContext().getSessionParamsMap().get(key).get(0)));
						break;
					}
				}

				dh.addParam("elementId", URL.encode(getElementInfo().getId().getString()));

				dh.addParam("linkId", URL.encode(colLinkId));

				DataPanelElementProc proc = getElementInfo().getProcById(colLinkId);
				if (proc != null) {
					dh.addParam("procName", URL.encode(proc.getName()));
				}

				dh.addParam("recordId", URL.encode(recId));

				dh.setMethod(FormPanel.METHOD_GET);
				try {
					dh.submit();
				} finally {
					dh.setMethod(FormPanel.METHOD_POST);
				}

			}

		} else {
			String colLinkId = null;
			for (GridColumnConfig lgcc : gridMetadata.getColumns()) {
				if (colId.equals(lgcc.getId())) {
					colLinkId = lgcc.getLinkId();
					break;
				}
			}

			if (colLinkId != null) {
				DownloadHelper dh = DownloadHelper.getInstance();
				dh.setEncoding(FormPanel.ENCODING_URLENCODED);
				dh.clear();

				dh.setErrorCaption(CourseClientLocalization.gettext(
						AppCurrContext.getInstance().getDomain(), ERROR_WHEN_DOWNLOADING_FILE));
				dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + URL_FILE_DOWNLOAD);

				try {
					dh.addParam("linkId", colLinkId);

					dh.addParam(getContext().getClass().getName(),
							getContext().toParamForHttpPost(getObjectSerializer()));
					dh.addParam(DataPanelElementInfo.class.getName(),
							getElementInfo().toParamForHttpPost(getObjectSerializer()));

					dh.addParam("recordId", recId);
					// dh.addParam("recordId", "12идентификатор_файла 3");

					dh.submit();
				} catch (SerializationException e) {
					ru.curs.showcase.app.client.MessageBox
							.showSimpleMessage(CourseClientLocalization.gettext(
									AppCurrContext.getInstance().getDomain(),
									ERROR_WHEN_DOWNLOADING_FILE), e.getMessage());
				}
			}
		}
	}

	@Override
	public void toolbarProcessFileDownload(final String downloadLinkId) {

		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(
				// AppCurrContext.getInstance().getBundleMap().get("grid_error_caption_file_download"));
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						ERROR_WHEN_DOWNLOADING_FILE));
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridFileDownload");

		try {
			dh.addParam("linkId", downloadLinkId);

			dh.addParam(getContext().getClass().getName(),
					getContext().toParamForHttpPost(getObjectSerializer()));
			dh.addParam(DataPanelElementInfo.class.getName(),
					getElementInfo().toParamForHttpPost(getObjectSerializer()));

			dh.addParam("recordId", getDetailedContext().getCurrentRecordId());

			dh.submit();
		} catch (SerializationException e) {
			ru.curs.showcase.app.client.MessageBox.showSimpleMessage(
					// AppCurrContext.getInstance().getBundleMap().get("grid_error_caption_file_download"),
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							ERROR_WHEN_DOWNLOADING_FILE),
					e.getMessage());
		}

	}

	public boolean pluginUpdateParents(final String parentId) {

		if (getElementInfo().getProcByType(DataPanelElementProcType.UPDATEPARENTS) == null) {
			return false;
		}

		final GridContext gc = getDetailedContext();
		gc.setUpdateParents(true);
		gc.setParentId(parentId);

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getGridData(gc, getElementInfo(),
				new GWTServiceCallback<GridData>(
						// AppCurrContext.getInstance().getBundleMap().get(JSGRID_ERROR_GETTABLE))
						// {
						JSGRID_ERROR_GETTABLE) {

					@Override
					public void onFailure(final Throwable caught) {
						gc.setUpdateParents(false);
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(final GridData aLiveGridData) {
						gc.setUpdateParents(false);
						super.onSuccess(aLiveGridData);
						partialUpdateGridPanelByGrid(aLiveGridData);
					}
				});

		return true;

	}

	public JSONObject pluginGetHttpParams(final int offset, final int limit,
			final String sortColId, final String sortColDir, final String parentId) {

		GridContext gridContext = getDetailedContext();
		// gridContext.getLiveInfo().setOffset(offset);
		// gridContext.getLiveInfo().setLimit(limit);
		gridContext.setParentId(parentId);

		if ((sortColId != null) && (sortColDir != null)) {
			GridSorting gs = new GridSorting(sortColId, Sorting.valueOf(sortColDir));
			gridContext.setGridSorting(gs);
		}

		JSONObject params = new JSONObject();
		try {
			params.put("gridContextName", new JSONString(gridContext.getClass().getName()));
			params.put("gridContextValue",
					new JSONString(gridContext.toParamForHttpPost(getObjectSerializer())));

			params.put("elementInfoName", new JSONString(getElementInfo().getClass().getName()));
			params.put("elementInfoValue",
					new JSONString(getElementInfo().toParamForHttpPost(getObjectSerializer())));
		} catch (SerializationException e) {
			params.put("error", new JSONString(
					// AppCurrContext.getInstance().getBundleMap().get("jsGridSerializationError")));
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"Error during serialization parameters for Http-request plug.")));
		}

		return params;
	}

	public JSONObject pluginEditorGetHttpParams(final String data, final String editorType) {

		GridContext gridContext = getContextForEditor();
		if ("save".equalsIgnoreCase(editorType)) {
			JSONObject column = new JSONObject();
			int i = 1;
			for (final GridColumnConfig egcc : gridMetadata.getColumns()) {
				column.put("col" + String.valueOf(i), new JSONString(egcc.getCaption()));
				i++;
			}
			String json =
				"{\"savedata\":{\"data\":" + data + ", \"columns\":" + column.toString() + "}}";
			gridContext.setEditorData(json);

			gridContext.setAddRecordData(null);
		} else {
			gridContext.setEditorData(null);

			String json = "{\"addrecorddata\":{\"currentRecordId\":\""
					+ gridContext.getCurrentRecordId() + "\"}}";
			gridContext.setAddRecordData(json);
		}

		JSONObject params = new JSONObject();
		try {
			params.put("gridContextName", new JSONString(gridContext.getClass().getName()));
			params.put("gridContextValue",
					new JSONString(gridContext.toParamForHttpPost(getObjectSerializer())));

			params.put("elementInfoName", new JSONString(getElementInfo().getClass().getName()));
			params.put("elementInfoValue",
					new JSONString(getElementInfo().toParamForHttpPost(getObjectSerializer())));
		} catch (SerializationException e) {
			params.put("error", new JSONString(
					// AppCurrContext.getInstance().getBundleMap().get("jsGridSerializationError")));
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"Error during serialization parameters for Http-request plug.")));
		}

		return params;
	}

	public void pluginAfterLoadData(final String stringEvents, final String stringAddData,
			final String wrongSelection) {

		if (stringEvents != null) {
			try {

				@SuppressWarnings("unchecked")
				List<GridEvent> eventsNew = (List<GridEvent>) getObjectSerializer()
						.createStreamReader(stringEvents).readObject();

				List<GridEvent> eventsAdd;
				if (gridMetadata.getEventManager().getEvents().size() == 0) {
					eventsAdd = eventsNew;
				} else {
					eventsAdd = new ArrayList<GridEvent>();

					boolean needAdd;
					for (ru.curs.showcase.app.api.grid.GridEvent ev : eventsNew) {
						needAdd = true;
						for (ru.curs.showcase.app.api.grid.GridEvent evOld : gridMetadata
								.getEventManager().getEvents()) {
							if (ev.extEquals(evOld)) {
								needAdd = false;
								break;
							}
						}
						if (needAdd) {
							eventsAdd.add(ev);
						}
					}
				}
				gridMetadata.getEventManager().getEvents().addAll(eventsAdd);

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage(AFTER_HTTP_POST_FROM_PLUGIN,
						JSGRID_DESERIALIZATION_ERROR + " Events: " + e.getMessage());
			}
		}

		if (stringAddData != null) {
			try {
				GridAddData addData = (GridAddData) getAddObjectSerializer()
						.createStreamReader(stringAddData).readObject();

				if ((hpHeader.getWidgetCount() > 0) && (!((HTML) (hpHeader.getWidget(0))).getHTML()
						.equals(addData.getHeader()))) {
					((HTML) (hpHeader.getWidget(0))).setHTML(addData.getHeader());
				}

				if ((hpFooter.getWidgetCount() > 0) && (!((HTML) (hpFooter.getWidget(0))).getHTML()
						.equals(addData.getFooter()))) {
					((HTML) (hpFooter.getWidget(0))).setHTML(addData.getFooter());
				}
			} catch (SerializationException e) {
				MessageBox.showSimpleMessage(AFTER_HTTP_POST_FROM_PLUGIN,
						JSGRID_DESERIALIZATION_ERROR + " GridAddData: " + e.getMessage());
			}
		}

		final Timer delayTimer = new Timer() {
			@Override
			public void run() {
				RootPanel.getBodyElement().addClassName("updategrid");
			}
		};
		delayTimer.schedule(500);

		afterUpdateGrid(wrongSelection);

	}

	public void pluginAfterPartialUpdate(final String stringEvents) {

		if (stringEvents != null) {
			try {

				@SuppressWarnings("unchecked")
				List<GridEvent> eventsNew = (List<GridEvent>) getObjectSerializer()
						.createStreamReader(stringEvents).readObject();

				for (ru.curs.showcase.app.api.grid.GridEvent ev : eventsNew) {
					for (ru.curs.showcase.app.api.grid.GridEvent evOld : gridMetadata
							.getEventManager().getEvents()) {
						if (ev.extEquals(evOld)) {
							gridMetadata.getEventManager().getEvents().remove(evOld);
							break;
						}
					}
					gridMetadata.getEventManager().getEvents().add(ev);
				}

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage(AFTER_HTTP_POST_FROM_PLUGIN,
						JSGRID_DESERIALIZATION_ERROR + " Events: " + e.getMessage());
			}
		}

	}

	private String replaceServiceSymbols(final String mess) {
		String ret = mess;
		ret = ret.replace(ExchangeConstants.OK_MESSAGE_X, "\\x");
		ret = ret.replace(ExchangeConstants.OK_MESSAGE_QUOT, "\\\"");
		return ret;
	}

	public void pluginShowMessage(final String stringMessage, final String editorType) {

		if (!stringMessage.isEmpty()) {
			try {
				UserMessage um = (UserMessage) getObjectSerializer()
						.createStreamReader(replaceServiceSymbols(stringMessage)).readObject();
				if (um != null) {

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
							CourseClientLocalization
									.gettext(AppCurrContext.getInstance().getDomain(), "Message");
					}

					String subtypeMessage = um.getSubtype();

					MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage,
							false, subtypeMessage);

				}

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage("pluginShowMessage",
						// AppCurrContext.getInstance().getBundleMap().get(JSGRID_DESERIALIZATION_ERROR)
						JSGRID_DESERIALIZATION_ERROR + " UserMessage: " + e.getMessage());
			}
		}

		// MessageBox.showSimpleMessage(gridContext.getCurrentColumnId(),
		// gridContext.getCurrentRecordId());

		GridContext gridContext = getDetailedContext();
		gridContext.setEditorData(null);

		InteractionType it;
		if ("save".equalsIgnoreCase(editorType)) {
			it = InteractionType.SAVE_DATA;
		} else {
			it = InteractionType.ADD_RECORD;
		}

		processClick(gridContext.getCurrentRecordId(), gridContext.getCurrentColumnId(), it);

	}

	public void pluginShowErrorMessage(final String stringMessage) {
		if (!stringMessage.isEmpty()) {
			String mess = stringMessage.replace("<root>", "").replace("</root>", "");
			mess = replaceServiceSymbols(mess);
			try {
				Throwable caught =
					(Throwable) getObjectSerializer().createStreamReader(mess).readObject();

				WebUtils.onFailure(caught, "Error");

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage("showErrorMessage()",
						"DeserializationError: " + e.getMessage());
			}
		}
	}

	public void pluginAfterClick(final String recId, final String colId,
			final String aStringSelectedRecordIds) {

		stringSelectedRecordIds = aStringSelectedRecordIds;

		if (gridMetadata.getUISettings().isSingleClickBeforeDoubleClick()) {
			handleClick(recId, colId, InteractionType.SINGLE_CLICK);
		} else {

			if (clickTimer != null) {
				clickTimer.cancel();
			}

			clickTimer = new Timer() {
				@Override
				public void run() {
					if (!doubleClick) {
						handleClick(recId, colId, InteractionType.SINGLE_CLICK);
					}
					doubleClick = false;
				}
			};
			clickTimer.schedule(gridMetadata.getUISettings().getDoubleClickTime());

		}

	}

	public void pluginAfterDoubleClick(final String recId, final String colId,
			final String aStringSelectedRecordIds) {
		stringSelectedRecordIds = aStringSelectedRecordIds;

		doubleClick = true;

		handleClick(recId, colId, InteractionType.DOUBLE_CLICK);
	}

	private void handleClick(final String recId, final String colId,
			final InteractionType interactionType) {

		saveCurrentClickSelection(recId, colId);

		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			selectedRecordsChanged();
		}

		if (!isFirstLoading) {
			getToolBarHelper().fillToolBar();
		}

		processClick(recId, colId, interactionType);

	}

	private void processClick(final String rowId, final String colId,
			final InteractionType interactionType) {
		RootPanel.getBodyElement().removeClassName("updategrid");

		Action ac = null;

		List<ru.curs.showcase.app.api.grid.GridEvent> events =
			gridMetadata.getEventManager().getEventForCell(rowId, colId, interactionType);

		for (ru.curs.showcase.app.api.grid.GridEvent ev : events) {
			ac = ev.getAction();
			runAction(ac);
		}

		final Timer delayTimer = new Timer() {
			@Override
			public void run() {
				RootPanel.getBodyElement().addClassName("ready");
				RootPanel.getBodyElement().addClassName("updategrid");
			}
		};
		delayTimer.schedule(2000);
	}

	@Override
	public void runAction(final Action ac) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, gridMetadata);
			ActionExecuter.execAction();
		}
	}

	private void selectedRecordsChanged() {
		if (selectionTimer != null) {
			selectionTimer.cancel();
		}

		selectionTimer = new Timer() {
			@Override
			public void run() {
				saveCurrentCheckBoxSelection();
				processSelectionRecords();
			}
		};
		selectionTimer.schedule(Constants.GRID_SELECTION_DELAY);

	}

	private void processSelectionRecords() {
		List<String> selectedRecordIds = new ArrayList<String>();

		if (stringSelectedRecordIds != null) {
			String[] strs = stringSelectedRecordIds.split(STRING_SELECTED_RECORD_IDS_SEPARATOR);
			for (String s : strs) {
				selectedRecordIds.add(s);
			}
		}

		Action ac = gridMetadata.getEventManager()
				.getSelectionActionForDependentElements(selectedRecordIds);

		runAction(ac);

	}

	private boolean adjustSelectionRecords(final String wrongSelection) {
		boolean ret = false;
		if (!((wrongSelection == null) || wrongSelection.isEmpty())) {
			GridContext gridContext = getDetailedContext();
			String[] strs = wrongSelection.split(STRING_SELECTED_RECORD_IDS_SEPARATOR);
			for (String s : strs) {
				if (gridContext.getSelectedRecordIds().remove(s)) {
					ret = true;
				}
				if (s.equals(gridContext.getCurrentRecordId())) {
					gridContext.setCurrentRecordId(null);
					ret = true;
				}
			}
		}
		return ret;
	}

	/**
	 * Замечание: сбрасывать состояние грида нужно обязательно до вызова
	 * отрисовки зависимых элементов. Иначе потеряем выделенную запись или
	 * ячейку в related!
	 * 
	 */

	private void afterUpdateGrid(final String wrongSelection) {

		needRestoreAfterShowLoadingMessage = false;

		if (isFirstLoading) {

			// toolBarHelper.fillToolBar();

			runAction(gridMetadata.getActionForDependentElements());

		} else {

			adjustSelectionRecords(wrongSelection);

			if (isRefreshLoading) {
				toolBarHelper.fillToolBar();
			}

		}

		isRefreshLoading = false;

		setupTimer();

		setFirstLoading(false);

	}

	private String getColumnStyle(final GridColumnConfig egcc) {
		String style = "";

		style = style + "width:" + egcc.getWidth() + "px;";

		if (egcc.getHorizontalAlignment() != null) {
			style = style + "text-align:" + egcc.getHorizontalAlignment().toString().toLowerCase()
					+ ";";
		}

		return style;
	}

	private String getCommonColumnStyle() {
		String style = "";

		if (gridMetadata.getTextColor() != null) {
			style = style + "color:" + gridMetadata.getTextColor() + ";";
		}

		if (gridMetadata.getBackgroundColor() != null) {
			style = style + "background-color:" + gridMetadata.getBackgroundColor() + ";";
		}

		if (gridMetadata.getFontSize() != null) {
			style = style + "font-size:" + gridMetadata.getFontSize() + ";";
		}

		if ((gridMetadata.getFontSize() != null) && (gridMetadata.getFontModifiers() != null)) {
			for (FontModifier fm : gridMetadata.getFontModifiers()) {
				switch (fm) {
				case ITALIC:
					style = style + "font-style:italic;";
					continue;
				case BOLD:
					style = style + "font-weight:bold;";
					continue;
				case STRIKETHROUGH:
					style = style + "text-decoration:line-through;";
					continue;
				case UNDERLINE:
					style = style + "text-decoration:underline;";
					continue;
				default:
					continue;
				}
			}
		}

		if (gridMetadata.getUISettings().getDisplayMode() != ColumnValueDisplayMode.SINGLELINE) {
			style = style + "white-space:normal;";
		} else {
			style = style + "white-space:nowrap;";
		}

		return style;
	}

	private String getColumnEditor(final GridColumnConfig egcc) {

		String editor = egcc.getEditor();

		if (editor == null) {
			String columnEditor = "text";

			GridValueType valueType = egcc.getValueType();
			if (valueType.isGeneralizedString()) {
				columnEditor = "text";
			}
			if (valueType.isNumber()) {
				columnEditor = "number";
			}
			if (valueType.isDate()) {
				columnEditor = "date";
			}

			editor =
				"{editOn: has('touch') ? 'click' : 'dblclick', editor: '" + columnEditor + "'}";
		}

		return editor;
	}

	/**
	 * Локальный класс для работы с ячейкой грида в Showcase.
	 * 
	 * @author den
	 * 
	 */
	class Cell {
		private String recId = null;
		private String colId = null;
	}

	private void saveCurrentCheckBoxSelection() {
		localContext.getSelectedRecordIds().clear();

		if (stringSelectedRecordIds != null) {
			String[] strs = stringSelectedRecordIds.split(STRING_SELECTED_RECORD_IDS_SEPARATOR);
			for (String s : strs) {
				localContext.getSelectedRecordIds().add(s);
			}
		}
	}

	private void saveCurrentClickSelection(final String recId, final String colId) {
		localContext.setCurrentRecordId(recId);
		localContext.setCurrentColumnId(colId);
	}

	/**
	 * Получает информацию о сохраненном выделении в гриде, при этом user
	 * settings имеет приоритет над данными из БД.
	 * 
	 * @return
	 */
	private Cell getStoredRecordId() {
		Cell cell = new Cell();

		cell.recId = localContext.getCurrentRecordId();
		cell.colId = localContext.getCurrentColumnId();

		return cell;
	}

	protected void resetGridSettingsToCurrent() {
		localContext = new GridContext();
		localContext.setSubtype(DataPanelElementSubType.JS_TREE_GRID);
		getLocalContext().setParentId(null);
		getLocalContext().resetForReturnAllRecords();

		String recId = gridMetadata.getAutoSelectRecordId();
		String colId = gridMetadata.getAutoSelectColumnId();

		saveCurrentClickSelection(recId, colId);

		stringSelectedRecordIds = recId;
		saveCurrentCheckBoxSelection();
	}

	/**
	 * Экспорт в Excel.
	 * 
	 * @param exportType
	 *            GridToExcelExportType
	 */
	@Override
	public void exportToExcel(final Widget wFrom, final GridToExcelExportType exportType,
			final String fileName) {
		String parentId = null;
		if (exportType == GridToExcelExportType.CURRENTPAGE) {
			parentId = getStoredRecordId().recId;
		}
		GridContext gridContext = getDetailedContext();
		gridContext.setParentId(parentId);
		gridContext.setFileName(fileName);

		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(CourseClientLocalization.gettext(
				AppCurrContext.getInstance().getDomain(), "Error when exporting to Excel"));

		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridToExcel");

		try {
			dh.addParam(exportType.getClass().getName(), exportType.toString());

			@SuppressWarnings("unused")
			SerializationStreamFactory ssfExcel = dh.getAddObjectSerializer();

			dh.addParam(getDetailedContext().getClass().getName(),
					gridContext.toParamForHttpPost(getObjectSerializer()));
			dh.addParam(DataPanelElementInfo.class.getName(),
					getElementInfo().toParamForHttpPost(getObjectSerializer()));

			dh.submit();

		} catch (SerializationException e) {
			MessageBox.showSimpleMessage(
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"Error when exporting to Excel"),
					e.getMessage());
		}
	}

	/**
	 * Передача в буфер обмена.
	 * 
	 * @return ClipboardDialog
	 * 
	 */
	@Override
	public ClipboardDialog copyToClipboard() {
		String params = "'" + getDivIdPlugin() + "'";
		String s = pluginProc(gridMetadata.getJSInfo().getClipboardProc(), params);

		ClipboardDialog cd = new ClipboardDialog(s);
		cd.center();
		return cd;
	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = localContext;
		if (result == null) {
			result = GridContext.createFirstLoadDefault();
			result.setSubtype(DataPanelElementSubType.JS_TREE_GRID);
			result.setParentId(null);
		}
		result.setIsFirstLoad(isNeedResetLocalContext());
		result.resetForReturnAllRecords();
		result.applyCompositeContext(getContext());

		return result;
	}

	private GridContext getContextForEditor() {
		GridContext gridContext = getDetailedContext();
		GridContext editorContext = gridContext.gwtClone();
		editorContext.addRelated(getElementInfo().getId(), gridContext);
		return editorContext;
	}

	@Override
	public ToolBarHelper getToolBarHelper() {
		if (this.toolBarHelper == null) {
			this.toolBarHelper = new ToolBarHelper(dataService, this);
		}
		return this.toolBarHelper;
	}

	@Override
	public void editorAddRecord() {
		String params = "'" + getDivIdPlugin() + "'";
		pluginProc(gridMetadata.getJSInfo().getAddRecordProc(), params);
	}

	@Override
	public void editorSave() {
		String params = "'" + getDivIdPlugin() + "'";
		pluginProc(gridMetadata.getJSInfo().getSaveProc(), params);
	}

	@Override
	public void editorRevert() {
		String params = "'" + getDivIdPlugin() + "'";
		pluginProc(gridMetadata.getJSInfo().getRevertProc(), params);
	}

}