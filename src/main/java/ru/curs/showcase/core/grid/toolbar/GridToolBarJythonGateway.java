package ru.curs.showcase.core.grid.toolbar;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.JythonQuery;

/**
 * Шлюз для получения данных, источником которых являются jython скрипты.
 * 
 * @author bogatov
 * 
 */
public class GridToolBarJythonGateway extends JythonQuery<String> implements
		GridToolBarGateway {

	private CompositeContext context;
	private DataPanelElementInfo elementInfo;

	protected GridToolBarJythonGateway() {
		super(String.class);
	}

	@Override
	public GridToolBarRawData getGridToolBarRawData(
			final CompositeContext oContext,
			final DataPanelElementInfo oElementInfo) {
		this.context = oContext;
		this.elementInfo = oElementInfo;
		runTemplateMethod();
		String data = getResult();
		return new GridToolBarRawData(data);
	}

	@Override
	protected Object execute() {
		return getProc().getGridToolBarData(context,
				elementInfo.getId().getString());
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getToolBarProc().getName();
	}

}
