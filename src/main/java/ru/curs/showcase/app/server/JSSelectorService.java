package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONArray;
import org.slf4j.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import ru.curs.showcase.app.api.BrowserType;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.GridUtils;
import ru.curs.showcase.core.selector.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Сервлет работы с данными для селекторов.
 * 
 */
public class JSSelectorService extends HttpServlet {
	private static final long serialVersionUID = -2872717652106441149L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	private static final String FIELD_MESSAGE = "message_D13k82F9g7";

	private static final String ERROR_MES = "Сообщение об ошибке";
	private static final Logger LOGGER = LoggerFactory.getLogger(JSSelectorService.class);

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		try {

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
					obj.put(FIELD_MESSAGE,
							GridUtils.getSerializeUserMessage(result.getOkMessage()));
					try (PrintWriter writer = hresp.getWriter()) {
						writer.print(obj.toJSONString());
					}
				}
			}

			// ---------------------------------------------

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
