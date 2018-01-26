package ru.curs.showcase.core.grid.toolbar;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.toolbar.GridToolBar;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.command.ServiceLayerCommand;

/**
 * Команда получения данных для построения панели инструментов грида.
 * @author bogatov
 *
 */
public class GridToolBarCommand extends ServiceLayerCommand<GridToolBar> {
	private DataPanelElementInfo elInfo;
	
	public GridToolBarCommand(final CompositeContext oContext, final DataPanelElementInfo oElInfo) {
		super(oContext);
		this.elInfo = oElInfo;
	}

	@Override
	protected void mainProc() throws Exception {
		SourceSelector<GridToolBarGateway> selector = new GridToolBarSelector(this.elInfo);
		GridToolBarGateway gateway = selector.getGateway();
		GridToolBarRawData rawData = gateway.getGridToolBarRawData(getContext(), this.elInfo);
		GridToolBarFactory factory = new GridToolBarFactory(rawData, getContext());
		setResult(factory.build());
	}

}
