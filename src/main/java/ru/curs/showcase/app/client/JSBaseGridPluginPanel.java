package ru.curs.showcase.app.client;

import java.util.Map.Entry;

import com.google.gwt.json.client.*;
import com.google.gwt.user.client.ui.Widget;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementProcType;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.ToolBarHelper;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

/**
 * Базовый класс-адаптер панели с внешним плагином типа JSGrid.
 */
public abstract class JSBaseGridPluginPanel extends BasicElementPanelBasis {

	private static final String ICON_CLASS_NAME = "iconClassName";

	public abstract GridMetadata getGridMetadata();

	public abstract ToolBarHelper getToolBarHelper();

	public abstract void toolbarProcessFileDownload(final String downloadLinkId);

	public abstract void runAction(final Action ac);

	public abstract void exportToExcel(final Widget wFrom, final GridToExcelExportType exportType,
			final String fileName);

	public abstract ClipboardDialog copyToClipboard();

	public abstract void editorAddRecord();

	public abstract void editorSave();

	public abstract void editorRevert();

	@SuppressWarnings("unused")
	private boolean isJSLiveGridPluginPanel() {
		return getClass().getName().contains("JSLiveGridPluginPanel");
	}

	@SuppressWarnings("unused")
	private boolean isJSPageGridPluginPanel() {
		return getClass().getName().contains("JSPageGridPluginPanel");
	}

	private boolean isJSTreeGridPluginPanel() {
		return getClass().getName().contains("JSTreeGridPluginPanel");
	}

	private boolean isJSLyraGridPluginPanel() {
		return getClass().getName().contains("JSLyraGridPluginPanel");
	}

