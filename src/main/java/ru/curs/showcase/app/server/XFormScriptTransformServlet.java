package ru.curs.showcase.app.server;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Сервлет, обрабатывающий SQL и Jython submission из XForms. Является
 * универсальным обработчиком для не XSL преобразований XForm.
 */
public class XFormScriptTransformServlet extends HttpServlet {

	public static final String PROC_PARAM = "proc";

	private static final long serialVersionUID = -1387485389229827545L;

	private static final String ERROR_MES = "Сообщение об ошибке";
	private static final Logger LOGGER =
		LoggerFactory.getLogger(XFormScriptTransformServlet.class);

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		try {

			String procName = request.getParameter(PROC_PARAM);
			if (procName == null) {
				throw new HTTPRequestRequiredParamAbsentException(PROC_PARAM);
			}

			Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(request);
			params.remove(PROC_PARAM);
			String content = ServletUtils.getRequestAsString(request);

			int indContextBegin = content.indexOf(ExchangeConstants.CONTEXT_BEGIN);
			int indContextEnd = content.indexOf(ExchangeConstants.CONTEXT_END);

			String stringContext = content.substring(
					indContextBegin + ExchangeConstants.CONTEXT_BEGIN.length(), indContextEnd);

			content = content.substring(0, indContextBegin);

			CompositeContext cnt = null;
			try {
				cnt = (CompositeContext) ServletUtils.deserializeObject(stringContext);
			} catch (SerializationException e) {
				throw GeneralExceptionFactory.build(e);
			}

			XFormContext context = new XFormContext(cnt, content);
			DataPanelElementInfo elInfo =
				XFormInfoFactory.generateXFormsSQLSubmissionInfo(procName);

			XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
			String res = command.execute();

			if (context.getOkMessage() != null) {
				try {
					String stringOkMessage =
						ExchangeConstants.OK_MESSAGE_BEGIN + RPC.encodeResponseForSuccess(
								FakeService.class.getMethod("serializeUserMessage"),
								context.getOkMessage()) + ExchangeConstants.OK_MESSAGE_END;
					res = res + stringOkMessage;
				} catch (SerializationException | NoSuchMethodException | SecurityException e) {
					throw GeneralExceptionFactory.build(e);
				}
			}

			response.setStatus(HttpServletResponse.SC_OK);
			ServletUtils.makeResponseFromString(response, res);

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
}
