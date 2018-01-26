package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONArray;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.GridUtils;
import ru.curs.showcase.core.selector.*;
import ru.curs.showcase.util.ServletUtils;

/**
 * Сервлет работы с данными для селекторов.
 * 
 */
public class JSSelectorService extends HttpServlet {
	private static final long serialVersionUID = -2872717652106441149L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	private static final String FIELD_MESSAGE = "message_D13k82F9g7";

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		String stringDataRequest = hreq.getParameter("DataRequest");
		if (stringDataRequest == null) {
			throw new HTTPRequestRequiredParamAbsentException("DataRequest");
		}

		DataRequest req = null;
		try {
			req = (DataRequest) ServletUtils.deserializeObject(stringDataRequest);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		SelectorGetCommand command = new SelectorGetCommand(req);
		ResultSelectorData result = command.execute();

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		int totalCount = result.getCount();
		int firstIndex = req.getFirstRecord();
		int lastIndex = req.getFirstRecord() + req.getRecordCount() - 1;

		hresp.setHeader("Content-Range", "items " + String.valueOf(firstIndex) + "-"
				+ String.valueOf(lastIndex) + "/" + String.valueOf(totalCount));

		// ---------------------------------------------

		JSONArray data = new JSONArray();
		for (DataRecord dr : result.getDataRecordList()) {
			org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
			obj.put("id", dr.getId());
			obj.put("name", dr.getName());
			if (dr.getParameters() != null) {
				for (String key : dr.getParameters().keySet()) {
					obj.put(key, dr.getParameters().get(key));
				}
			}
			data.add(obj);
		}

		if ((result.getOkMessage() != null) && (data.size() > 0)) {
			((org.json.simple.JSONObject) data.get(0)).put(FIELD_MESSAGE,
					GridUtils.getSerializeUserMessage(result.getOkMessage()));
		}

		if ((data.size() > 0) || (result.getOkMessage() == null)) {
			try (PrintWriter writer = hresp.getWriter()) {
				writer.print(data.toJSONString());
			}
		} else {
			if (result.getOkMessage() != null) {
				org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
				obj.put(FIELD_MESSAGE, GridUtils.getSerializeUserMessage(result.getOkMessage()));
				try (PrintWriter writer = hresp.getWriter()) {
					writer.print(obj.toJSONString());
				}
			}
		}

		// ---------------------------------------------

	}

}
