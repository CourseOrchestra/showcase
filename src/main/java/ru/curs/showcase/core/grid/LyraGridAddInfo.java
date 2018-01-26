package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.grid.GridToExcelExportType;

/**
 * Класс для хранения дополнительной информации, необходимой для подключения
 * лирагрид.
 * 
 */
public class LyraGridAddInfo {

	private int lyraOldPosition = 0;
	private int dgridOldTotalCount = 0;

	private GridToExcelExportType excelExportType = null;

	private boolean needRecreateWebsocket = false;

	public int getLyraOldPosition() {
		return lyraOldPosition;
	}

	public void setLyraOldPosition(final int aLyraOldPosition) {
		lyraOldPosition = aLyraOldPosition;
	}

	public int getDgridOldTotalCount() {
		return dgridOldTotalCount;
	}

	public void setDgridOldTotalCount(final int aDgridOldTotalCount) {
		dgridOldTotalCount = aDgridOldTotalCount;
	}

	protected GridToExcelExportType getExcelExportType() {
		return excelExportType;
	}

	protected void setExcelExportType(final GridToExcelExportType aExcelExportType) {
		excelExportType = aExcelExportType;
	}

	public boolean isNeedRecreateWebsocket() {
		return needRecreateWebsocket;
	}

	public void setNeedRecreateWebsocket(final boolean aNeedRecreateWebsocket) {
		needRecreateWebsocket = aNeedRecreateWebsocket;
	}

}
