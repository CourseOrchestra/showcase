package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.http.*;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.util.ServletUtils;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Базовый обработчик для работы с файлами.
 * 
 * @author den
 * 
 */
public abstract class AbstractFilesHandler {
	/**
	 * Контекст.
	 */
	private CompositeContext context;
	/**
	 * Описание элемента.
	 */
	private DataPanelElementInfo elementInfo;

	/**
	 * Обрабатываемые HTTP запрос.
	 */
	private HttpServletRequest request;
	/**
	 * Формируемый HTTP ответ.
	 */
	private HttpServletResponse response;

	/**
	 * Основной метод обработчика.
	 * 
	 * @param aRequest
	 *            - запрос.
	 * @param aResponse
	 *            - ответ.
	 */
	public void handle(final HttpServletRequest aRequest, final HttpServletResponse aResponse) {
		request = aRequest;
		response = aResponse;

		try {
			handleTemplateMethod();
		} catch (SerializationException | IOException | FileUploadException e) {
			throw GeneralExceptionFactory.build(e);
		}
	}

	private void handleTemplateMethod() throws SerializationException, IOException,
			FileUploadException {
		getParams();
		processFiles();
		fillResponse();
	}

	/**
	 * Функция для заполнения response.
	 * 
	 * @throws IOException
	 */
	protected abstract void fillResponse() throws IOException;

	/**
	 * Функция считывания параметров запроса на скачивание.
	 * 
	 * @throws SerializationException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	protected abstract void getParams() throws SerializationException, FileUploadException,
			IOException;

	/**
	 * Функция получения файла.
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 */
	protected abstract void processFiles() throws UnsupportedEncodingException;

	/**
	 * Функция десериализации объекта, переданного в теле запроса.
	 * 
	 * @param data
	 *            - строка с urlencoded объектом.
	 * @return - объект.
	 * @throws SerializationException
	 */
	protected Object deserializeObject(final String data) throws SerializationException {
		return ServletUtils.deserializeObject(data);
	}

	public CompositeContext getContext() {
		return context;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setContext(final CompositeContext aContext) {
		this.context = aContext;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		this.elementInfo = aElementInfo;
	}

}