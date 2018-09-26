package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import ru.curs.showcase.app.api.BrowserType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Сервлет работы с данными для JSLyraGrid'ов.
 * 
 */
public class JSLyraGridService extends HttpServlet {
	private static final long serialVersionUID = -7092195759008723860L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	private static final String ERROR_MES = "Сообщение об ошибке";
	private static final Logger LOGGER = LoggerFactory.getLogger(JSLyraGridService.class);

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		try {

			getData(request, response);

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

	private void getData(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

		String stringLyraGridContext = hreq.getParameter(LyraGridContext.class.getName());
		if (stringLyraGridContext == null) {
			throw new HTTPRequestRequiredParamAbsentException(LyraGridContext.class.getName());
		}
		String stringElementInfo = hreq.getParameter(DataPanelElementInfo.class.getName());
		if (stringElementInfo == null) {
			throw new HTTPRequestRequiredParamAbsentException(
					DataPanelElementInfo.class.getName());
		}

		LyraGridContext context = null;
		DataPanelElementInfo element = null;
		try {
			context = (LyraGridContext) ServletUtils.deserializeObject(stringLyraGridContext);
			element = (DataPanelElementInfo) ServletUtils.deserializeObject(stringElementInfo);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		try {
			GridUtils.fillFilterContextByFilterInfo(context);
		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}

		LyraGridDataGetCommand command = new LyraGridDataGetCommand(context, element);
		GridData gridData = command.execute();

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------

		int totalCount = context.getLiveInfo().getTotalCount();
		int firstIndex = context.getLiveInfo().getOffset();
		int lastIndex = context.getLiveInfo().getOffset() + context.getLiveInfo().getLimit() - 1;

		hresp.setHeader("Content-Range", "items " + String.valueOf(firstIndex) + "-"
				+ String.valueOf(lastIndex) + "/" + String.valueOf(totalCount));

		try (PrintWriter writer = hresp.getWriter()) {
			writer.print(gridData.getData());
		}

	}

}
