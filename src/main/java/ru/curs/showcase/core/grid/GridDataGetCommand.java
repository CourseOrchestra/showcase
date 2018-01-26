package ru.curs.showcase.core.grid;

import java.util.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.core.sp.RecordSetElementRawData;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Команда получения данных для грида.
 * 
 */
public class GridDataGetCommand extends DataPanelElementCommand<GridData> {

	private final Boolean applyLocalFormatting;

	private List<GridColumnConfig> columns = null;
	private List<HashMap<String, String>> records = null;

	public List<GridColumnConfig> getColumns() {
		return columns;
	}

	public List<HashMap<String, String>> getRecords() {
		return records;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public GridDataGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final Boolean aApplyLocalFormatting) {
		super(aContext, aElInfo);
		applyLocalFormatting = aApplyLocalFormatting;
	}

	@InputParam
	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	/**
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#mainProc()
	 **/
	@Override
	protected void mainProc() throws Exception {

		SourceSelector<GridGateway> selector = new GridSelector(getElementInfo());
		GridGateway gateway = selector.getGateway();
		RecordSetElementRawData rawData = gateway.getRawData(getContext(), getElementInfo());

		GridServerState state =
			(GridServerState) AppInfoSingleton.getAppInfo().getGridCacheState(getSessionId(),
					getElementInfo(), getContext());

		if (applyLocalFormatting) {
			if (state == null) {
				GridMetadataGetCommand command =
					new GridMetadataGetCommand(getContext(), getElementInfo());
				command.execute();
				state = command.getGridServerState();
			} else {
				if (state.isForceLoadSettings()) {
					GridMetadataGetCommand command =
						new GridMetadataGetCommand(getContext(), getElementInfo());
					state.setTotalCount(command.getTotalCount());
				}
			}
		} else {
			GridMetadataGetCommand command =
				new GridMetadataGetCommand(getContext(), getElementInfo());
			columns = command.execute().getColumns();
			state = command.getGridServerState();
		}

		GridDataFactory factory = new GridDataFactory(rawData, state, applyLocalFormatting);
		GridData grid = factory.buildData();
		grid.setOkMessage(getContext().getOkMessage());
		if (!applyLocalFormatting) {
			records = factory.getRecords();
		}

		setResult(grid);

	}
}
