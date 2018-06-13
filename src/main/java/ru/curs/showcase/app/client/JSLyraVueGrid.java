package ru.curs.showcase.app.client;

import java.util.ArrayList;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.FormPanel;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.*;

/**
 * Класс JSLyraVueGrid.
 */
public final class JSLyraVueGrid {

	private static final String JSLYRAVUE_DESERIALIZATION_ERROR =
		CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
				"An error occurred while deserializing an object");

	private static final String ERROR_WHEN_DOWNLOADING_FILE = "Error when downloading file";

	private static final String URL_FILE_DOWNLOAD = "/gridFileDownload";

	private static SerializationStreamFactory ssf = null;

	private JSLyraVueGrid() {
	}

	public static void init() {
		setCallbackJSNIFunction();

		loadLyraVue();
	}

	// CHECKSTYLE:OFF
	private static native void setCallbackJSNIFunction() /*-{
															$wnd.gwtLyraVueGridGetLocalizedParams = @ru.curs.showcase.app.client.JSLyraVueGrid::pluginGetLocalizedParams();
															$wnd.gwtLyraVueGridShowMessage = @ru.curs.showcase.app.client.JSLyraVueGrid::pluginShowMessage(Ljava/lang/String;);
															$wnd.gwtLyraVueGridShowErrorMessage = @ru.curs.showcase.app.client.JSLyraVueGrid::pluginShowErrorMessage(Ljava/lang/String;);
															$wnd.gwtLyraVueGridShowErrorTextMessage = @ru.curs.showcase.app.client.JSLyraVueGrid::pluginShowErrorTextMessage(Ljava/lang/String;);															
															$wnd.gwtLyraVueGridExportToClipboard = @ru.curs.showcase.app.client.JSLyraVueGrid::pluginExportToClipboard(Ljava/lang/String;);															
															$wnd.gwtLyraVueGridExportToExcel = @ru.curs.showcase.app.client.JSLyraVueGrid::pluginExportToExcel(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtProcessFileDownloadLyraVue = @ru.curs.showcase.app.client.JSLyraVueGrid::pluginProcessFileDownload(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															}-*/;
	// CHECKSTYLE:ON

	private static native void loadLyraVue() /*-{
		$wnd.safeIncludeJS("js/ui/grids/lyra-vue.js");
	}-*/;

	public static String pluginGetLocalizedParams() {

		com.google.gwt.json.client.JSONObject localizedParams =
			new com.google.gwt.json.client.JSONObject();

		localizedParams.put("loadingMessage", new JSONString(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "Loading...")));
		localizedParams.put("noDataMessage", new JSONString(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "No records")));

		return localizedParams.toString();

	}

	private static String replaceServiceSymbols(final String mess) {
		String ret = mess;
		ret = ret.replace(ExchangeConstants.OK_MESSAGE_X, "\\x");
		ret = ret.replace(ExchangeConstants.OK_MESSAGE_QUOT, "\\\"");
		return ret;
	}

	public static void pluginShowErrorMessage(final String stringMessage) {
		// if (!stringMessage.isEmpty()) {
		String mess = stringMessage.replace("<root>", "").replace("</root>", "");
		mess = replaceServiceSymbols(mess);
		try {

			if (ssf == null) {
				ssf = WebUtils.createStdGWTSerializer();
			}

			Throwable caught = (Throwable) ssf.createStreamReader(mess).readObject();

			WebUtils.onFailure(caught, "Error");

		} catch (SerializationException e) {
			MessageBox.showSimpleMessage("showErrorMessage()",
					"DeserializationError: " + e.getMessage());
		}
		// }
	}

	public static void pluginShowMessage(final String stringMessage) {

		if (!stringMessage.isEmpty()) {
			try {

				if (ssf == null) {
					ssf = WebUtils.createStdGWTSerializer();
				}

				UserMessage um = (UserMessage) ssf
						.createStreamReader(replaceServiceSymbols(stringMessage)).readObject();
				if (um != null) {

					String textMessage = um.getText();
					if ((textMessage == null) || textMessage.isEmpty()) {
						return;
					}

					MessageType typeMessage = um.getType();
					if (typeMessage == null) {
						typeMessage = MessageType.INFO;
					}

					String captionMessage = um.getCaption();
					if (captionMessage == null) {
						captionMessage = CourseClientLocalization
								.gettext(AppCurrContext.getInstance().getDomain(), "Message");
					}

					String subtypeMessage = um.getSubtype();

					MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage,
							false, subtypeMessage);

				}

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage("pluginShowMessage",
						JSLYRAVUE_DESERIALIZATION_ERROR + " UserMessage: " + e.getMessage());
			}
		}

	}

	public static void pluginShowErrorTextMessage(final String stringMessage) {

		MessageBox.showMessageWithDetails(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "Error"), stringMessage, "",
				MessageType.ERROR, false, null);

	}

	public static void pluginExportToClipboard(final String str) {
		ClipboardDialog cd = new ClipboardDialog(str);
		cd.center();
	}

	// CHECKSTYLE:OFF
	public static void pluginExportToExcel(final String formClass, final String instanceId,
			final String mainContext, final String addContext, final String refreshId,
			final String offset, final String limit, final String exportType,
			final String fileName, final String userdata) {

		LyraGridContext context = new LyraGridContext();
		context.setMain(mainContext);
		context.setAdditional(addContext);

		context.getLiveInfo().setOffset(Integer.valueOf(offset));
		context.getLiveInfo().setLimit(Integer.valueOf(limit));

		context.setRefreshId(refreshId);
		context.setFileName(fileName);

		if ((userdata != null) && (!userdata.isEmpty())) {
			ArrayList<String> al = new ArrayList<String>(1);
			al.add(userdata);
			context.getSessionParamsMap().put("userdata", al);
		}

		DataPanelElementInfo elInfo = new DataPanelElementInfo();
		elInfo.setProcName(formClass);
		elInfo.setId(instanceId);
		elInfo.setType(DataPanelElementType.GRID);
		elInfo.setSubtype(DataPanelElementSubType.JS_LYRA_VUE_GRID);

		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(CourseClientLocalization.gettext(
				AppCurrContext.getInstance().getDomain(), "Error when exporting to Excel"));

		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridToExcel");

		try {
			GridToExcelExportType excelExportType;
			if ("current".equalsIgnoreCase(exportType)) {
				excelExportType = GridToExcelExportType.CURRENTPAGE;
			} else {
				excelExportType = GridToExcelExportType.ALL;
			}

			dh.addParam(excelExportType.getClass().getName(), excelExportType.toString());

			if (ssf == null) {
				ssf = WebUtils.createStdGWTSerializer();
			}
			dh.addParam(context.getClass().getName(), context.toParamForHttpPost(ssf));
			dh.addParam(elInfo.getClass().getName(), elInfo.toParamForHttpPost(ssf));

			dh.submit();

		} catch (SerializationException e) {
			MessageBox.showSimpleMessage(
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"Error when exporting to Excel"),
					e.getMessage());
		}
	}
	// CHECKSTYLE:ON

	// CHECKSTYLE:OFF
	public static void pluginProcessFileDownload(final String formClass, final String instanceId,
			final String mainContext, final String addContext, final String recId,
			final String procName, final String downloadFileByGetMethod, final String userdata) {

		if ("true".equalsIgnoreCase(downloadFileByGetMethod)) {
			if (procName != null) {
				DownloadHelper dh = DownloadHelper.getInstance();
				dh.setEncoding(FormPanel.ENCODING_URLENCODED);
				dh.clear();

				dh.setErrorCaption(CourseClientLocalization.gettext(
						AppCurrContext.getInstance().getDomain(), ERROR_WHEN_DOWNLOADING_FILE));
				dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + URL_FILE_DOWNLOAD);

				if ((userdata != null) && (!userdata.isEmpty())) {
					dh.addParam("userdata", URL.encode(userdata));
				}
				dh.addParam("elementId", URL.encode(instanceId));
				dh.addParam("linkId", URL.encode("1"));
				dh.addParam("procName", URL.encode(procName));
				dh.addParam("recordId", URL.encode(recId));

				dh.setMethod(FormPanel.METHOD_GET);
				try {
					dh.submit();
				} finally {
					dh.setMethod(FormPanel.METHOD_POST);
				}

			}

		} else {

			if (procName != null) {
				DownloadHelper dh = DownloadHelper.getInstance();
				dh.setEncoding(FormPanel.ENCODING_URLENCODED);
				dh.clear();

				dh.setErrorCaption(CourseClientLocalization.gettext(
						AppCurrContext.getInstance().getDomain(), ERROR_WHEN_DOWNLOADING_FILE));
				dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + URL_FILE_DOWNLOAD);

				try {
					CompositeContext context = new CompositeContext();
					context.setMain(URL.decodeQueryString(mainContext));
					context.setAdditional(URL.decodeQueryString(addContext));

					if ((userdata != null) && (!userdata.isEmpty())) {
						ArrayList<String> al = new ArrayList<String>(1);
						al.add(userdata);
						context.getSessionParamsMap().put("userdata", al);
					}

					DataPanelElementInfo elInfo = new DataPanelElementInfo();
					elInfo.setProcName(formClass);
					elInfo.setId(instanceId);
					elInfo.setType(DataPanelElementType.GRID);
					elInfo.setSubtype(DataPanelElementSubType.JS_LYRA_VUE_GRID);

					DataPanelElementProc proc = new DataPanelElementProc();
					proc.setId("1");
					proc.setType(DataPanelElementProcType.DOWNLOAD);
					proc.setName(procName);
					elInfo.getProcs().put(new ID("1"), proc);

					dh.addParam("linkId", "1");

					if (ssf == null) {
						ssf = WebUtils.createStdGWTSerializer();
					}
					dh.addParam(context.getClass().getName(), context.toParamForHttpPost(ssf));
					dh.addParam(elInfo.getClass().getName(), elInfo.toParamForHttpPost(ssf));

					dh.addParam("recordId", recId);

					dh.submit();
				} catch (SerializationException e) {
					ru.curs.showcase.app.client.MessageBox
							.showSimpleMessage(CourseClientLocalization.gettext(
									AppCurrContext.getInstance().getDomain(),
									ERROR_WHEN_DOWNLOADING_FILE), e.getMessage());
				}
			}
		}
	}
	// CHECKSTYLE:ON

}
