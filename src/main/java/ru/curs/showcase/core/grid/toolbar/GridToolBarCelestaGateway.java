package ru.curs.showcase.core.grid.toolbar;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;

/**
 * Шлюз для получения панели инструментов, где источником данных является
 * celesta скрипта.
 * 
 * @author bogatov
 * 
 */
public class GridToolBarCelestaGateway implements GridToolBarGateway {
	@Override
	public GridToolBarRawData getGridToolBarRawData(
			final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		final String elementId = elementInfo.getId().getString();
		CelestaHelper<String> helper = new CelestaHelper<String>(context,
				String.class);
		String result = helper.runPython(
				elementInfo.getToolBarProc().getName(), elementId);
		return new GridToolBarRawData(result);
	}
}
