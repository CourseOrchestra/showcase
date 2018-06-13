package ru.curs.showcase.app.server;

import java.io.*;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.*;

/**
 * Сервлет для работы с JSLyraVueGrid'ами.
 * 
 */
public class JSLyraVueGridService extends HttpServlet {
	private static final long serialVersionUID = 2743412023490799202L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	private static final String FORMCLASS = "formClass";
	private static final String INSTANCEID = "instanceId";

	private static final String MAINCONTEXT = "mainContext";
	private static final String ADDCONTEXT = "addContext";

	private static final String USERDATA = "userdata";

	@Override
	public void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		if (hreq.getServletPath().toLowerCase().contains("JSLyraVueGridMeta".toLowerCase())) {
			getMetaData(hreq, hresp);
		} else {
			getData(hreq, hresp);
		}

	}

	private void getMetaData(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

		String formClass = hreq.getParameter(FORMCLASS);
		if (formClass == null) {
			throw new HTTPRequestRequiredParamAbsentException(FORMCLASS);
		}

		String instanceId = hreq.getParameter(INSTANCEID);
		if (instanceId == null) {
			throw new HTTPRequestRequiredParamAbsentException(INSTANCEID);
		}

		String mainContext = hreq.getParameter(MAINCONTEXT);
		String addContext = hreq.getParameter(ADDCONTEXT);

		String userdata = hreq.getParameter(USERDATA);

		LyraGridContext context = new LyraGridContext();
		context.setMain(mainContext);
		context.setAdditional(addContext);

		if (userdata != null) {
			ArrayList<String> al = new ArrayList<String>(1);
			al.add(userdata);
			context.getSessionParamsMap().put(USERDATA, al);
		}

		DataPanelElementInfo elInfo = new DataPanelElementInfo();
		elInfo.setProcName(formClass);
		elInfo.setId(instanceId);
		elInfo.setType(DataPanelElementType.GRID);
		elInfo.setSubtype(DataPanelElementSubType.JS_LYRA_GRID);

		LyraVueGridMetadataGetCommand command = new LyraVueGridMetadataGetCommand(context, elInfo);
		GridMeta metadata = command.execute();

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------

		try (PrintWriter writer = hresp.getWriter()) {
			writer.print(metadata.getMeta());
		}

	}

	private void getData(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

		String formClass = hreq.getParameter(FORMCLASS);
		if (formClass == null) {
			throw new HTTPRequestRequiredParamAbsentException(FORMCLASS);
		}

		String instanceId = hreq.getParameter(INSTANCEID);
		if (instanceId == null) {
			throw new HTTPRequestRequiredParamAbsentException(INSTANCEID);
		}

		String stringOffset = hreq.getParameter("offset");
		if (stringOffset == null) {
			throw new HTTPRequestRequiredParamAbsentException("offset");
		}

		String stringLimit = hreq.getParameter("limit");
		if (stringLimit == null) {
			throw new HTTPRequestRequiredParamAbsentException("limit");
		}

		String stringDgridOldPosition = hreq.getParameter("dgridOldPosition");
		if (stringDgridOldPosition == null) {
			throw new HTTPRequestRequiredParamAbsentException("dgridOldPosition");
		}

		String refreshId = hreq.getParameter("refreshId");
		String sortingOrFilteringChanged = hreq.getParameter("sortingOrFilteringChanged");
		String firstLoading = hreq.getParameter("firstLoading");

		String mainContext = hreq.getParameter(MAINCONTEXT);
		String addContext = hreq.getParameter(ADDCONTEXT);

		String userdata = hreq.getParameter(USERDATA);

		LyraGridContext context = new LyraGridContext();
		context.setMain(mainContext);
		context.setAdditional(addContext);

		context.getLiveInfo().setOffset(Integer.valueOf(stringOffset));
		context.getLiveInfo().setLimit(Integer.valueOf(stringLimit));
		context.setDgridOldPosition(Integer.valueOf(stringDgridOldPosition));

		if ("true".equalsIgnoreCase(sortingOrFilteringChanged)) {
			context.setExternalSortingOrFilteringChanged(true);
		}

		context.setRefreshId(refreshId);

		if ("true".equalsIgnoreCase(firstLoading)) {
			context.setIsFirstLoad(true);
		}

		if (userdata != null) {
			ArrayList<String> al = new ArrayList<String>(1);
			al.add(userdata);
			context.getSessionParamsMap().put(USERDATA, al);
		}

		DataPanelElementInfo elInfo = new DataPanelElementInfo();
		elInfo.setProcName(formClass);
		elInfo.setId(instanceId);
		elInfo.setType(DataPanelElementType.GRID);
		elInfo.setSubtype(DataPanelElementSubType.JS_LYRA_GRID);

		LyraVueGridDataGetCommand command = new LyraVueGridDataGetCommand(context, elInfo);
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
