package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.*;
import org.xml.sax.SAXException;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.selector.TreeDataRequest;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.GridUtils;
import ru.curs.showcase.core.selector.*;
import ru.curs.showcase.util.*;

/**
 * Сервлет работы с данными для триселекторов.
 * 
 */
public class JSTreeSelectorService extends HttpServlet {
	private static final long serialVersionUID = -3021829449521409288L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	private static final String FIELD_MESSAGE = "message_D13k82F9g7";

	private static final String FIELD_PARENT_ID = "parentId_D13k82F9g7";

	@Override
	public void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		String parent = hreq.getParameter("parent");
		if (parent == null) {
			throw new HTTPRequestRequiredParamAbsentException("parent");
		}

		String stringRequestData = hreq.getParameter("TreeDataRequest");
		if (stringRequestData == null) {
			throw new HTTPRequestRequiredParamAbsentException("TreeDataRequest");
		}

		TreeDataRequest requestData = null;
		try {
			requestData = (TreeDataRequest) ServletUtils.deserializeObject(stringRequestData);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		TreeSelectorGetCommand command = new TreeSelectorGetCommand(requestData);
		ResultTreeSelectorData result = command.execute();

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// -------------------------------------

		org.json.JSONArray data = null;
		try {
			String json = XMLJSONConverter.xmlToJson(result.getData(), false);
			if (!json.trim().isEmpty()) {
				JSONObject jo = new JSONObject(json);
				JSONObject items = jo.optJSONObject("items");
				if (items != null) {
					data = items.optJSONArray("item");
				}
			}
			if (data == null) {
				data = new org.json.JSONArray();
			}

			for (int i = 0; i < data.length(); i++) {
				org.json.JSONObject obj = (org.json.JSONObject) data.get(i);

				if (obj.isNull("hasChildren") && (!obj.isNull("leaf"))) {
					boolean leaf = obj.getBoolean("leaf");
					obj.put("hasChildren", !leaf);
				}

				obj.put(FIELD_PARENT_ID, parent);

				data.put(i, obj);
			}

			// ---------------------------------------------

			int totalCount = data.length();
			int firstIndex = 0;
			final int lastIndex = totalCount - 1;

			hresp.setHeader("Content-Range", "items " + String.valueOf(firstIndex) + "-"
					+ String.valueOf(lastIndex) + "/" + String.valueOf(totalCount));

			// ---------------------------------------------

			if ((result.getOkMessage() != null) && (data.length() > 0)) {
				data.put(0, ((org.json.JSONObject) data.get(0)).put(FIELD_MESSAGE,
						GridUtils.getSerializeUserMessage(result.getOkMessage())));
			}

			if ((data.length() > 0) || (result.getOkMessage() == null)) {
				try (PrintWriter writer = hresp.getWriter()) {
					writer.print(data.toString());
				}
			} else {
				if (result.getOkMessage() != null) {
					JSONObject obj = new JSONObject();
					obj.put(FIELD_MESSAGE,
							GridUtils.getSerializeUserMessage(result.getOkMessage()));
					try (PrintWriter writer = hresp.getWriter()) {
						writer.print(obj.toString());
					}
				}
			}

			// -------------------------------------

		} catch (SAXException | JSONException e) {
			throw GeneralExceptionFactory.build(e);
		}

	}

}
