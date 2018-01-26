package ru.curs.showcase.app.client.api;

import java.util.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.FormPanel;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.selector.SelectorAdditionalData;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.selector.*;
import ru.curs.showcase.app.client.selector.BaseSelectorComponent.*;
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
	 * Статический метод для открытия окна селектора с единственным выбором.
	 * Может быть использован непосредственно в javscript-е на форме.
	 * 
	 * @param o
	 *            Объект-хендлер окна-селектора
	 * 
	 */
	public static void showSelector(final JavaScriptObject o) {

		// MessageBox.showSimpleMessage("showSelector", "showSelector");

		showSingleAndMultiSelector(o, false);
	}

	/**
	 * Статический метод для открытия окна селектора со множественным выбором.
	 * Может быть использован непосредственно в javscript-е на форме.
	 * 
	 * @param o
	 *            Объект-хендлер окна-селектора
	 * 
	 */
	public static void showMultiSelector(final JavaScriptObject o) {
		showSingleAndMultiSelector(o, true);
	}

	/**
	 * SelectorParam.
	 */
	private static final class SelectorParam extends JavaScriptObject {

		protected SelectorParam() {

		};

		/**
		 * Id элемента xForm.
		 * 
		 * @return String
		 */
		native String id()/*-{
			return this.id;
		}-*/;

		/**
		 * Название процедуры получения общего числа записей.
		 * 
		 * @return String
		 */
		native String procCount()/*-{
			return this.procCount;
		}-*/;

		/**
		 * Название процедуры получения записей как таковых.
		 * 
		 * @return String
		 */
		native String procList()/*-{
			return this.procList;
		}-*/;

		/**
		 * Название процедуры получения и общего числа записей, и записей как
		 * таковых.
		 * 
		 * @return String
		 */
		native String procListAndCount()/*-{
			return this.procListAndCount;
		}-*/;

		/**
		 * общие фильтры. Этот параметр передаётся хранимой процедуре БД (см.
		 * ниже) без изменений
		 * 
		 * @return String
		 */
		native Object generalFilters()/*-{
			return this.generalFilters != null ? this.generalFilters : "";
		}-*/;

		/**
		 * начальное значение поискового поля.
		 * 
		 * @return String
		 */
		native String currentValue()/*-{
			return this.currentValue != null ? this.currentValue : "";
		}-*/;

		/**
		 * 
		 * начальное значение галочки "Начинается с".
		 * 
		 * @return boolean
		 */
		native boolean startWith()/*-{
			return this.startWith != null ? this.startWith : true;
		}-*/;

		/**
		 * 
		 * признак того, что надо скрыть галочку "Начинается с".
		 * 
		 * @return boolean
		 */
		native boolean hideStartWith()/*-{
			return this.hideStartWith != null ? this.hideStartWith : false;
		}-*/;

		/**
		 * заголовок окна для выбора из больших списков.
		 * 
		 * @return String
		 */
		native String windowCaption()/*-{
			return this.windowCaption;
		}-*/;

		/**
		 * Мультиселектор. Нужно ли очищать ноду перед вставкой выбранных
		 * значений.
		 * 
		 * @return boolean
		 */
		native boolean needClear()/*-{
			return this.needClear != null ? this.needClear : false;
		}-*/;

		/**
		 * Мультиселектор. Нужно ли загружать начальные выбранные значения.
		 * 
		 * @return boolean
		 */
		native boolean needInitSelection()/*-{
			return this.needInitSelection != null ? this.needInitSelection
					: false;
		}-*/;

		/**
		 * onSelectionComplete.
		 * 
		 * @param ok
		 *            boolean
		 * 
		 * @param selected
		 *            JavaScriptObject
		 */
		native void onSelectionComplete(final boolean ok, final JavaScriptObject selected)/*-{
			if (this.onSelectionComplete != null) {
				this.onSelectionComplete(ok, selected);
			}
		}-*/;

		/**
		 * onSelectionCompleteAction.
		 * 
		 * @param ok
		 *            boolean
		 * 
		 * @param selected
		 *            JavaScriptObject
		 */
		native void onSelectionCompleteAction(final boolean ok,
				final JavaScriptObject selected)/*-{
			if (this.onSelectionCompleteAction != null) {
				this.onSelectionCompleteAction(ok, selected);
			}
		}-*/;

		/**
		 * Мультиселектор. Определяет тег, куда должны попадать записи.
		 * 
		 * @return String
		 */
		native String xpathRoot()/*-{
			return this.xpathRoot;
		}-*/;

		/**
		 * mapping между полями XForm'ы и полями выбранной записи.
		 * 
		 * @return Map<String, String>
		 */
		native Map<String, String> xpathMapping()/*-{
			return this.xpathMapping;
		}-*/;

		/**
		 * Ширина списка с данными.
		 * 
		 * @return String
		 */
		native String dataWidth()/*-{
			return this.dataWidth;
		}-*/;

		/**
		 * Высота списка с данными.
		 * 
		 * @return String
		 */
		native String dataHeight()/*-{
			return this.dataHeight;
		}-*/;

		/**
		 * Мультиселектор. Ширина списка с выбранными данными.
		 * 
		 * @return String
		 */
		native String selectedDataWidth()/*-{
			return this.selectedDataWidth;
		}-*/;

		native String visibleRecordCount()/*-{
			return this.visibleRecordCount;
		}-*/;

		/**
		 * Поиск по кнопке (неавтоматический).
		 * 
		 * @return boolean
		 */
		native boolean manualSearch()/*-{
			return this.manualSearch != null ? this.manualSearch : false;
		}-*/;

		/**
		 * Признак того, что после фильтрации будет выделена первая запись.
		 * 
		 * @return boolean
		 */
		native boolean selectedFirst()/*-{
			return this.selectedFirst != null ? this.selectedFirst : false;
		}-*/;

	}

	private static void showSingleAndMultiSelector(final JavaScriptObject o,
			final boolean isMultiSelector) {

		final SelectorParam param = (SelectorParam) o;

		// MessageBox.showSimpleMessage(param.windowCaption(),
		// param.windowCaption());

		final XFormPanel currentXFormPanel =
			(XFormPanel) ActionExecuter.getElementPanelById(param.id());

		if (currentXFormPanel != null) {
			Options options = new Options();
			String defaultSelectedDataWidth = options.getSelectedDataWidth();
			if (param.dataWidth() != null) {
				options.dataWidth(param.dataWidth());
			}
			if (param.dataHeight() != null) {
				options.dataHeight(param.dataHeight());
			}
			if (param.selectedDataWidth() != null) {
				options.selectedDataWidth(param.selectedDataWidth());
			}
			if (param.visibleRecordCount() != null) {
				options.visibleRecordCount(Integer.valueOf(param.visibleRecordCount()));
			}

			options.startsWithChecked(param.startWith());
			options.hideStartsWith(param.hideStartWith());

			options.manualSearch(param.manualSearch());

			options.selectedFirst(param.selectedFirst());

			ErrorHandler errHandler = new ErrorHandler() {
				@Override
				public void onDataServiceFailure(final Throwable throwable) {
					WebUtils.onFailure(throwable, "Error");
				}
			};
			options.errorHandler(errHandler);

			BaseSelectorComponent c;
			if (isMultiSelector) {
				if (param.dataWidth() == null) {
					options.dataWidth(defaultSelectedDataWidth);
				}

				JavaScriptObject initSelection;
				if (param.needInitSelection()) {
					initSelection = getInitSelection(param.xpathRoot(), param.xpathMapping());
				} else {
					initSelection = null;
				}

				c = new MultiSelectorComponent(currentXFormPanel.getSelSrv(),
						param.windowCaption(), initSelection, options);
			} else {
				c = new SelectorComponent(currentXFormPanel.getSelSrv(), param.windowCaption(),
						options);
			}
			c.setSelectorListener(new BaseSelectorComponent.SelectorListener() {
				@Override
				public void onSelectionComplete(final BaseSelectorComponent selector) {
					if (param.xpathMapping() == null) {
						param.onSelectionCompleteAction(selector.isOK(),
								selector.getSelectedAsJsObject());

						param.onSelectionComplete(selector.isOK(),
								selector.getSelectedAsJsObject());
					} else {
						if (isMultiSelector) {
							insertXFormByXPath(selector.isOK(), selector.getSelectedAsJsObject(),
									param.xpathRoot(), param.xpathMapping(), param.needClear(),
									((XForm) currentXFormPanel.getElement()).getSubformId());
						} else {
							setXFormByXPath(selector.isOK(), selector.getSelectedAsJsObject(),
									param.xpathMapping(),
									((XForm) currentXFormPanel.getElement()).getSubformId());
						}

						param.onSelectionCompleteAction(selector.isOK(),
								selector.getSelectedAsJsObject());

						param.onSelectionComplete(selector.isOK(),
								selector.getSelectedAsJsObject());

					}
				}
			});
			c.center();

			String procName;
			if (param.procListAndCount() == null) {
				procName =
					param.procCount() + "FDCF8ABB9B6540A89E350010424C2B80" + param.procList();
			} else {
				procName = param.procListAndCount();
			}

			SelectorAdditionalData addData = new SelectorAdditionalData();
			addData.setContext(currentXFormPanel.getContext());
			addData.setElementInfo(currentXFormPanel.getElementInfo());

			c.initData(getXMLByXPathArray(param.generalFilters()), procName,
					getValueByXPath(param.currentValue()), addData);
		}
	}

	private static native void setXFormByXPath(final boolean ok, final JavaScriptObject selected,
			final Map<String, String> xpathMapping, final String subformId) /*-{
		$wnd.setXFormByXPath(ok, selected, xpathMapping, subformId);
	}-*/;

	// CHECKSTYLE:OFF
	private static native void insertXFormByXPath(final boolean ok,
			final JavaScriptObject selected, final String xpathRoot,
			final Map<String, String> xpathMapping, final boolean needClear,
			final String subformId) /*-{
		$wnd.insertXFormByXPath(ok, selected, xpathRoot, xpathMapping,
				needClear, subformId);
	}-*/;

	// CHECKSTYLE:ON

	private static native String getValueByXPath(final String xpath) /*-{
		return $wnd.getValueByXPath(xpath);
	}-*/;

	public static native String getXMLByXPathArray(final Object xpathArray) /*-{
		return $wnd.getXMLByXPathArray(xpathArray);
	}-*/;

	private static native JavaScriptObject getInitSelection(final String xpathRoot,
			final Map<String, String> xpathMapping) /*-{
		return $wnd.getInitSelection(xpathRoot, xpathMapping);
	}-*/;

	private static native JavaScriptObject
			getInitSelectionForSingleSelector(final Map<String, String> xpathMapping) /*-{
		return $wnd.getInitSelectionForSingleSelector(xpathMapping);
	}-*/;

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
