package ru.curs.showcase.app.client;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.html.XForm;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.app.client.api.ActionExecuter;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.WebUtils;

/**
 * Класс селектора.
 */
public final class JSSelector {

	private static final String JSSELECTOR_DESERIALIZATION_ERROR =
		CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
				"An error occurred while deserializing an object");

	private static SerializationStreamFactory ssf = null;
	private static SerializationStreamFactory addSSF = null;

	private JSSelector() {
	}

	// CHECKSTYLE:OFF
	public static String pluginGetHttpParams(final String offset, final String limit,
			final String searchString, final String startsWith, final String xformId,
			final String generalFilters, final String procName) {

		DataRequest req = new DataRequest();

		req.setParams(generalFilters);
		req.setProcName(procName);

		XFormPanel currentXFormPanel = (XFormPanel) ActionExecuter.getElementPanelById(xformId);
		SelectorAdditionalData addData = new SelectorAdditionalData();
		addData.setContext(currentXFormPanel.getContext());
		addData.setElementInfo(currentXFormPanel.getElementInfo());
		req.setAddData(addData);

		req.setStartsWith(Boolean.valueOf(startsWith));
		req.setCurValue(searchString);
		req.setFirstRecord(Integer.valueOf(offset));
		req.setRecordCount(Integer.valueOf(limit));

		if (addSSF == null) {
			addSSF = WebUtils.createAddGWTSerializer();
		}

		SerializationStreamWriter writer = addSSF.createStreamWriter();
		try {
			writer.writeObject(req);
		} catch (SerializationException e) {
			return CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"Error during serialization parameters for Http-request plug.") + ": "
					+ e.getMessage();
		}
		return writer.toString();
	}
	// CHECKSTYLE:ON

	public static String pluginGetLocalizedParams(final String xformId) {

		com.google.gwt.json.client.JSONObject localizedParams =
			new com.google.gwt.json.client.JSONObject();

		localizedParams.put("startWithTitle", new JSONString(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "Starts with (Ctrl+B)")));
		localizedParams.put("cancelTitle", new JSONString(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "Cancel")));

		localizedParams.put("loadingMessage", new JSONString(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "Loading...")));
		localizedParams.put("noDataMessage", new JSONString(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "No records")));

		localizedParams.put("selectedNotFound", new JSONString(CourseClientLocalization
				.gettext(AppCurrContext.getInstance().getDomain(), "Not found")));

		XFormPanel currentXFormPanel = (XFormPanel) ActionExecuter.getElementPanelById(xformId);
		localizedParams.put("subformId",
				new JSONString(((XForm) currentXFormPanel.getElement()).getSubformId()));

		return localizedParams.toString();

	}

	private static String replaceServiceSymbols(final String mess) {
		String ret = mess;
		ret = ret.replace(ExchangeConstants.OK_MESSAGE_X, "\\x");
		ret = ret.replace(ExchangeConstants.OK_MESSAGE_QUOT, "\\\"");
		return ret;
	}

	public static void pluginShowErrorMessage(final String stringMessage) {
		if (!stringMessage.isEmpty()) {
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
		}
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
						JSSELECTOR_DESERIALIZATION_ERROR + " UserMessage: " + e.getMessage());
			}
		}

	}

	public static void pluginShowTextMessage(final String stringMessage) {
		if (!stringMessage.isEmpty()) {
			MessageBox
					.showMessageWithDetails(
							CourseClientLocalization
									.gettext(AppCurrContext.getInstance().getDomain(), "Message"),
							stringMessage, "", MessageType.INFO, false, null);
		}
	}

	// CHECKSTYLE:OFF
	public static String pluginTreeGetHttpParams(final String searchString,
			final String startsWith, final String xformId, final String generalFilters,
			final String getDataProcName, final String parentId, final String parentName) {

		XFormPanel currentXFormPanel = (XFormPanel) ActionExecuter.getElementPanelById(xformId);

		TreeDataRequest requestData = new TreeDataRequest();

		requestData.setContext(currentXFormPanel.getContext());

		PluginInfo elInfo = new PluginInfo();
		elInfo.setId(currentXFormPanel.getElementInfo().getId());
		elInfo.setGetDataProcName(getDataProcName);
		requestData.setElInfo(elInfo);

		requestData.setParams(generalFilters);
		requestData.setCurValue(searchString);
		requestData.setStartsWith(Boolean.valueOf(startsWith));
		requestData.setParentId(parentId);

		// -------------------------------------------------

		if (addSSF == null) {
			addSSF = WebUtils.createAddGWTSerializer();
		}

		SerializationStreamWriter writer = addSSF.createStreamWriter();
		try {
			writer.writeObject(requestData);
		} catch (SerializationException e) {
			return CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"Error during serialization parameters for Http-request plug.") + ": "
					+ e.getMessage();
		}

		return writer.toString();
	}
	// CHECKSTYLE:ON

}
