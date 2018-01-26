package ru.curs.showcase.core.grid;

import java.io.ByteArrayOutputStream;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ExcelFile;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда экспорта в грид.
 * 
 */
public final class GridExcelExportCommand extends DataPanelElementCommand<ExcelFile> {

	private final GridToExcelExportType exportType;

	@InputParam
	public GridToExcelExportType getExportType() {
		return exportType;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public GridExcelExportCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final GridToExcelExportType aExportType) {
		super(aContext, aElInfo);
		exportType = aExportType;
	}

	@InputParam
	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected void preProcess() {
		super.preProcess();

		if (exportType == GridToExcelExportType.ALL) {
			getContext().resetForReturnAllRecords();
		}

		initCommandContext();
	}

	@Override
	protected void mainProc() throws Exception {

		GridDataGetCommand command = new GridDataGetCommand(getContext(), getElementInfo(), false);
		command.execute();

		GridToExcelXMLFactory factory =
			new GridToExcelXMLFactory(command.getColumns(), command.getRecords());
		org.w3c.dom.Document xml = factory.build();
		ByteArrayOutputStream stream = XMLUtils.xsltTransformForGrid(xml);
		setResult(new ExcelFile(stream, getContext().getFileName(), "xls"));

	}

	@Override
	protected void postProcess() {
		super.postProcess();
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info(String.format("Размер возвращаемого файла: %d байт",
					getResult().getData().size()));
		}
	}
}
