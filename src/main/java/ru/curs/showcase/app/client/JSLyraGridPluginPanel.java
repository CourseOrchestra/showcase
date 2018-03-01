package ru.curs.showcase.app.client;

import java.util.*;

import com.google.gwt.core.client.*;
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
 * Класс-адаптер панели с внешним плагином типа JSLyraGrid.
 */
public class JSLyraGridPluginPanel extends JSBaseGridPluginPanel {
	private static final String PROC100 = "100%";

	private static final String STRING_SELECTED_RECORD_IDS_SEPARATOR = "D13&82#9g7";

	private static final String JSGRID_DESERIALIZATION_ERROR =
		// "jsGridDeserializationError";
		CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
				"An error occurred while deserializing an object");

	private static final String ERROR_WHEN_DOWNLOADING_FILE = "Error when downloading file";

	private static final String AFTER_HTTP_POST_FROM_PLUGIN = "afterHttpPostFromPlugin";

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
	private LyraGridContext localContext = null;

	public LyraGridContext getLocalContext() {
		return localContext;
	}

	private LyraGridMetadata gridMetadata = null;

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

	private String lyraGridSorting = "";

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

	public SerializationStreamFactory getAddObjectSerializer() {
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
															$wnd.gwtGetHttpParamsLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtEditorGetHttpParamsLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginEditorGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtAfterLoadDataLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterLoadData(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterPartialUpdateLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterPartialUpdate(Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterClickLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtAfterDoubleClickLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterDoubleClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtProcessFileDownloadLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginProcessFileDownload(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtShowMessageLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginShowMessage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtShowErrorMessageLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginShowErrorMessage(Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtSetOldPositionLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginSetOldPosition(Ljava/lang/String;Ljava/lang/String;);
		  											        $wnd.gwtToolbarRunAction = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginToolbarRunAction(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															}-*/;

	// CHECKSTYLE:ON

	public JSLyraGridPluginPanel(final DataPanelElementInfo element) {
		setContext(null);
		setElementInfo(element);
		setFirstLoading(true);
		setCallbackJSNIFunction();
		this.getPanel().addStyleName("jslyragrid-element");
		this.getPanel().addStyleName("id-" + element.getId().getString());

	}

	public JSLyraGridPluginPanel(final CompositeContext context,
			final DataPanelElementInfo element) {
		setContext(context);
		setElementInfo(element);
		setFirstLoading(true);
		setCallbackJSNIFunction();
		this.getPanel().addStyleName("jslyragrid-element");
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
				gridMetadata.getEventManager().getEvents().clear();

				isRefreshLoading = true;

				String params = "'" + getDivIdPlugin() + "'";
				pluginProc(gridMetadata.getJSInfo().getRefreshProc(), params);
			}

		}
	}

	private void partialUpdateGridPanel() {

		final LyraGridContext gc = getDetailedContext();
		gc.setPartialUpdate(true);

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getGridData(gc, getElementInfo(),
				new GWTServiceCallback<GridData>(
						// AppCurrContext.getInstance().getBundleMap().get("gridErrorGetTable"))
						// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving table data from server")) {

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

		LyraGridContext gc = getDetailedContext();

		dataService.getLyraGridMetadata(gc, getElementInfo(),
				new GWTServiceCallback<LyraGridMetadata>(
						// AppCurrContext.getInstance().getBundleMap().get("gridErrorGetTable"))
						// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving table data from server")) {

					@Override
					public void onSuccess(final LyraGridMetadata aGridMetadata) {

						super.onSuccess(aGridMetadata);

						setDataGridPanelByGrid(aGridMetadata);

					}
				});
	}

	private void setDataGridPanelByGrid(final LyraGridMetadata aGridMetadata) {
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

		// ----------------------------------------------------

		final String div = "<div id='";
		final String htmlForPlugin = div + getDivIdPlugin() + "' style='width:"
				+ gridMetadata.getUISettings().getGridWidth() + "; height:"
				+ gridMetadata.getUISettings().getGridHeight() + "px'></div>";

		// ----------------------------------------------------

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
		common.put("pageNumber",
				new JSONString(String.valueOf(gridMetadata.getLiveInfo().getPageNumber())));

		if (gridMetadata.getUISettings().isVisiblePager()) {
			common.put("isVisiblePager", new JSONString("true"));
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

		common.put("pagingLinks", new JSONString(
				String.valueOf(gridMetadata.getUISettings().getPagesButtonCount())));

		if (gridMetadata.getLyraGridSorting() != null) {
			common.put("lyraGridSorting", new JSONString(gridMetadata.getLyraGridSorting()));
			lyraGridSorting = gridMetadata.getLyraGridSorting();
		}

		if (gridMetadata.isNeedCreateWebSocket()) {
			common.put("isNeedCreateWebSocket", new JSONString("true"));
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

			columns.put(egcc.getId(), column);

			if (((LyraGridColumnConfig) egcc).isSortingAvailable()) {
				column.put("sortingAvailable", new JSONString(String.valueOf("true")));
			}

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
			$wnd.safeIncludeJS("js/ui/grids/lyraDGrid.js");
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

	public void pluginProcessFileDownload(final String recId, final String colId) {
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

			dh.setErrorCaption(
					// AppCurrContext.getInstance().getBundleMap().get("grid_error_caption_file_download"));
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							ERROR_WHEN_DOWNLOADING_FILE));
			dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridFileDownload");

			try {
				dh.addParam("linkId", colLinkId);

				dh.addParam(getContext().getClass().getName(),
						getContext().toParamForHttpPost(getObjectSerializer()));
				dh.addParam(DataPanelElementInfo.class.getName(),
						getElementInfo().toParamForHttpPost(getObjectSerializer()));

				dh.addParam("recordId", recId);

				dh.submit();
			} catch (SerializationException e) {
				ru.curs.showcase.app.client.MessageBox.showSimpleMessage(
						// AppCurrContext.getInstance().getBundleMap().get("grid_error_caption_file_download"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								ERROR_WHEN_DOWNLOADING_FILE),
						e.getMessage());
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

	public void pluginSetOldPosition(final String oldPosition) {
		LyraGridContext gridContext = getDetailedContext();
		// gridContext.setDgridOldPosition(Integer.parseInt(oldPosition));
		gridContext.getLiveInfo().setOffset(Integer.parseInt(oldPosition));
	}

	public JSONObject pluginGetHttpParams(final int offset, final int limit,
			final String sortColId, final String sortColDir, final String refreshId) {

		LyraGridContext gridContext = getDetailedContext();
		if (offset >= 0) {
			gridContext.setDgridOldPosition(gridContext.getLiveInfo().getOffset());
			gridContext.getLiveInfo().setOffset(offset);
			gridContext.getLiveInfo().setLimit(limit);
		}

		gridContext.setRefreshId(refreshId);

		if ((sortColId != null) && (sortColDir != null)) {
			GridSorting gs = new GridSorting(sortColId, Sorting.valueOf(sortColDir));
			gridContext.setGridSorting(gs);

			String newLyraGridSorting = "\"" + gs.getSortColId() + "\"";
			if (gs.getSortColDirection() == Sorting.DESC) {
				newLyraGridSorting = newLyraGridSorting + " desc";
			}

			if (!newLyraGridSorting
					.equalsIgnoreCase(lyraGridSorting.toLowerCase().replace(" asc", ""))) {
				lyraGridSorting = newLyraGridSorting;
				gridContext.setSortingChanged(true);
				gridContext.setDgridOldPosition(0);
			}
		}

		gridContext.setIsFirstLoad(isFirstLoading());

		if (isFirstLoading()) {
			gridContext.setExternalSortingOrFilteringChanged(true);
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
		} finally {
			gridContext.setRefreshId(null);
			gridContext.setSortingChanged(false);
			gridContext.setExternalSortingOrFilteringChanged(false);
		}

		return params;
	}

	public JSONObject pluginEditorGetHttpParams(final String data, final String editorType) {

		LyraGridContext gridContext = getDetailedContext();
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
			final String totalCount) {

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
						// AppCurrContext.getInstance().getBundleMap().get(JSGRID_DESERIALIZATION_ERROR)
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
						JSGRID_DESERIALIZATION_ERROR + " LyraGridAddData: " + e.getMessage());
			}
		}

		final Timer delayTimer = new Timer() {
			@Override
			public void run() {
				RootPanel.getBodyElement().addClassName("updategrid");
			}
		};
		delayTimer.schedule(500);

		afterUpdateGrid();

		LyraGridContext gridContext = getDetailedContext();
		gridContext.getLiveInfo().setTotalCount(Integer.parseInt(totalCount));

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
						// AppCurrContext.getInstance().getBundleMap().get(JSGRID_DESERIALIZATION_ERROR)
						JSGRID_DESERIALIZATION_ERROR + " Events: " + e.getMessage());
			}
		}

	}

	public void pluginShowMessage(final String stringMessage, final String editorType) {

		if (!stringMessage.isEmpty()) {
			try {
				UserMessage um = (UserMessage) getObjectSerializer()
						.createStreamReader(stringMessage).readObject();
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

		LyraGridContext gridContext = getDetailedContext();
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

	/**
	 * Замечание: сбрасывать состояние грида нужно обязательно до вызова
	 * отрисовки зависимых элементов. Иначе потеряем выделенную запись или
	 * ячейку в related!
	 * 
	 */

	private void afterUpdateGrid() {

		needRestoreAfterShowLoadingMessage = false;

		if (isFirstLoading) {

			resetSelection();

			// toolBarHelper.fillToolBar();

			runAction(gridMetadata.getActionForDependentElements());

		} else {

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

	private void resetSelection() {
		// selectionModel.deselectAll();
		stringSelectedRecordIds = null;
		if (localContext == null) {
			return;
		}
		localContext.getSelectedRecordIds().clear();
		localContext.setCurrentColumnId(null);
		localContext.setCurrentRecordId(null);
	}

	private void saveCurrentCheckBoxSelection() {
		if (localContext == null) {
			return;
		}

		localContext.getSelectedRecordIds().clear();

		if (stringSelectedRecordIds != null) {
			String[] strs = stringSelectedRecordIds.split(STRING_SELECTED_RECORD_IDS_SEPARATOR);
			for (String s : strs) {
				localContext.getSelectedRecordIds().add(s);
			}
		}
	}

	private void saveCurrentClickSelection(final String recId, final String colId) {
		if (localContext == null) {
			return;
		}

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
		if ((localContext != null) && (localContext.getCurrentRecordId() != null)) {
			cell.recId = localContext.getCurrentRecordId();
			cell.colId = localContext.getCurrentColumnId();
		} else {
			cell.recId = gridMetadata.getAutoSelectRecordId();
			cell.colId = gridMetadata.getAutoSelectColumnId();
		}
		return cell;
	}

	protected void resetGridSettingsToCurrent() {
		localContext = new LyraGridContext();
		localContext.setSubtype(DataPanelElementSubType.JS_LYRA_GRID);

		Cell selected = getStoredRecordId();
		saveCurrentClickSelection(selected.recId, selected.colId);

		stringSelectedRecordIds = selected.recId;
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

		LyraGridContext gridContext = getDetailedContext();
		String params = "'" + getDivIdPlugin() + "'";
		String refreshId = pluginProc("getRefreshIdForExcelLyraDGrid", params);
		gridContext.setRefreshId(refreshId);
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
		} finally {
			gridContext.setRefreshId(null);
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
	public LyraGridContext getDetailedContext() {
		LyraGridContext result = localContext;
		if (result == null) {
			result = LyraGridContext.createFirstLoadDefault();
			result.setSubtype(DataPanelElementSubType.JS_LYRA_GRID);
		}
		result.setIsFirstLoad(isNeedResetLocalContext());
		result.applyCompositeContext(getContext());

		return result;
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