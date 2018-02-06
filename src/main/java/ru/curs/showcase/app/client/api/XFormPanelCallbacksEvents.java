package ru.curs.showcase.app.client.api;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.FormPanel;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.*;

/**
 * Класс, реализующий функции обратного вызова из XFormPanel.
 * 
 */
public final class XFormPanelCallbacksEvents {
	/**
	 * Тестовая XFormPanel.
	 */
	private static XFormPanel testXFormPanel = null;

	public static void setTestXFormPanel(final XFormPanel testXFormPanel1) {
		testXFormPanel = testXFormPanel1;
	}

	private static SerializationStreamFactory ssf = null;

	private XFormPanelCallbacksEvents() {

	}

	/**
	 * Функция, возвращаюшая сериализованный XFormContext.
	 * 
	 * @param eltid
	 *            Id подформы.
	 * 
	 */
	public static String getStringContext(final String eltid) {
		String stringContext = "";

		String xformId = eltid.trim();
		xformId = xformId.substring(0, xformId.length() - 1);

		XFormPanel currentXFormPanel = getCurrentPanel(xformId);
		if (currentXFormPanel != null) {
			try {
				stringContext = ExchangeConstants.CONTEXT_BEGIN
						+ currentXFormPanel.getContext().toParamForHttpPost(getObjectSerializer())
						+ ExchangeConstants.CONTEXT_END;
			} catch (SerializationException e) {
				MessageBox.showSimpleMessage("getStringContext()",
						" SerializationError: " + e.getMessage());
			}
		}

		return stringContext;
	}

