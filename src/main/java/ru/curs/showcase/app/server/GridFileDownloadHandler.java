package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.core.grid.GridFileDownloadCommand;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из грида. TODO: подумать над тем, чтобы
 * возвращать linkId и recordId в GridContext.
 * 
 */
public class GridFileDownloadHandler extends AbstractDownloadHandler {

	/**
	 * Ссылка на хранимую процедуру получения файла.
	 */
	private ID linkId;

	/**
	 * Идентификатор записи грида для скачивания файла.
	 */
	private String recordId;

	@Override
	protected void processFiles() {
		GridFileDownloadCommand command =
			new GridFileDownloadCommand(getContext(), getElementInfo(), linkId, recordId);
		setOutputFile(command.execute());
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		linkId = new ID(getRequest().getParameter("linkId"));
		if (linkId == null) {
			throw new HTTPRequestRequiredParamAbsentException("linkId");
		}
		recordId = getRequest().getParameter("recordId");
		if (recordId == null) {
			throw new HTTPRequestRequiredParamAbsentException("recordId");
		}
	}

	public ID getLinkId() {
		return linkId;
	}

	public String getRecordId() {
		return recordId;
	}

}
