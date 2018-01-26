package ru.curs.showcase.core.grid;

import java.io.ByteArrayOutputStream;
import java.util.*;

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
public final class LyraGridExcelExportCommand extends DataPanelElementCommand<ExcelFile> {

	private final GridToExcelExportType exportType;

	@InputParam
	public GridToExcelExportType getExportType() {
		return exportType;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LyraGridExcelExportCommand(final GridContext aContext,
			final DataPanelElementInfo aElInfo, final GridToExcelExportType aExportType) {
		super(aContext, aElInfo);
		exportType = aExportType;
	}

	@InputParam
	@Override
	public LyraGridContext getContext() {
		return (LyraGridContext) super.getContext();
	}

	@Override
	protected void preProcess() {
		super.preProcess();

		// if (exportType == GridToExcelExportType.ALL) {
		// getContext().resetForReturnAllRecords();
		// }

		initCommandContext();
	}

	@Override
	protected void mainProc() throws Exception {

		if (exportType == GridToExcelExportType.CURRENTPAGE) {

			LyraGridMetaFactory lyraGridMetaFactory =
				new LyraGridMetaFactory(getContext(), getElementInfo());
			LyraGridMetadata gm = lyraGridMetaFactory.buildMetadata();

			LyraGridDataFactory lyraGridDataFactory =
				new LyraGridDataFactory(getContext(), getElementInfo());
			List<HashMap<String, String>> records = lyraGridDataFactory.exportExcelCurrentPage();

			GridToExcelXMLFactory factory = new GridToExcelXMLFactory(gm.getColumns(), records);
			org.w3c.dom.Document xml = factory.build();
			ByteArrayOutputStream stream = XMLUtils.xsltTransformForGrid(xml);
			setResult(new ExcelFile(stream, getContext().getFileName(), "xls"));

		} else {

			LyraGridDataFactory lyraGridDataFactory =
				new LyraGridDataFactory(getContext(), getElementInfo());
			ByteArrayOutputStream stream = lyraGridDataFactory.exportExcelAll();
			setResult(new ExcelFile(stream, getContext().getFileName(), "xlsx"));

		}

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
