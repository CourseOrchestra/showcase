package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения данных для LyraVueGrid.
 * 
 */
public class LyraVueGridDataGetCommand extends DataPanelElementCommand<GridData> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LyraVueGridDataGetCommand(final GridContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@InputParam
	@Override
	public LyraGridContext getContext() {
		return (LyraGridContext) super.getContext();
	}

	/**
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#mainProc()
	 **/
	@Override
	protected void mainProc() throws Exception {

		LyraVueGridDataFactory factory =
			new LyraVueGridDataFactory(getContext(), getElementInfo());
		GridData gd = factory.buildData();
		gd.setOkMessage(getContext().getOkMessage());

		setResult(gd);

	}

}
