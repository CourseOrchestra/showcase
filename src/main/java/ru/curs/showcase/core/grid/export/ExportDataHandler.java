package ru.curs.showcase.core.grid.export;

import java.io.OutputStream;

import javax.servlet.http.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.util.TextUtils;

/**
 * Обработчик запроса на экспорт данных в Excel.
 * 
 * @author bogatov
 * 
 */
public class ExportDataHandler {
	private final String fileName = "data";

	public void handle(final HttpServletRequest request, final HttpServletResponse response) {
		try {
			DataPanelElementInfo eInfo = (DataPanelElementInfo) deserializeObject(
					getParam(request, DataPanelElementInfo.class));
			GridToExcelExportType gridToExcelExportType =
				GridToExcelExportType.valueOf(getParam(request, GridToExcelExportType.class));

			if (eInfo.getExportDataProc() == null
					|| GridToExcelExportType.CURRENTPAGE.equals(gridToExcelExportType)) {
				// Прежний функционал выгрузки в Excel
				if (eInfo.getSubtype() == DataPanelElementSubType.JS_LYRA_GRID) {
					LyraGridToExcelHandler handler = new LyraGridToExcelHandler();
					handler.handle(request, response);
				} else {
					GridToExcelHandler handler = new GridToExcelHandler();
					handler.handle(request, response);
				}
				return;
			}

			CompositeContext context =
				(CompositeContext) deserializeObject(getParam(request, getContextClass()));

			ExportType exportType = getFileType(request);

			response.setCharacterEncoding(TextUtils.DEF_ENCODING);
			if (ExportType.XLST.equals(exportType)) {
				response.setContentType("application/vnd.vnd.xlsx");
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + fileName + ".xlsx\"");
			} else {
				response.setContentType("text/csv");
				if (exportType == null) {
					response.setHeader("Content-Disposition",
							"attachment; filename=\"" + fileName + ".csv\"");
				}
			}

			processFiles(context, eInfo, response.getOutputStream(), exportType);
		} catch (Exception ex) {
			throw GeneralExceptionFactory.build(ex);
		}
	}

	protected void processFiles(final CompositeContext context, final DataPanelElementInfo eInfo,
			final OutputStream out, final ExportType exportType) {
		ExportDataCommand command = new ExportDataCommand(context, eInfo, out, exportType);
		command.execute();
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
	protected String getParam(final HttpServletRequest request, final Class paramClass) {
		String paramName = paramClass.getName();
		String result = request.getParameter(paramName);
		if (result == null) {
			throw new HTTPRequestRequiredParamAbsentException(paramName);
		}
		return result;
	}

	/**
	 * Функция десериализации объекта, переданного в теле запроса.
	 * 
	 * @param data
	 *            - строка с urlencoded объектом.
	 * @return - объект.
	 * @throws SerializationException
	 */
	protected Object deserializeObject(final String data) throws SerializationException {
		ServerSerializationStreamReader streamReader = new ServerSerializationStreamReader(
				Thread.currentThread().getContextClassLoader(), null);
		streamReader.prepareToRead(data);
		return streamReader.readObject();
	}

	protected Class<? extends CompositeContext> getContextClass() {
		return GridContext.class;
	}

	private ExportType getFileType(final HttpServletRequest request) {
		// TODO: Необходимо реализовать механизм выбора формата результирующего
		// файла
		return ExportType.XLST;
		// Выбор типа файла на основе запрашиваемого URL
		// String servletPath = request.getServletPath();
		// int lastIndex = servletPath.lastIndexOf(".");
		// if (lastIndex != -1) {
		// String fileType = servletPath.substring(lastIndex + 1);
		// if (fileType != null) {
		// return ExportType.valueOf(fileType.toUpperCase());
		// }
		// }
		// return null;
	}

}