	/**
	 * Функция, показывающая сообщение из сабмишена XForm.
	 * 
	 * @param stringMessage
	 *            сериализованное сообщение.
	 * 
	 */
	public static void showMessage(final String stringMessage) {
		if (!stringMessage.isEmpty()) {
			try {
				UserMessage um = (UserMessage) getObjectSerializer()
						.createStreamReader(stringMessage).readObject();
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
						captionMessage =
							// AppCurrContext.getInstance().getBundleMap().get("okMessage");
							CourseClientLocalization
									.gettext(AppCurrContext.getInstance().getDomain(), "Message");
					}

					String subtypeMessage = um.getSubtype();

					MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage,
							false, subtypeMessage);

				}

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage("showMessage()",
						"DeserializationError: " + e.getMessage());
			}
		}
	}

	/**
	 * Функция, показывающая сообщение об ошибке из сабмишена XForm.
	 * 
	 * @param stringMessage
	 *            сообщение об ошибке.
	 * 
	 */
	public static void showErrorMessage(final String stringMessage) {
		if (!stringMessage.isEmpty()) {
			String mess = stringMessage.replace("<root>", "").replace("</root>", "");
			try {
				Throwable caught =
					(Throwable) getObjectSerializer().createStreamReader(mess).readObject();

				WebUtils.onFailure(caught, "Error");

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage("showErrorMessage()",
						"DeserializationError: " + e.getMessage());
			}
		}
	}

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

	/**
	 * Функция, которая будет выполняться по клику на кнопку Сохранить в XForm.
	 * 
	 * @param xformId
	 *            - Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            - Данные xForm'ы
	 */
	public static void xFormPanelClickSave(final String xformId, final String linkId,
			final String data, final String elementId, final Boolean forceCloseWindow) {
		final XFormPanel curXFormPanel = getCurrentPanel(xformId);

		if (curXFormPanel != null) {

			// MessageBox.showSimpleMessage("xFormPanelClickSave. xformId=" +
			// xformId + ", linkId="
			// + linkId + ", forceCloseWindow=" + forceCloseWindow, data);

			final Action ac = getActionByLinkId(linkId, curXFormPanel);

			if (curXFormPanel.getElementInfo().getSaveProc() != null) {

				setEnableDisableState(elementId, false);

				curXFormPanel.getDataService().saveXForms(
						new XFormContext(curXFormPanel.getContext(), data),
						curXFormPanel.getElementInfo(),
						new GWTServiceCallback<VoidElement>(
								// AppCurrContext.getInstance().getBundleMap().get("xform_save_data_error"))
								// {
								CourseClientLocalization.gettext(
										AppCurrContext.getInstance().getDomain(),
										"Error when XForms data saving on server")) {
							// new
							// GWTServiceCallback<VoidElement>(AppCurrContext.getInstance()
							// .getInternationalizedMessages().xform_save_data_error())
							// {

							@Override
							public void onSuccess(final VoidElement result) {

								super.onSuccess(result);

								// setEnableDisableState(elementId, true);

								InlineUploader uploader = new InlineUploader(data, curXFormPanel);
								uploader.checkForUpload(new CompleteHandler() {

									@Override
									public void onComplete(final boolean aRes) {
										// MessageBox.showSimpleMessage("InlineUploaderComplete",
										// "aRes=" + String.valueOf(aRes));

										if (ac != null) {
											runAction(ac, curXFormPanel.getElement());
										} else {
											if (Boolean.parseBoolean(
													String.valueOf(forceCloseWindow))) {
												ActionExecuter.closeCurrentWindow();
											}
										}

										setEnableDisableState(elementId, true);
									}

								});

								if (curXFormPanel.getUw() != null) {
									submitUploadForm(data, curXFormPanel, ac);
								}
							}
						});
			} else {

				if (ac != null) {
					runAction(ac, curXFormPanel.getElement());
				} else {
					if (Boolean.parseBoolean(String.valueOf(forceCloseWindow))) {
						ActionExecuter.closeCurrentWindow();
					}
				}

			}
		}
	}

	private static void setEnableDisableState(final String elementId, final boolean state) {
		if (elementId == null) {
			return;
		}

		final com.google.gwt.user.client.Element el = DOM.getElementById(elementId);

		if (el == null) {
			return;
		}

		if (state) {
			Timer enabledTimer = new Timer() {
				@Override
				public void run() {
					el.getParentElement().removeAttribute("disabled");
				}
			};
			final int delay = 3000;
			enabledTimer.schedule(delay);
		} else {
			el.getParentElement().setAttribute("disabled", "");
		}
	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Загрузить в XForm.
	 * 
	 * @param xformId
	 *            - Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            - Данные xForm'ы
	 */
	public static void simpleUpload(final String xformId, final String linkId, final String data) {
		final XFormPanel curXFormPanel = getCurrentPanel(xformId);

		InlineUploader uploader = new InlineUploader(data, curXFormPanel);
		uploader.singleFormUpload(linkId);
	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Обновить в XForm.
	 * 
	 * @param xformId
	 *            Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void xFormPanelClickUpdate(final String xformId, final String linkId,
			final String overridenAddContext, final Boolean forceCloseWindow) {

		// MessageBox.showSimpleMessage("xFormPanelClickUpdate. xformId=" +
		// xformId + ", linkId="
		// + linkId, overridenAddContext);

		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {
			Action ac = getActionByLinkId(linkId, currentXFormPanel);
			if (ac != null) {
				currentXFormPanel.setLoadedInDOM(true);

				ac = ac.gwtClone();
				if (overridenAddContext != null) {
					ac.setAdditionalContext(overridenAddContext);
				}
				runAction(ac, currentXFormPanel.getElement());
			} else {
				if (Boolean.parseBoolean(String.valueOf(forceCloseWindow))) {
					ActionExecuter.closeCurrentWindow();
				}
			}
		}
	}

	private static void runAction(final Action ac, final DataPanelElement aElement) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, aElement);
			ActionExecuter.execAction();
		}
	}

	private static Action getActionByLinkId(final String linkId,
			final XFormPanel currentXFormPanel) {
		Action ac = null;

		List<HTMLEvent> events =
			((XForm) currentXFormPanel.getElement()).getEventManager().getEventForLink(linkId);
		// TODO сделал для простоты т.к. сейчас для xforms не может вернутся
		// более 1 события
		if (events.size() > 0) {
			ac = events.get(0).getAction();
		}
		return ac;
	}

	private static void submitUploadForm(final String data, final XFormPanel currentXFormPanel,
			final Action ac) {
		UploadHelper uh = currentXFormPanel.getUw().getUploadHelper();
		try {
			XFormContext xcontext = new XFormContext(currentXFormPanel.getContext(), data);
			uh.addStdPostParamsToBody(xcontext, currentXFormPanel.getElementInfo());
		} catch (SerializationException e) {
			// MessageBox.showSimpleMessage(AppCurrContext.getInstance()
			// .getInternationalizedMessages().xforms_upload_error(),
			// e.getMessage());
			MessageBox.showSimpleMessage(
					// AppCurrContext.getInstance().getBundleMap().get("xforms_upload_error"),
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"File uploading error"),
					e.getMessage());
		}

		currentXFormPanel.getPanel().add(uh);
		uh.submit(new CompleteHandler() {

			@Override
			public void onComplete(final boolean aRes) {

			}
		});
		uh.clear();
	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Отфильтровать в
	 * XForm.
	 * 
	 * @param xformId
	 *            Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void xFormPanelClickFilter(final String xformId, final String linkId,
			final String data) {
		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {
			// MessageBox.showSimpleMessage("xFormPanelClickFilter. xformId=" +
			// xformId + ", linkId="
			// + linkId, data);

			Action ac = getActionByLinkId(linkId, currentXFormPanel);

			if (ac != null) {
				currentXFormPanel.setLoadedInDOM(true);

				ac.filterBy(data);
				runAction(ac, currentXFormPanel.getElement());
			}
		}
	}

	/**
	 * Загружает файл с сервера.
	 * 
	 * @param xformId
	 *            Id элемента xForm
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void downloadFile(final String xformId, final String linkId, final String data) {
		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {

			// MessageBox.showSimpleMessage(
			// "downloadFile. xformId=" + xformId + ", linkId=" + linkId, data);

			DownloadHelper dh = DownloadHelper.getInstance();
			dh.setEncoding(FormPanel.ENCODING_URLENCODED);
			dh.clear();
			// dh.setErrorCaption(AppCurrContext.getInstance().getInternationalizedMessages()
			// .xforms_download_error());
			dh.setErrorCaption(
					// AppCurrContext.getInstance().getBundleMap().get("xforms_download_error"));
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"File downloading error"));
			dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/download");

			try {
				dh.addParam("linkId", linkId);
				dh.addStdPostParamsToBody(new XFormContext(currentXFormPanel.getContext(), data),
						currentXFormPanel.getElementInfo());
				dh.submit();
			} catch (SerializationException e) {
				// MessageBox.showSimpleMessage(AppCurrContext.getInstance()
				// .getInternationalizedMessages().xforms_download_error(),
				// e.getMessage());
				MessageBox.showSimpleMessage(
						// AppCurrContext.getInstance().getBundleMap().get("xforms_download_error"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"File downloading error"),
						e.getMessage());
			}
		}
	}

	/**
	 * Загружает файл на сервер.
	 * 
	 * @param o
	 *            JavaScriptObject
	 * 
	 */
	public static void uploadFile(final JavaScriptObject o) {
		/**
		 * SelectorParam
		 */
		final class UploadParam extends JavaScriptObject {

			protected UploadParam() {

			};

			/**
			 * Id элемента xForm
			 * 
			 * @return String
			 */
			native String xformsId()/*-{
		return this.xformsId;
	}-*/;

			/**
			 * Id ссылки на файл.
			 * 
			 * @return String
			 */
			native String linkId()/*-{
		return this.linkId;
	}-*/;

			/**
			 * onSelectionComplete
			 * 
			 * @param ok
			 *            boolean
			 * 
			 * @param fileName
			 *            String
			 */
			native void onSelectionComplete(final boolean ok, final String fileName)/*-{
		this.onSelectionComplete(ok, fileName);
	}-*/;
		}
		final UploadParam param = (UploadParam) o;

		final XFormPanel currentXFormPanel = getCurrentPanel(param.xformsId());

		if (currentXFormPanel != null) {

			if (currentXFormPanel.getUw() == null) {
				// currentXFormPanel.setUw(new
				// UploadWindow(AppCurrContext.getInstance()
				// .getInternationalizedMessages().xform_upload_caption()));
				currentXFormPanel.setUw(new UploadWindow(
						// AppCurrContext.getInstance().getBundleMap().get("xform_upload_caption")));
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"File downloading")));
				currentXFormPanel.getPanel().add(currentXFormPanel.getUw());
				UploadHelper uh = currentXFormPanel.getUw().getUploadHelper();
				// uh.setErrorCaption(AppCurrContext.getInstance().getInternationalizedMessages()
				// .xforms_upload_error());
				uh.setErrorCaption(
						// AppCurrContext.getInstance().getBundleMap().get("xforms_upload_error"));
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"File uploading error"));
				uh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/upload");
			}
			currentXFormPanel.getUw().runUpload(param.linkId(), new UploadEndHandler() {

				@Override
				public void onEnd(final boolean res, final String filePath) {

					String fileName = "";
					if (filePath.indexOf("/") > -1) {
						fileName =
							filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
					} else {
						fileName =
							filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
					}

					param.onSelectionComplete(res, fileName);
				}

			});
		}
	}

	/**
	 * Возвращает текущую XFormPanel.
	 * 
	 * @param xformId
	 *            - Id элемента xForm.
	 * @return XFormPanel
	 */
	public static XFormPanel getCurrentPanel(final String xformId) {
		if (testXFormPanel == null) {
			return (XFormPanel) ActionExecuter.getElementPanelById(xformId);
		} else {
			return testXFormPanel;
		}
	}

}
