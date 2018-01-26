package ru.curs.showcase.app.server;

import java.io.*;
import java.net.URLEncoder;

import org.apache.commons.fileupload.FileUploadException;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.*;

/**
 * Базовый обработчик для сервлетов, предназначенных для передачи файлов на
 * клиента.
 * 
 * @author den
 * 
 */
public abstract class AbstractDownloadHandler extends AbstractFilesHandler {
	/**
	 * Создаваемый файл.
	 */
	private OutputStreamDataFile outputFile;

	/**
	 * Особенности: 1) Перекодирование нужно для корректной передачи русских
	 * символов в имени файла. Работает в IE, Chrome и Opera, не работает в
	 * Firefox и Safari. На английские символы перекодировка не влияет. 2)
	 * encoding для response ставим для единообразия - везде UTF-8
	 * 
	 * @see ru.curs.showcase.app.server.AbstractFilesHandler#fillResponse()
	 **/
	@Override
	protected void fillResponse() throws IOException {
		String encName = URLEncoder.encode(outputFile.getName(), TextUtils.DEF_ENCODING);
		encName = encName.replaceAll("\\+", "%20");
		setContentType();
		getResponse().setCharacterEncoding(TextUtils.DEF_ENCODING);

		getResponse().setHeader("Content-Disposition",
				String.format("attachment; filename*=UTF-8''%s", encName));

		try (OutputStream out = getResponse().getOutputStream()) {
			out.write(outputFile.getData().toByteArray());
		}
	}

	/**
	 * По агентурным данным для старых версий IE "application/octet-stream"
	 * обрабатывается некорректно.
	 */
	protected void setContentType() {
		if (ServletUtils.isOldIE(getRequest())) {
			getResponse().setContentType("application/force-download");
		} else {
			getResponse().setContentType("application/octet-stream");
		}
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		setContext((CompositeContext) deserializeObject(getParam(getContextClass())));
		setElementInfo(
				(DataPanelElementInfo) deserializeObject(getParam(DataPanelElementInfo.class)));
	}

	protected Class<? extends CompositeContext> getContextClass() {
		return CompositeContext.class;
	}

	/**
	 * Получить значение параметра по классу объекта. Имя параметра должно
	 * совпадать с именем класса.
	 * 
	 * @param paramClass
	 *            - класс.
	 * @return - значение параметра.
	 */
	@SuppressWarnings("rawtypes")
	protected String getParam(final Class paramClass) {
		String paramName = paramClass.getName();
		String result = getRequest().getParameter(paramName);
		if (result == null) {
			throw new HTTPRequestRequiredParamAbsentException(paramName);
		}
		return result;
	}

	public OutputStreamDataFile getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(final OutputStreamDataFile aOutputFile) {
		outputFile = aOutputFile;
	}
}
