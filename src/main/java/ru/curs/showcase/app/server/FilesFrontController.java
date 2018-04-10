package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.core.grid.export.ExportDataHandler;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Front controller для работы с файлами.
 */
public final class FilesFrontController extends HttpServlet {

	private static final long serialVersionUID = 7991801050316249555L;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String servlet = request.getServletPath();
		servlet = servlet.replace("/" + ExchangeConstants.SECURED_SERVLET_PREFIX + "/", "")
				.toUpperCase();
		FilesFrontControllerAction action = null;
		try {
			action = FilesFrontControllerAction.valueOf(servlet);
		} catch (IllegalArgumentException e) {
			throw new HTTPRequestUnknownParamException(servlet);
		}
		AbstractFilesHandler handler = null;
		switch (action) {
		case DOWNLOAD:
			handler = new DownloadHandler();
			break;
		case GRIDTOEXCEL:
			ExportDataHandler edHandler = new ExportDataHandler();
			edHandler.handle(request, response);
			return;
		case GRIDFILEDOWNLOAD:
			handler = new GridFileDownloadHandler();
			break;
		case UPLOAD:
			handler = new UploadHandler();
			break;
		case GEOMAPEXPORT:
			handler = new GeoMapExportHandler();
			break;
		default:
			throw new NotImplementedYetException();
		}
		handler.handle(request, response);
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String servlet = request.getServletPath();
		servlet = servlet.replace("/" + ExchangeConstants.SECURED_SERVLET_PREFIX + "/", "")
				.toUpperCase();
		FilesFrontControllerAction action = null;
		try {
			action = FilesFrontControllerAction.valueOf(servlet);
		} catch (IllegalArgumentException e) {
			throw new HTTPRequestUnknownParamException(servlet);
		}
		AbstractFilesHandler handler = null;
		switch (action) {
		case GRIDFILEDOWNLOAD:
			handler = new GridFileDownloadHandlerByGetMethod();
			break;
		default:
			throw new NotImplementedYetException();
		}
		handler.handle(request, response);
	}

}
