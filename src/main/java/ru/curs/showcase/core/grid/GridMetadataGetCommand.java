package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Команда получения метаданных для LiveGrid.
 * 
 */
public class GridMetadataGetCommand extends DataPanelElementCommand<GridMetadata> {

	private GridServerState gridServerState = null;

	public GridServerState getGridServerState() {
		return gridServerState;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public GridMetadataGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@InputParam
	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	public int getTotalCount() {
		SourceSelector<ElementSettingsGateway> sselector =
			new GridSettingsSelector(getElementInfo());
		ElementSettingsGateway sgateway = sselector.getGateway();
		RecordSetElementRawData rawSettings = sgateway.getRawData(getContext(), getElementInfo());

		GridMetaFactory factory = new GridMetaFactory(rawSettings, null);
		int totalCount = factory.buildTotalCount();

		return totalCount;
	}

	/**
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#mainProc()
	 **/
	@Override
	protected void mainProc() throws Exception {

		SourceSelector<ElementSettingsGateway> sselector =
			new GridSettingsSelector(getElementInfo());
		ElementSettingsGateway sgateway = sselector.getGateway();
		RecordSetElementRawData rawSettings = sgateway.getRawData(getContext(), getElementInfo());

		gridServerState = getGridState(getContext(), getElementInfo());

		GridMetaFactory factory = new GridMetaFactory(rawSettings, gridServerState);
		GridMetadata gm = factory.buildMetadata();
		gm.setOkMessage(getContext().getOkMessage());

		setResult(gm);

		AppInfoSingleton.getAppInfo().storeGridCacheState(getSessionId(), getElementInfo(),
				getContext(), gridServerState);

	}

	private GridServerState getGridState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state;
		if (context.isFirstLoad()) {
			state = prepareInitGridServerState(context, elementInfo);
		} else {
			state =
				(GridServerState) AppInfoSingleton.getAppInfo().getGridCacheState(getSessionId(),
						elementInfo, context);
			if (state == null) {
				// состояние устарело или память была очищена
				state = prepareInitGridServerState(context, elementInfo);
				context.setIsFirstLoad(true);
			}
		}
		return state;
	}

	private GridServerState prepareInitGridServerState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state = new GridServerState();
		AppInfoSingleton.getAppInfo().storeGridCacheState(getSessionId(), elementInfo, context,
				state);
		return state;
	}

}
