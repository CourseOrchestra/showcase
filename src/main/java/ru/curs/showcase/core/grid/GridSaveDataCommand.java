package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.DataPanelElementCommand;

/**
 * Команда для сохранения отредактированных данных в гриде.
 * 
 */
public class GridSaveDataCommand extends DataPanelElementCommand<GridSaveResult> {

	public GridSaveDataCommand(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		super(aContext, aElementInfo);

	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	@Override
	protected void mainProc() throws Exception {
		DataPanelElementProc proc = getElementInfo().getProcByType(DataPanelElementProcType.SAVE);
		GridSelector gridSelector = new GridSelector(proc != null ? proc.getName() : null);
		GridGateway gateway = gridSelector.getGateway();

		GridSaveResult gridSaveResult =
			gateway.saveData((GridContext) getContext(), getElementInfo());
		setResult(gridSaveResult);

	}
}
