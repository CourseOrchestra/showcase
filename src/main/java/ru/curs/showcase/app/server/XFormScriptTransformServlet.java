package ru.curs.showcase.app.server;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.util.ServletUtils;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

/**
 * Сервлет, обрабатывающий SQL и Jython submission из XForms. Является
 * универсальным обработчиком для не XSL преобразований XForm.
 */
public class XFormScriptTransformServlet extends HttpServlet {

	public static final String PROC_PARAM = "proc";

	private static final long serialVersionUID = -1387485389229827545L;

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse response)
			throws ServletException, IOException {
		String procName = req.getParameter(PROC_PARAM);
		if (procName == null) {
			throw new HTTPRequestRequiredParamAbsentException(PROC_PARAM);
		}

		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(req);
		params.remove(PROC_PARAM);
		String content = ServletUtils.getRequestAsString(req);

		int indContextBegin = content.indexOf(ExchangeConstants.CONTEXT_BEGIN);
		int indContextEnd = content.indexOf(ExchangeConstants.CONTEXT_END);

		String stringContext =
			content.substring(indContextBegin + ExchangeConstants.CONTEXT_BEGIN.length(),
					indContextEnd);

		content = content.substring(0, indContextBegin);

		CompositeContext cnt = null;
		try {
			cnt = (CompositeContext) ServletUtils.deserializeObject(stringContext);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		XFormContext context = new XFormContext(cnt, content);
		DataPanelElementInfo elInfo = XFormInfoFactory.generateXFormsSQLSubmissionInfo(procName);

		XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
		String res = command.execute();

		if (context.getOkMessage() != null) {
			try {
				String stringOkMessage =
					ExchangeConstants.OK_MESSAGE_BEGIN
							+ RPC.encodeResponseForSuccess(
									FakeService.class.getMethod("serializeUserMessage"),
									context.getOkMessage()) + ExchangeConstants.OK_MESSAGE_END;
				res = res + stringOkMessage;
			} catch (SerializationException | NoSuchMethodException | SecurityException e) {
				throw GeneralExceptionFactory.build(e);
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeResponseFromString(response, res);
	}
}
