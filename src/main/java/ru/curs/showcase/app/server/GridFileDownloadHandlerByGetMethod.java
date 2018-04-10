package ru.curs.showcase.app.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.commons.fileupload.FileUploadException;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.grid.GridFileDownloadCommand;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из грида. При помощи GET-метода в сервлете
 * 
 */
public class GridFileDownloadHandlerByGetMethod extends AbstractDownloadHandler {

	private static final String UTF8 = "UTF-8";

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

		CompositeContext context = new CompositeContext();

		String key;
		String value = getRequest().getParameter("perspective");
		if (value == null) {
			key = "userdata";
			value = getRequest().getParameter("userdata");
		} else {
			key = "perspective";
		}
		if (value != null) {
			value = URLDecoder.decode(value, UTF8);
			ArrayList<String> al = new ArrayList<String>(1);
			al.add(value);
			context.getSessionParamsMap().put(key, al);
		}

		setContext(context);

		DataPanelElementInfo elInfo = new DataPanelElementInfo();

		String elementId = getRequest().getParameter("elementId");
		if (elementId == null) {
			throw new HTTPRequestRequiredParamAbsentException("elementId");
		}
		elementId = URLDecoder.decode(elementId, UTF8);
		elInfo.setId(elementId);

		elInfo.setProcName("temp");
		elInfo.setType(DataPanelElementType.GRID);

		String stringLinkId = getRequest().getParameter("linkId");
		if (stringLinkId == null) {
			throw new HTTPRequestRequiredParamAbsentException("linkId");
		}
		stringLinkId = URLDecoder.decode(stringLinkId, UTF8);
		linkId = new ID(stringLinkId);

		String procName = getRequest().getParameter("procName");
		if (procName == null) {
			throw new HTTPRequestRequiredParamAbsentException("procName");
		}
		procName = URLDecoder.decode(procName, UTF8);

		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(linkId);
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		proc.setName(procName);
		elInfo.getProcs().put(linkId, proc);

		setElementInfo(elInfo);

		recordId = getRequest().getParameter("recordId");
		if (recordId == null) {
			throw new HTTPRequestRequiredParamAbsentException("recordId");
		}
		recordId = URLDecoder.decode(recordId, UTF8);

	}

}
