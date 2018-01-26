package ru.curs.showcase.app.server;

import java.io.*;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.util.*;

/**
 * Сервлет работы с данными для JSGrid'ов.
 * 
 */
public class JSGridService extends HttpServlet {
	private static final long serialVersionUID = 350171574189068907L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	@Override
	public void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		String editor = hreq.getParameter("editor");
		if (editor == null) {
			getData(hreq, hresp);
		} else {
			if ("addRecord".equals(editor)) {
				addRecord(hreq, hresp);
			} else {
				saveData(hreq, hresp);
			}
		}
	}

	private void getData(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

		Date dt1 = new Date();

		String stringGridContext = hreq.getParameter(GridContext.class.getName());
		if (stringGridContext == null) {
			throw new HTTPRequestRequiredParamAbsentException(GridContext.class.getName());
		}
		String stringElementInfo = hreq.getParameter(DataPanelElementInfo.class.getName());
		if (stringElementInfo == null) {
			throw new HTTPRequestRequiredParamAbsentException(
					DataPanelElementInfo.class.getName());
		}

		GridContext context = null;
		DataPanelElementInfo element = null;
		try {
			context = (GridContext) ServletUtils.deserializeObject(stringGridContext);
			element = (DataPanelElementInfo) ServletUtils.deserializeObject(stringElementInfo);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		try {
			GridUtils.fillFilterContextByFilterInfo(context);
		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}

		GridDataGetCommand command = new GridDataGetCommand(context, element, true);
		GridData gridData = command.execute();

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------

		// Внимание! для JS_TREE_GRID будет
		// totalCount = gridData.getData().size();
		// firstIndex = 0;
		// lastIndex = totalCount - 1;

		int totalCount = context.getLiveInfo().getTotalCount();
		int firstIndex = context.getLiveInfo().getOffset();
		int lastIndex = context.getLiveInfo().getOffset() + context.getLiveInfo().getLimit() - 1;

		hresp.setHeader("Content-Range", "items " + String.valueOf(firstIndex) + "-"
				+ String.valueOf(lastIndex) + "/" + String.valueOf(totalCount));

		try (PrintWriter writer = hresp.getWriter()) {
			writer.print(gridData.getData());
		}

		Date dt2 = new Date();
		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2,
				element.getType().toString() + "_DATA", element.getSubtype().toString());

	}

	private void saveData(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

		String stringGridContext = hreq.getParameter(GridContext.class.getName());
		if (stringGridContext == null) {
			throw new HTTPRequestRequiredParamAbsentException(GridContext.class.getName());
		}
		String stringElementInfo = hreq.getParameter(DataPanelElementInfo.class.getName());
		if (stringElementInfo == null) {
			throw new HTTPRequestRequiredParamAbsentException(
					DataPanelElementInfo.class.getName());
		}

		GridContext context = null;
		DataPanelElementInfo element = null;
		try {
			context = (GridContext) ServletUtils.deserializeObject(stringGridContext);
			element = (DataPanelElementInfo) ServletUtils.deserializeObject(stringElementInfo);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		try {
			GridUtils.fillFilterContextByFilterInfo(context);
		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------

		String success = "1";
		UserMessage um = null;
		boolean refreshAfterSave = false;

		try {
			GridSaveDataCommand command = new GridSaveDataCommand(context, element);
			GridSaveResult gridSaveResult = command.execute();

			success = "1";
			if (gridSaveResult != null) {
				um = gridSaveResult.getOkMessage();
				if ((um != null) && (um.getType() == MessageType.ERROR)) {
					success = "0";
				}
				refreshAfterSave = gridSaveResult.isRefreshAfterSave();
			}

		} catch (Exception e) {
			success = "0";
			um = new UserMessage(e.getMessage(), MessageType.ERROR);
		}

		String message = GridUtils.getSerializeUserMessage(um);
		try (PrintWriter writer = hresp.getWriter()) {
			message = message.replace("\"", "'");
			writer.print("{\"success\":\"" + success + "\", \"message\":\"" + message
					+ "\", \"refreshAfterSave\":\"" + refreshAfterSave + "\"}");
		}

	}

	private void addRecord(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

		String stringGridContext = hreq.getParameter(GridContext.class.getName());
		if (stringGridContext == null) {
			throw new HTTPRequestRequiredParamAbsentException(GridContext.class.getName());
		}
		String stringElementInfo = hreq.getParameter(DataPanelElementInfo.class.getName());
		if (stringElementInfo == null) {
			throw new HTTPRequestRequiredParamAbsentException(
					DataPanelElementInfo.class.getName());
		}

		GridContext context = null;
		DataPanelElementInfo element = null;
		try {
			context = (GridContext) ServletUtils.deserializeObject(stringGridContext);
			element = (DataPanelElementInfo) ServletUtils.deserializeObject(stringElementInfo);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		try {
			GridUtils.fillFilterContextByFilterInfo(context);
		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------

		String success = "1";
		UserMessage um = null;

		try {
			GridAddRecordCommand command = new GridAddRecordCommand(context, element);
			GridAddRecordResult gridAddRecordResult = command.execute();

			success = "1";
			if (gridAddRecordResult != null) {
				um = gridAddRecordResult.getOkMessage();
				if ((um != null) && (um.getType() == MessageType.ERROR)) {
					success = "0";
				}
			}

		} catch (Exception e) {
			success = "0";
			um = new UserMessage(e.getMessage(), MessageType.ERROR);
		}

		String message = GridUtils.getSerializeUserMessage(um);
		try (PrintWriter writer = hresp.getWriter()) {
			message = message.replace("\"", "'");
			writer.print("{\"success\":\"" + success + "\", \"message\":\"" + message + "\"}");
		}

	}

}