	// CHECKSTYLE:OFF
	public void addStaticItemToJSToolBar(final JSONObject jsonStaticItems) {
		if (getGridMetadata().getUISettings().isVisibleExportToExcelCurrentPage()) {
			String id = "exportToExcelCurrentPage";
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("id", new JSONString(id));
			jsonItem.put("type", new JSONString(String.valueOf("item")));
			jsonItem.put("disable", new JSONString(String.valueOf(false)));
			jsonItem.put("text", new JSONString(""));

			String local;
			if (isJSTreeGridPluginPanel()) {
				local = "Export to Excel descendants of the current record";
			} else {
				local = "Export current page to Excel";
			}
			jsonItem.put("hint", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), local)));

			local =
				"Export to Excel is running. It may take a few minutes. Click here to hide the message.";
			jsonItem.put("popupText", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), local)));

			jsonItem.put(ICON_CLASS_NAME, new JSONString(id));
			jsonStaticItems.put(String.valueOf(id), jsonItem);
		}
		if (getGridMetadata().getUISettings().isVisibleExportToExcelAll()) {
			String id = "exportToExcelAll";
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("id", new JSONString(id));
			jsonItem.put("type", new JSONString(String.valueOf("item")));
			jsonItem.put("disable", new JSONString(String.valueOf(false)));
			jsonItem.put("text", new JSONString(""));

			String local;
			if (isJSTreeGridPluginPanel()) {
				local = "Export to Excel records 0-level";
			} else {
				local = "Export entire table to Excel";
			}
			jsonItem.put("hint", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), local)));

			local =
				"Export to Excel is running. It may take a few minutes. Click here to hide the message.";
			jsonItem.put("popupText", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), local)));

			jsonItem.put(ICON_CLASS_NAME, new JSONString(id));
			jsonStaticItems.put(String.valueOf(id), jsonItem);
		}
		if (getGridMetadata().getUISettings().isVisibleCopyToClipboard()) {
			String id = "copyToClipboard";
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("id", new JSONString(id));
			jsonItem.put("type", new JSONString(String.valueOf("item")));
			jsonItem.put("disable", new JSONString(String.valueOf(false)));
			jsonItem.put("text", new JSONString(""));
			jsonItem.put("hint", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "Copy to clipboard")));
			jsonItem.put(ICON_CLASS_NAME, new JSONString(id));
			jsonStaticItems.put(String.valueOf(id), jsonItem);
		}
		if (getGridMetadata().getUISettings().isVisibleFilter() && (!isJSTreeGridPluginPanel())
				&& (!isJSLyraGridPluginPanel())) {
			String id = "filter";
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("id", new JSONString(id));
			jsonItem.put("type", new JSONString(String.valueOf("item")));
			jsonItem.put("disable", new JSONString(String.valueOf(false)));
			jsonItem.put("text", new JSONString(""));
			jsonItem.put("hint", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "Filter")));
			jsonItem.put(ICON_CLASS_NAME, new JSONString(id));
			jsonStaticItems.put(String.valueOf(id), jsonItem);
		}
		if (getElementInfo().getProcByType(DataPanelElementProcType.ADDRECORD) != null) {
			String id = "addRecord";
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("id", new JSONString(id));
			jsonItem.put("type", new JSONString(String.valueOf("item")));
			jsonItem.put("disable", new JSONString(String.valueOf(false)));
			jsonItem.put("text", new JSONString(""));
			jsonItem.put("hint", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "Add Record")));
			jsonItem.put(ICON_CLASS_NAME, new JSONString(id));
			jsonStaticItems.put(String.valueOf(id), jsonItem);
		}
		if ((getElementInfo().getProcByType(DataPanelElementProcType.SAVE) != null)
				&& getGridMetadata().getUISettings().isVisibleSave()) {
			String id = "save";
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("id", new JSONString(id));
			jsonItem.put("type", new JSONString(String.valueOf("item")));
			jsonItem.put("disable", new JSONString(String.valueOf(false)));
			jsonItem.put("text", new JSONString(""));
			jsonItem.put("hint", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "Save")));
			jsonItem.put(ICON_CLASS_NAME, new JSONString(id));
			jsonStaticItems.put(String.valueOf(id), jsonItem);
		}
		if ((getElementInfo().getProcByType(DataPanelElementProcType.SAVE) != null)
				&& getGridMetadata().getUISettings().isVisibleRevert()) {
			String id = "revert";
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("id", new JSONString(id));
			jsonItem.put("type", new JSONString(String.valueOf("item")));
			jsonItem.put("disable", new JSONString(String.valueOf(false)));
			jsonItem.put("text", new JSONString(""));
			jsonItem.put("hint", new JSONString(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "Undo")));
			jsonItem.put(ICON_CLASS_NAME, new JSONString(id));
			jsonStaticItems.put(String.valueOf(id), jsonItem);
		}
	}
	// CHECKSTYLE:ON

	public boolean pluginToolbarRunAction(final String actionId, final String downloadLinkId) {

		if (getToolBarHelper() == null) {
			return false;
		}

		if (getToolBarHelper().needBlinking()) {
			return true;
		}

		if ("exportToExcelCurrentPage".equals(actionId)) {

			exportToExcel(null, GridToExcelExportType.CURRENTPAGE, downloadLinkId);

		} else if ("exportToExcelAll".equals(actionId)) {

			exportToExcel(null, GridToExcelExportType.ALL, downloadLinkId);

		} else if ("copyToClipboard".equals(actionId)) {

			copyToClipboard();

		} else if ("filter".equals(actionId)) {

			new JSFilter((JSLiveGridPluginPanel) this);

		} else if ("addRecord".equals(actionId)) {

			editorAddRecord();

		} else if ("save".equals(actionId)) {

			editorSave();

		} else if ("revert".equals(actionId)) {

			editorRevert();

		} else {

			Action action = getToolBarHelper().getAction(actionId);
			if (action != null) {
				CompositeContext contextForJSToolbarAction = getContextForJSToolbar();
				action.setContext(contextForJSToolbarAction);
				// action.setActionCaller(menuItem);
				runAction(action);
			}

			if (downloadLinkId != null) {
				toolbarProcessFileDownload(downloadLinkId);
			}

		}

		return false;
	}

	public CompositeContext getContextForJSToolbar() {
		CompositeContext elContext = this.getContext();
		CompositeContext context = new CompositeContext();
		context.setMain(elContext.getMain());
		context.setAdditional(elContext.getAdditional());
		context.setFilter(elContext.getFilter());
		context.setSession(elContext.getSession());
		context.setSessionParamsMap(elContext.getSessionParamsMap());
		context.setCurrentDatapanelWidth(elContext.getCurrentDatapanelWidth());
		context.setCurrentDatapanelHeight(elContext.getCurrentDatapanelHeight());
		for (Entry<ID, CompositeContext> entry : elContext.getRelated().entrySet()) {
			context.addRelated(entry.getKey(), entry.getValue());
		}
		context.addRelated(this.getElementInfo().getId(), this.getDetailedContext());
		return context;
	}

	public String getDivIdPlugin() {
		return getElementInfo().getFullId() + Constants.PLUGIN_DIV_ID_SUFFIX;
	}

	public String getDivIdToolBar() {
		return getElementInfo().getFullId() + "_toolbar";
	}
}