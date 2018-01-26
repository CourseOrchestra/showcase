package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.OutputStreamDataFile;

/**
 * Команда для скачивания файла с сервера в гриде.
 * 
 */
public class GridFileDownloadCommand extends DataPanelElementCommand<OutputStreamDataFile> {

	private final ID linkId;
	private final String recordId;

	@InputParam
	public ID getLinkId() {
		return linkId;
	}

	@InputParam
	public String getRecordId() {
		return recordId;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public GridFileDownloadCommand(final CompositeContext aContext,
			final DataPanelElementInfo aElInfo, final ID aLinkId, final String aRecordId) {
		super(aContext, aElInfo);
		linkId = aLinkId;
		recordId = aRecordId;
	}

	@Override
	protected void mainProc() throws Exception {
		DataPanelElementProc proc = getElementInfo().getProcs().get(linkId);
		GridSelector gridSelector = new GridSelector(proc != null ? proc.getName() : null);
		GridGateway gateway = gridSelector.getGateway();
		OutputStreamDataFile file =
			gateway.downloadFile(getContext(), getElementInfo(), linkId, recordId);
		// TODO Борису - подключить UserXMLTransformer
		// UserXMLTransformer transformer =
		// new UserXMLTransformer(file, getElementInfo().getProcs().get(linkId),
		// new DataPanelElementContext(getContext(), getElementInfo()));
		// transformer.checkAndTransform();
		// setResult(transformer.getOutputStreamResult());
		setResult(file);
	}

	@Override
	protected void logOutput() {
		super.logOutput();

		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info(String.format("Размер скачиваемого файла: %d байт", getResult().getData()
					.size()));
		}
	}

}
