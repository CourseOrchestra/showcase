package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.util.ServletUtils;

/**
 * Сервлет работы с данными для JSLyraGrid'ов.
 * 
 */
public class JSLyraGridService extends HttpServlet {
	private static final long serialVersionUID = -7092195759008723860L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	@Override
	public void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		getData(hreq, hresp);

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
