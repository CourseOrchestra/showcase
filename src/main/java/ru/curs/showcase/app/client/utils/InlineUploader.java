package ru.curs.showcase.app.client.utils;

import java.util.Map.Entry;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.api.CompleteHandler;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.rpc.*;

/**
 * Класс для загрузки файлов на сервер прямо из XForm.
 * 
 * @author den
 * 
 */
public class InlineUploader {
	private static final String NAME_ATTR = "name";
	private static final String VALUE_ATTR = "value";

	private final String data;
	private final XFormPanel currentXFormPanel;

	private static int counter = 0;

	private boolean isAtLeastOneFileSelected = false;

	private static SerializationStreamFactory ssf = null;

	/**
	 * Обработчик окончания загрузки файлов.
	 */
	private static CompleteHandler submitHandler = null;

	public InlineUploader(final String aData, final XFormPanel aCurrentXFormPanel) {
		super();

		data = aData;
		currentXFormPanel = aCurrentXFormPanel;
	}

	public void checkForUpload(final CompleteHandler uplSubmitEndHandler) {
		submitHandler = uplSubmitEndHandler;
		DataPanelElementInfo dpei = currentXFormPanel.getElementInfo();

		counter = 0;
		isAtLeastOneFileSelected = false;

		for (Entry<ID, DataPanelElementProc> entry : dpei.getProcs().entrySet()) {
			if (entry.getValue().getType() == DataPanelElementProcType.UPLOAD) {
				JavaScriptObject form =
					getElementById(dpei.getUploaderId(entry.getKey().getString()));
				if (form != null) {
					submitInlineForm(dpei, form);

					submitAddedUploaders(entry.getKey().getString(), dpei, form);
				}
			}
		}

		if (!isAtLeastOneFileSelected) {
			if (submitHandler != null) {
				submitHandler.onComplete(false);
				submitHandler = null;
			}
		}

	}

	public void singleFormUpload(final String linkId) {
		submitHandler = null;
		DataPanelElementInfo dpei = currentXFormPanel.getElementInfo();

		JavaScriptObject form = getElementById(dpei.getUploaderId(linkId));
		if (form != null) {
			submitInlineForm(dpei, form);

			submitAddedUploaders(linkId, dpei, form);
		}

	}

	private void submitAddedUploaders(final String linkId, final DataPanelElementInfo dpei,
			final JavaScriptObject element) {
		FormElement form = (FormElement) FormElement.as(element);
		String lastAddingId = form.getAttribute("lastAddingId");
		if (!((lastAddingId == null) || "".equals(lastAddingId.trim()))) {
			int count = Integer.valueOf(lastAddingId);
			for (int i = 1; i <= count; i++) {
				JavaScriptObject addForm =
					getElementById(dpei.getUploaderId(linkId) + "_add_" + String.valueOf(i));
				if (addForm != null) {
					submitInlineForm(dpei, addForm);
				}
			}
		}
	}

