package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.GridExcelExportCommand;

/**
 * Обработчик запроса на получение Excel файла по данным в гриде. Вызывается из
 * сервлета. Отдельный класс (и один объект на один запрос) нужны для того,
 * чтобы избежать проблем многопоточности.
 * 
 */
public class GridToExcelHandler extends AbstractDownloadHandler {
	/**
	 * Тип экспорта в Excel.
	 */
	private GridToExcelExportType exportType;

	protected GridToExcelExportType getExportType() {
		return exportType;
	}

	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected Class<? extends CompositeContext> getContextClass() {
		return GridContext.class;
	}

	@Override
	protected void processFiles() {
		GridExcelExportCommand command =
			new GridExcelExportCommand(getContext(), getElementInfo(), exportType);
		setOutputFile(command.execute());
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		exportType = GridToExcelExportType.valueOf(getParam(GridToExcelExportType.class));
	}

	@Override
	protected void setContentType() {
		getResponse().setContentType("application/vnd.ms-excel");
	}
}