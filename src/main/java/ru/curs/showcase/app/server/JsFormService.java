package ru.curs.showcase.app.server;

import java.io.*;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.html.jsForm.*;
import ru.curs.showcase.util.ServletUtils;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Сервлет обрабатывающий запросы элемента jsForm.
 * 
 * @author bogatov
 *
 */
public class JsFormService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action == null) {
			throw new HTTPRequestRequiredParamAbsentException("action");
		}
		String serialisedContext = req.getParameter("contex");
		if (serialisedContext == null) {
			throw new HTTPRequestRequiredParamAbsentException("contex");
		}
		String serialisedElInfo = req.getParameter("elementInfo");
		if (serialisedElInfo == null) {
			throw new HTTPRequestRequiredParamAbsentException("elementInfo");
		}

		CompositeContext context = null;
		DataPanelElementInfo elInfo = null;
		try {
			context = (CompositeContext) ServletUtils.deserializeObject(serialisedContext);
			elInfo = (DataPanelElementInfo) ServletUtils.deserializeObject(serialisedElInfo);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		switch (action) {
		case "template":
			getTemplateData(resp, context, elInfo);
			break;
		case "submit":
			String procId = req.getParameter("procId");
			if (procId == null) {
				throw new HTTPRequestRequiredParamAbsentException("procId");
			}
			String data = req.getParameter("data");
			submitData(resp, context, elInfo, procId, data);
			break;
		default:
			throw GeneralExceptionFactory.build(new UnsupportedOperationException("The action \""
					+ action + "\" is not supported."));
		}

	}

	private void getTemplateData(final HttpServletResponse resp, final CompositeContext context,
			final DataPanelElementInfo elInfo) throws IOException {
		JsFormTemplateGetCommand command = new JsFormTemplateGetCommand(context, elInfo);
		JsForm jsForm = command.execute();

		JSONObject obj = new JSONObject();
		obj.put("template", jsForm.getTemplate());
		List<HTMLEvent> events = jsForm.getEventManager().getEvents();
		if (events != null && !events.isEmpty()) {
			try {
				String stringEvents =
					com.google.gwt.user.server.rpc.RPC.encodeResponseForSuccess(
							FakeService.class.getMethod("serializeEvents"), events);
				obj.put("events", stringEvents);
			} catch (SerializationException | NoSuchMethodException e) {
				throw GeneralExceptionFactory.build(e);
			}
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setCharacterEncoding("UTF-8");
		try (PrintWriter writer = resp.getWriter()) {
			writer.print(obj.toJSONString());
		}
	}

	private void submitData(final HttpServletResponse resp, final CompositeContext context,
			final DataPanelElementInfo elInfo, final String procId, final String inData)
			throws IOException {
		ID linkId = new ID(procId);
		JsFormSubmitCommand command = new JsFormSubmitCommand(context, elInfo, linkId, inData);
		String outData = command.execute();

		JSONObject obj = new JSONObject();
		obj.put("data", outData);

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setCharacterEncoding("UTF-8");
		try (PrintWriter writer = resp.getWriter()) {
			writer.print(obj.toJSONString());
		}
	}
}
