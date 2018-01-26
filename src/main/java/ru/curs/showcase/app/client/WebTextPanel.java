package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с WebText.
 */
public class WebTextPanel extends BasicElementPanelBasis {

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * VerticalPanel на которой отображен web-text.
	 */
	private final VerticalPanel generalWebTextPanel;

	/**
	 * WebText.
	 */
	private WebText webText;

	/**
	 * HTML элемент, который показывает WebText.
	 */
	private HTML thmlwidget = null;

	public WebTextPanel(final CompositeContext context1, final DataPanelElementInfo element1) {

		this.setContext(context1);
		this.setElementInfo(element1);
		generalWebTextPanel = new VerticalPanel();

		this.getPanel().addStyleName("webtext-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		generalWebTextPanel.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			thmlwidget = new HTML("<div class=\"progress-bar\"></div>");
			// new HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading"));
		} else {
			thmlwidget = new HTML("");
		}

		// thmlwidget =
		// new HTML(AppCurrContext.getInstance().getInternationalizedMessages()
		// .please_wait_data_are_loading());

		dataService = GWT.create(DataService.class);
		generalWebTextPanel.add(thmlwidget);
		setWebTextPanel();
	}

	public WebTextPanel(final DataPanelElementInfo element1) {

		this.setElementInfo(element1);

		setContext(null);

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			thmlwidget = new HTML("<div class=\"progress-bar\"></div>");
			// new HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading"));
		} else {
			thmlwidget = new HTML("");
		}

		// thmlwidget =
		// new HTML(AppCurrContext.getInstance().getInternationalizedMessages()
		// .please_wait_data_are_loading());
		generalWebTextPanel = new VerticalPanel();

		this.getPanel().addStyleName("webtext-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		generalWebTextPanel.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		generalWebTextPanel.add(thmlwidget);

	}

	private void setWebTextPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getWebText(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<WebText>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_webtext_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving text data from server")) {

					@Override
					public void onSuccess(final WebText awt) {
						webText = awt;
						if (webText != null) {

							super.onSuccess(webText);

							fillWebTextPanel(webText);
						}

						Scheduler.get().scheduleDeferred(new Command() {
							@Override
							public void execute() {
								for (DataPanelElementInfo el : AppCurrContext.getReadyStateMap()
										.keySet()) {
									if (el.getType() == DataPanelElementType.WEBTEXT
											&& !AppCurrContext.getReadyStateMap().get(el)) {
										AppCurrContext.getReadyStateMap().put(el, true);
										break;
									}
								}

								if (!AppCurrContext.getReadyStateMap().containsValue(false)) {
									RootPanel.getBodyElement().addClassName("ready");
								}
							}
						});

					}

				});

	}

	/**
	 * 
	 * Заполняет WebTextPanel текстом.
	 * 
	 * @param aWebText
	 *            - WebText
	 */
	protected void fillWebTextPanel(final WebText aWebText) {
		thmlwidget.setHTML(aWebText.getData());
		setCollbackJSNIFunction();
		checkForDefaultAction();
		setupTimer();

	}

	/**
	 * @return Возвращает текущий объект типа WebText - данные веб текста.
	 */
	public WebText getWebText() {
		return webText;
	}

	// CHECKSTYLE:OFF
	/**
	 * 
	 * Процедура определяющая функцию, которая будет выполняться по клику на
	 * ссылку в WebText.
	 * 
	 */
	public native void setCollbackJSNIFunction() /*-{
		$wnd.gwtWebTextFunc = 
		@ru.curs.showcase.app.client.api.WebTextPanelCallbacksEvents::webTextPanelClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
	}-*/;

	// CHECKSTYLE:ON

	@Override
	public VerticalPanel getPanel() {
		return generalWebTextPanel;
	}

	public void setDataService(final DataServiceAsync adataService) {
		this.dataService = adataService;
	}

	public DataServiceAsync getDataService() {
		return dataService;
	}

	@Override
	public void reDrawPanel(final CompositeContext context1) {
		setContext(context1);

		// generalWebTextPanel.getOffsetHeight()

		// MessageBox
		// .showSimpleMessage("size",
		// String.valueOf(generalWebTextPanel.getOffsetHeight()));

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		if (this.getElementInfo().getShowLoadingMessage()) {
			// thmlwidget.setText(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading"));
			thmlwidget.setText("");
			thmlwidget.addStyleName("progress-bar");
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getWebText(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<WebText>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_webtext_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving text data from server")) {

					@Override
					public void onSuccess(final WebText awt) {
						webText = awt;
						if (webText != null) {

							super.onSuccess(webText);

							fillWebTextPanel(awt);
							getPanel().setHeight(SIZE_ONE_HUNDRED_PERCENTS);
							thmlwidget.removeStyleName("progress-bar");

							Scheduler.get().scheduleDeferred(new Command() {
								@Override
								public void execute() {
									for (DataPanelElementInfo el : AppCurrContext
											.getReadyStateMap().keySet()) {
										if (getElementInfo().getId().getString()
												.equals(el.getId().getString())
												&& !AppCurrContext.getReadyStateMap().get(el)) {
											AppCurrContext.getReadyStateMap().put(el, true);
											break;
										}
									}

									for (DataPanelElementInfo el : AppCurrContext
											.getFromActionElementsMap().keySet()) {
										if (getElementInfo().getId().getString()
												.equals(el.getId().getString())
												&& !AppCurrContext.getFromActionElementsMap().get(
														el)) {
											AppCurrContext.getFromActionElementsMap()
													.put(el, true);
											break;
										}
									}

									if (!AppCurrContext.getInstance()
											.getWebTextXformTrueStateForReadyStateMap()
											&& !AppCurrContext
													.getInstance()
													.getGridWithToolbarWebtextTrueStateForReadyStateMap())
										if (!AppCurrContext.getReadyStateMap()
												.containsValue(false)) {
											RootPanel.getBodyElement().addClassName("ready");
										}
								}
							});

						}
					}
				});

	}

	@Override
	public void hidePanel() {
		generalWebTextPanel.setVisible(false);
	}

	@Override
	public void showPanel() {
		generalWebTextPanel.setVisible(true);

	}

	private void checkForDefaultAction() {
		if (webText.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(
					webText.getActionForDependentElements(), webText);
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return webText;
	}

	@Override
	public void refreshPanel() {

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		if (this.getElementInfo().getShowLoadingMessage()) {
			// thmlwidget.setText(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading"));
			thmlwidget.setText("");
			thmlwidget.addStyleName("progress-bar");
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getWebText(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<WebText>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_webtext_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving text data from server")) {

					@Override
					public void onSuccess(final WebText awt) {
						webText = awt;
						if (webText != null) {

							super.onSuccess(webText);

							fillWebTextPanel(awt);
							getPanel().setHeight(SIZE_ONE_HUNDRED_PERCENTS);
							// onElementLoadEvent(getElementInfo().getId());
							thmlwidget.removeStyleName("progress-bar");

						}
					}
				});

	}

	// public static native void onElementLoadEvent(final String id) /*-{

	// $wnd.elementLoadEvent(id);
	// }-*/;

}
