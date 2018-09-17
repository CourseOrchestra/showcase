package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.export.ExportDataHandler;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Front controller для работы с файлами.
 */
public final class FilesFrontController extends HttpServlet {

	private static final long serialVersionUID = 7991801050316249555L;

	private static final String ERROR_MES = "Сообщение об ошибке";
	private static final Logger LOGGER = LoggerFactory.getLogger(FilesFrontController.class);

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		try {

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

		} catch (Exception e) {

			Throwable exc = e;
			if ((exc instanceof ServletException) && (exc.getCause() != null)) {
				exc = exc.getCause();
			}
			if (!(exc instanceof GeneralException) && !(exc instanceof BaseException)) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error(ERROR_MES, exc);
				}
			}

			String mess = null;
			if (request.getRequestURL().toString().toLowerCase()
					.contains("/secured/upload".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/submit".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/sqlTransform".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/jythonTransform".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/xslttransformer".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/xslTransform".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/JSGridService".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/JSSelectorService".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/JSTreeSelectorService".toLowerCase())
					|| request.getRequestURL().toString().toLowerCase()
							.contains("/secured/JSLyraGridService".toLowerCase())) {

				try {
					mess = RPC.encodeResponseForSuccess(
							FakeService.class.getMethod("serializeThrowable"), exc);
				} catch (NoSuchMethodException | SecurityException | SerializationException e1) {
					throw GeneralExceptionFactory.build(e1);
				}

			} else {

				mess = exc.getLocalizedMessage();

			}

			String userAgent = ServletUtils.getUserAgent(request);
			BrowserType browserType = BrowserType.detect(userAgent);
			if ((browserType == BrowserType.CHROME) || (browserType == BrowserType.FIREFOX)
					|| (browserType == BrowserType.IE)) {
				mess = "<root>" + mess + "</root>";
			}

			boolean needOKStatus = false;
			if ((browserType == BrowserType.IE) && request.getRequestURL().toString().toLowerCase()
					.contains("gridFileDownload".toLowerCase())) {
				needOKStatus = true;
			}

			ServletUtils.fillErrorResponce(response, mess, needOKStatus);

		}

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
