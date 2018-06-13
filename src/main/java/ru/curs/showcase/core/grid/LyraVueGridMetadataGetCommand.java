package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения метаданных для LyraVueGrid.
 * 
 */
public class LyraVueGridMetadataGetCommand extends DataPanelElementCommand<GridMeta> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LyraVueGridMetadataGetCommand(final GridContext aContext,
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

		LyraVueGridMetaFactory factory =
			new LyraVueGridMetaFactory(getContext(), getElementInfo());
		GridMeta md = factory.buildMetadata();
		md.setOkMessage(getContext().getOkMessage());

		setResult(md);

	}

}
