package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;

/**
 * Класс-адаптер панели с внешним плагином типа JSPageGrid.
 */
public class JSPageGridPluginPanel extends JSLiveGridPluginPanel {

	public JSPageGridPluginPanel(final CompositeContext context, final DataPanelElementInfo element) {
		super(context, element);
		this.getPanel().addStyleName("jspagegrid-element");
		this.getPanel().addStyleName("id-" + element.getId().getString());
	}

	public JSPageGridPluginPanel(final DataPanelElementInfo element) {
		super(element);
		this.getPanel().addStyleName("jspagegrid-element");
		this.getPanel().addStyleName("id-" + element.getId().getString());
	}

	@Override
	protected void resetGridSettingsToCurrent() {
		super.resetGridSettingsToCurrent();

		getLocalContext().setSubtype(DataPanelElementSubType.JS_PAGE_GRID);
	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = super.getDetailedContext();
		result.setSubtype(DataPanelElementSubType.JS_PAGE_GRID);
		return result;
	}

	@Override
	protected native void runGrid(final String procName, final String params) /*-{

		try {
			$wnd.eval(procName + "(" + params + ");");
		} catch (e) {
			$wnd.safeIncludeJS("js/ui/grids/pageDGrid.js");
			$wnd.eval(procName + "(" + params + ");");
		}

	}-*/;

}