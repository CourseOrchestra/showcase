package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения данных для LyraGrid.
 * 
 */
public class LyraGridDataGetCommand extends DataPanelElementCommand<GridData> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LyraGridDataGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo) {
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

		LyraGridDataFactory factory = new LyraGridDataFactory(getContext(), getElementInfo());
		GridData gd = factory.buildData();
		gd.setOkMessage(getContext().getOkMessage());

		setResult(gd);

	}

}
