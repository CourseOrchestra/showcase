package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.*;
import org.slf4j.*;
import org.xml.sax.SAXException;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import ru.curs.showcase.app.api.BrowserType;
import ru.curs.showcase.app.api.selector.TreeDataRequest;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.GridUtils;
import ru.curs.showcase.core.selector.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Сервлет работы с данными для триселекторов.
 * 
 */
public class JSTreeSelectorService extends HttpServlet {
	private static final long serialVersionUID = -3021829449521409288L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	private static final String FIELD_MESSAGE = "message_D13k82F9g7";

	private static final String FIELD_PARENT_ID = "parentId_D13k82F9g7";

	private static final String ERROR_MES = "Сообщение об ошибке";
	private static final Logger LOGGER = LoggerFactory.getLogger(JSTreeSelectorService.class);

	@Override
	public void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		try {

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

				String stringData =
					result.getData().replaceAll("<item ", "<item toJsonArray=\"true\" ");

				String json = XMLJSONConverter.xmlToJson(stringData, false);
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

					org.json.JSONObject obj;
					if (data.get(i) instanceof org.json.JSONObject) {
						obj = (org.json.JSONObject) data.get(i);
					} else {
						obj = (org.json.JSONObject) ((org.json.JSONArray) data.get(i)).get(0);
					}

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

			} catch (SAXException | JSONException e) {
				throw GeneralExceptionFactory.build(e);
			}

			// -------------------------------------

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
			if (hreq.getRequestURL().toString().toLowerCase()
					.contains("/secured/upload".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/submit".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/sqlTransform".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/jythonTransform".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/xslttransformer".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/xslTransform".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/JSGridService".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/JSSelectorService".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
							.contains("/secured/JSTreeSelectorService".toLowerCase())
					|| hreq.getRequestURL().toString().toLowerCase()
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

			String userAgent = ServletUtils.getUserAgent(hreq);
			BrowserType browserType = BrowserType.detect(userAgent);
			if ((browserType == BrowserType.CHROME) || (browserType == BrowserType.FIREFOX)
					|| (browserType == BrowserType.IE)) {
				mess = "<root>" + mess + "</root>";
			}

			boolean needOKStatus = false;
			if ((browserType == BrowserType.IE) && hreq.getRequestURL().toString().toLowerCase()
					.contains("gridFileDownload".toLowerCase())) {
				needOKStatus = true;
			}

			ServletUtils.fillErrorResponce(hresp, mess, needOKStatus);

		}

	}

}
