package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.DataPanelElementCommand;

/**
 * Команда для добавления записи в гриде.
 * 
 */
public class GridAddRecordCommand extends DataPanelElementCommand<GridAddRecordResult> {

	public GridAddRecordCommand(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		super(aContext, aElementInfo);

	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	@Override
	protected void mainProc() throws Exception {
		DataPanelElementProc proc =
			getElementInfo().getProcByType(DataPanelElementProcType.ADDRECORD);
		GridSelector gridSelector = new GridSelector(proc != null ? proc.getName() : null);
		GridGateway gateway = gridSelector.getGateway();

		GridAddRecordResult gridAddRecordResult =
			gateway.addRecord((GridContext) getContext(), getElementInfo());
		setResult(gridAddRecordResult);
	}
}