	private void submitInlineForm(final DataPanelElementInfo dpei, final JavaScriptObject element) {
		boolean isFilesSelected = false;
		try {
			FormElement form = (FormElement) FormElement.as(element);
			SerializationStreamFactory ssf = WebUtils.createStdGWTSerializer();
			XFormContext context = new XFormContext(currentXFormPanel.getContext(), data);
			for (int i = 0; i < form.getElements().getLength(); i++) {
				Element el = form.getElements().getItem(i);
				if (XFormContext.class.getName().equals(el.getAttribute(NAME_ATTR))) {
					el.setAttribute(VALUE_ATTR, context.toParamForHttpPost(ssf));
					continue;
				}
				if (DataPanelElementInfo.class.getName().equals(el.getAttribute(NAME_ATTR))) {
					el.setAttribute(VALUE_ATTR, dpei.toParamForHttpPost(ssf));
					continue;
				}
				if ("file".equals(el.getAttribute("type"))) {
					isFilesSelected = isFilesSelected || isFilesSelected(el);
				}
			}

			if (isFilesSelected) {
				counter++;
				isAtLeastOneFileSelected = true;
				form.submit();

				String notClearUpload =
					form.getAttribute(ExchangeConstants.NOT_CLEAR_UPLOAD_TAG.toLowerCase());
				if ((notClearUpload == null) || notClearUpload.trim().isEmpty()) {
					clearForm(form);
				}
			}
		} catch (SerializationException e) {
			MessageBox.showSimpleMessage(
			// AppCurrContext.getInstance().getBundleMap().get("xforms_upload_error"),
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"File uploading error"), e.getMessage());
		}

	}

	private void clearForm(final FormElement form) {
		form.setInnerHTML(form.getInnerHTML());
	}

	private static native boolean isFilesSelected(final Element el)/*-{
		return el.value != "";
	}-*/;

	private static native JavaScriptObject getElementById(final String id) /*-{
		return $wnd.document.getElementById(id);
	}-*/;

	/**
	 * Возвращает "сериализатор" для gwt объектов.
	 * 
	 * @return - SerializationStreamFactory.
	 */
	private static SerializationStreamFactory getObjectSerializer() {
		if (ssf == null) {
			ssf = WebUtils.createStdGWTSerializer();
		}
		return ssf;
	}

	public static synchronized void onSubmitComplete(final String iframeName) {
		Boolean result = true;

		String mess = getErrorByIFrame(iframeName);
		if (mess != null) {
			if (mess.contains(ExchangeConstants.OK_MESSAGE_TEXT_BEGIN)
					&& mess.contains(ExchangeConstants.OK_MESSAGE_TEXT_END)) {
				result = true;

				String textMessage =
					mess.substring(mess.indexOf(ExchangeConstants.OK_MESSAGE_TEXT_BEGIN)
							+ ExchangeConstants.OK_MESSAGE_TEXT_BEGIN.length(),
							mess.lastIndexOf(ExchangeConstants.OK_MESSAGE_TEXT_END));

				String typeMessage =
					mess.substring(mess.indexOf(ExchangeConstants.OK_MESSAGE_TYPE_BEGIN)
							+ ExchangeConstants.OK_MESSAGE_TYPE_BEGIN.length(),
							mess.lastIndexOf(ExchangeConstants.OK_MESSAGE_TYPE_END));

				String captionMessage =
					mess.substring(mess.indexOf(ExchangeConstants.OK_MESSAGE_CAPTION_BEGIN)
							+ ExchangeConstants.OK_MESSAGE_CAPTION_BEGIN.length(),
							mess.lastIndexOf(ExchangeConstants.OK_MESSAGE_CAPTION_END));

				if ("null".equalsIgnoreCase(captionMessage)) {
					captionMessage =
					// AppCurrContext.getInstance().getBundleMap().get("okMessage");
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Message");
				}

				String subtypeMessage =
					mess.substring(mess.indexOf(ExchangeConstants.OK_MESSAGE_SUBTYPE_BEGIN)
							+ ExchangeConstants.OK_MESSAGE_SUBTYPE_BEGIN.length(),
							mess.lastIndexOf(ExchangeConstants.OK_MESSAGE_SUBTYPE_END));

				if ("null".equalsIgnoreCase(subtypeMessage)) {
					subtypeMessage = null;
				}

				MessageBox.showMessageWithDetails(captionMessage, textMessage, "",
						MessageType.valueOf(typeMessage), false, subtypeMessage);

			} else {
				result = false;
				try {
					Throwable caught =
						(Throwable) getObjectSerializer().createStreamReader(mess).readObject();

					WebUtils.onFailure(caught, "Error");

				} catch (SerializationException e) {
					MessageBox.showSimpleMessage("showErrorMessage()", "DeserializationError: "
							+ e.getMessage());
				}
			}
		}

		if (submitHandler != null) {

			// MessageBox.showSimpleMessage("", "Complete");

			counter--;
			if (counter == 0) {
				submitHandler.onComplete(result);
				submitHandler = null;
			}
		}
	}

	private static native String getErrorByIFrame(final String iframeName) /*-{
		return $wnd.getErrorByIFrame(iframeName);
	}-*/;

	public static void onChooseFiles(final String subformId, final String inputName,
			final String filenamesMapping, final Boolean needClearFilenames,
			final String addUploadIndex) {
		insertFilenamesByXPath(subformId, inputName, filenamesMapping, needClearFilenames,
				addUploadIndex);
	}

	private static native void insertFilenamesByXPath(final String subformId,
			final String inputName, final String filenamesMapping,
			final Boolean needClearFilenames, final String addUploadIndex) /*-{
		$wnd.insertFilenamesByXPath(subformId, inputName, filenamesMapping,
				needClearFilenames, addUploadIndex);
	}-*/;

}
