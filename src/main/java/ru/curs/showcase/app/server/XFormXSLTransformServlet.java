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
 * Сервлет, обрабатывающий xslt-преобразование из XForms.
 */
public class XFormXSLTransformServlet extends HttpServlet {

	public static final String XSLTFILE_PARAM = "xsltfile";

	private static final long serialVersionUID = 382470453045525219L;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String xsltFile = request.getParameter(XSLTFILE_PARAM);
		if (xsltFile == null) {
			throw new HTTPRequestRequiredParamAbsentException(XSLTFILE_PARAM);
		}

		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(request);
		params.remove(XSLTFILE_PARAM);
		String content = ServletUtils.getRequestAsString(request);

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
		DataPanelElementInfo elInfo = XFormInfoFactory.generateXFormsTransformationInfo(xsltFile);

		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
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
