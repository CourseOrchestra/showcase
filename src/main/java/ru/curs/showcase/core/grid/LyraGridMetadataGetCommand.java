package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения метаданных для LyraGrid.
 * 
 */
public class LyraGridMetadataGetCommand extends DataPanelElementCommand<LyraGridMetadata> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LyraGridMetadataGetCommand(final GridContext aContext,
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

		LyraGridMetaFactory factory = new LyraGridMetaFactory(getContext(), getElementInfo());
		LyraGridMetadata gm = factory.buildMetadata();
		gm.setOkMessage(getContext().getOkMessage());

		setResult(gm);

	}

}
