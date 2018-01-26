package ru.curs.showcase.app.client;

import java.util.List;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.UploadWindow;

/**
 * Класс панели с XForm.
 */
public class XFormPanel extends BasicElementPanelBasis {

	private static final String PROC100 = "100%";
	private static final String SHOWCASE_APP_CONTAINER = "showcaseAppContainer";

	private final VerticalPanel p = new VerticalPanel();
	private final HTML xf = new HTML();
	{
		xf.setWidth(PROC100);
	}

	private XForm xform = null;
	private String mainInstance = null;

	private JavaScriptObject cacheInstances;

	private Boolean needReload = true;

	private Boolean loadedInDOM = false;

	public void setLoadedInDOM(final Boolean aLoadedInDOM) {
		loadedInDOM = aLoadedInDOM;
	}

	/**
	 * Окно для загрузки файлов на сервер.
	 */
	private UploadWindow uw = null;

	private DataServiceAsync dataService = null;

	private final SelectorDataServiceAsync selSrv = GWT.create(SelectorDataService.class);
	{
		((ServiceDefTarget) selSrv).setServiceEntryPoint(
				GWT.getModuleBaseURL() + "SelectorDataService" + Window.Location.getQueryString());
	}

	public SelectorDataServiceAsync getSelSrv() {
		return selSrv;
	}

	/**
	 * Ф-ция, возвращающая панель с XForm.
	 * 
	 * @return - Панель с XForm.
	 */
	@Override
	public VerticalPanel getPanel() {
		return p;
	}

	@Override
	public DataPanelElement getElement() {
		return xform;
	}

	public DataServiceAsync getDataService() {
		return dataService;
	}

	public UploadWindow getUw() {
		return uw;
	}

	public void setUw(final UploadWindow auw) {
		this.uw = auw;
	}

	@Override
	public void hidePanel() {
		p.setVisible(false);
	}

	@Override
	public void showPanel() {
		p.setVisible(true);
	}

	@Override
	protected void internalResetLocalContext() {
		mainInstance = null;
	}

	/**
	 * Возвращает содержимое mainInstance.
	 * 
	 * @return содержимое mainInstance
	 * 
	 */
	public String fillAndGetMainInstance() {
		if (loadedInDOM) {
			fillMainInstance(xform.getSubformId());
		}
		return mainInstance;
	}

	private native void fillMainInstance(final String subformId) /*-{
		if ($wnd.getSubformInstanceDocument(subformId + 'mainModel', subformId
				+ 'mainInstance') != null) {
			this.@ru.curs.showcase.app.client.XFormPanel::mainInstance = $wnd.Writer
					.toString($wnd.getSubformInstanceDocument(subformId
							+ 'mainModel', subformId + 'mainInstance'));
		}
	}-*/;

	/**
	 * Конструктор класса XFormPanel без начального показа XForm.
	 */
	public XFormPanel(final DataPanelElementInfo element) {
		// MessageBox.showSimpleMessage("",
		// "Конструктор класса XFormPanel без начального показа XForm");

		setElementInfo(element);
		setContext(null);

		this.getPanel().addStyleName("xform-element");
		this.getPanel().addStyleName("id-" + element.getId().getString());
	}

	/**
	 * Конструктор класса XFormPanel с начальным показом XForm.
	 */
	public XFormPanel(final CompositeContext context, final DataPanelElementInfo element,
			final XForm xformExternal) {
		// MessageBox
		// .showSimpleMessage("",
		// "Конструктор класса XFormPanel с начальным показом XForm");

		setContext(context);
		setElementInfo(element);

		this.getPanel().addStyleName("xform-element");
		this.getPanel().addStyleName("id-" + element.getId().getString());

		if (xformExternal == null) {
			needReload = true;
			setXFormPanel(true);
		} else {
			RootPanel.get(SHOWCASE_APP_CONTAINER).clear();
			RootPanel.get(SHOWCASE_APP_CONTAINER).add(p);

			setXFormPanelByXForms(xformExternal);
		}

	}

	/**
	 * Ф-ция reDrawPanel для тестов.
	 * 
	 * @param context
	 *            CompositeContext
	 * @param xformExternal
	 *            XForms
	 */
	public void reDrawPanelForTest(final CompositeContext context, final XForm xformExternal) {

		setContext(context);
		// --------------

		RootPanel.get(SHOWCASE_APP_CONTAINER).clear();
		RootPanel.get(SHOWCASE_APP_CONTAINER).add(p);

		setXFormPanelByXForms(xformExternal);

	}

	@Override
	public void reDrawPanel(final CompositeContext context) {

		// MessageBox.showSimpleMessage("", "reDrawPanel");

		setContext(context);

		refreshPanel();
	}

	@Override
	public final void refreshPanel() {

		// MessageBox.showSimpleMessage("", "refreshPanel");

		needReload = true;
		setXFormPanel(false);
	}

	private void setXFormPanel(final boolean fromConstructor) {

		p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		unloadSubform();

		resetLocalContext();

		p.clear();
		if (fromConstructor || getElementInfo().getShowLoadingMessage()) {
			if (getElementInfo().getShowLoadingMessageForFirstTime()) {
				// xf.setHTML(AppCurrContext.getInstance().getBundleMap()
				// .get("please_wait_data_are_loading"));
				xf.setHTML("<div class=\"progress-bar\"></div>");
			} else {
				xf.setHTML("");
			}
		}
		p.add(xf);

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getXForms(getDetailedContext(), getElementInfo(),
				new GWTServiceCallback<XForm>(
						// AppCurrContext.getInstance().getBundleMap().get("xformsErrorGetData"))
						// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving data from the server XForm")) {
					@Override
					public void onSuccess(final XForm aXform) {

						super.onSuccess(aXform);

						setXFormPanelByXForms(aXform);

						Scheduler.get().scheduleDeferred(new Command() {
							@Override
							public void execute() {
								boolean gridRelated = false;
								for (DataPanelElementInfo elem : AppCurrContext.getReadyStateMap()
										.keySet()) {
									if (getElementInfo().getRelated().contains(elem.getId())) {
										AppCurrContext.getReadyStateMap().put(elem, true);
										if (elem.getType() == DataPanelElementType.GRID)
											gridRelated = true;
									}
								}

								for (DataPanelElementInfo el : AppCurrContext.getReadyStateMap()
										.keySet()) {
									if (getElementInfo().getId().getString()
											.equals(el.getId().getString())
											&& !AppCurrContext.getReadyStateMap().get(el)) {
										AppCurrContext.getReadyStateMap().put(el, true);
										break;
									}
								}

								for (DataPanelElementInfo el : AppCurrContext
										.getNeverShowInPanelElementsReadyStateMap().keySet()) {
									if (getElementInfo().getId().getString()
											.equals(el.getId().getString())
											&& !AppCurrContext
													.getNeverShowInPanelElementsReadyStateMap()
													.get(el)) {
										AppCurrContext.getNeverShowInPanelElementsReadyStateMap()
												.put(el, true);
										break;
									}
								}

								for (DataPanelElementInfo el : AppCurrContext
										.getNeverShowInPanelElementsFromActionMap().keySet()) {
									if (getElementInfo().getId().getString()
											.equals(el.getId().getString())
											&& !AppCurrContext
													.getNeverShowInPanelElementsFromActionMap()
													.get(el)) {
										AppCurrContext.getNeverShowInPanelElementsFromActionMap()
												.put(el, true);
										break;
									}
								}

								// for (Entry<DataPanelElementInfo, Boolean>
								// readyEntry : AppCurrContext
								// .getNeverShowInPanelElementsReadyStateMap().entrySet())
								// {
								// for (Entry<DataPanelElementInfo, Boolean>
								// fromActionEntry : AppCurrContext
								// .getNeverShowInPanelElementsFromActionMap().entrySet())
								// {
								// if (readyEntry
								// .getKey()
								// .getId()
								// .getString()
								// .equals(fromActionEntry.getKey().getId()
								// .getString())) {
								// if (readyEntry.getKey().getType() ==
								// DataPanelElementType.XFORMS
								// && fromActionEntry.getKey().getType() ==
								// DataPanelElementType.XFORMS
								// && readyEntry.getValue()
								// && fromActionEntry.getValue()) {
								// // final Timer timer = new
								// // Timer() {
								// // @Override
								// // public void run() {
								// //
								// RootPanel.getBodyElement().addClassName("ready");
								// // }
								// // };
								// // final int n3000 = 3000;
								// // timer.schedule(n3000);
								//
								// // final Timer timer = new
								// // Timer() {
								// // @Override
								// // public void run() {
								// //
								// // dataService
								// // .fakeRPC(new
								// // GWTServiceCallback<Void>(
								// // "Error") {
								// // @Override
								// // public void onSuccess(
								// // final Void result) {
								// // Scheduler
								// // .get()
								// // .scheduleDeferred(
								// // new Command() {
								// // @Override
								// // public
								// // void
								// // execute() {
								// // RootPanel
								// // .getBodyElement()
								// // .addClassName(
								// // "ready");
								// // }
								// // });
								// // }
								// // });
								// // }
								// // };
								// // final int n3000 = 3000;
								// // timer.schedule(n3000);
								//
								// }
								// }
								// }
								// }

								if (!gridRelated) {
									if (!AppCurrContext.getReadyStateMap().containsValue(false)) {
										// if
										// (RootPanel.getBodyElement().getClassName()
										// != null
										// &&
										// !RootPanel.getBodyElement().getClassName()
										// .contains("ready")
										// &&
										// !RootPanel.getBodyElement().getClassName()
										// .equals("ready")) {
										// final Timer timer = new Timer() {
										// @Override
										// public void run() {
										RootPanel.getBodyElement().addClassName("ready");
										// }
										// };
										// final int n500 = 500;
										// timer.schedule(n500);

										// }
										AppCurrContext.getInstance()
												.setWebTextXformTrueStateForReadyStateMap(true);
										AppCurrContext.getInstance()
												.setChartXformTrueStateForReadyStateMap(true);
										AppCurrContext.getInstance()
												.setGeoMapXformTrueStateForReadyStateMap(true);
										AppCurrContext.getInstance()
												.setPluginXformTrueStateForReadyStateMap(true);
									}
								}
							}
						});
					}
				});

	}

	private void setXFormPanelByXForms(final XForm aXform) {

		xform = aXform;

		xf.getElement().setId(xform.getSubformId());

		instrumentForm(xform.getXFormParts(), xform.getSubformId());
		loadedInDOM = true;

		Action ac = xform.getActionForDependentElements();
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, xform);
			ActionExecuter.execAction();
		}

		setupTimer();

		p.setSize(PROC100, PROC100);
	}

	/**
	 * Инструментует панель с формой.
	 */
	private static void instrumentForm(final List<String> stringList, final String subformId) {

		// прописываем динамический CSS
		if ((stringList.size() > 1) && (!stringList.get(1).trim().isEmpty())) {
			addSubformCSS(subformId + "style", stringList.get(1));
		}

		// MessageBox.showSimpleMessage("", stringList.get(0));

		initSubform(stringList.get(0), subformId);

	}

	/**
	 * Добавляет динамический CSS для подформы.
	 * 
	 * @param subformTagId
	 *            - id подформы
	 * 
	 * @param cssdef
	 *            - текст CSS.
	 */
	private static native void addSubformCSS(final String subformTagId, final String cssdef) /*-{
		var ss1 = $doc.createElement('style');
		ss1.setAttribute('type', 'text/css');
		ss1.setAttribute('id', subformTagId);
		if (ss1.styleSheet) { // IE
			ss1.styleSheet.cssText = cssdef;
		} else { // the world
			var tt1 = $doc.createTextNode(cssdef);
			ss1.appendChild(tt1);
		}
		var hh1 = $doc.getElementsByTagName('head')[0];
		hh1.appendChild(ss1);
	}-*/;

	/**
	 * Инициализирует X-форму, запуская инструментующий javascript.
	 */
	private static native void initSubform(final String subform, final String subformId) /*-{
		$wnd.XsltForms_load.subform(subform, subformId);
	}-*/;

	/**
	 * Закрывает X-форму, деиницилизирует инструментовку и подчищает все
	 * динамически созданные скрипты и стили.
	 */
	private static native void unloadSubformById(final String subformId) /*-{

		//Выгружаем подформу
		$wnd.XsltForms_unload.subform(subformId);

		//Подчищаем динамические стили
		var hdr = $doc.getElementsByTagName('head')[0];
		var ss1 = $doc.getElementById(subformId + 'style');
		if (ss1 != null)
			hdr.removeChild(ss1);

	}-*/;

	/**
	 * Выгружает со страницы подформу, связанную с данной панелью.
	 */
	public void unloadSubform() {
		if (loadedInDOM) {

			if (getElementInfo().getCacheData()) {
				cacheInstances = getXFormCacheInstances(xform.getSubformId());
			}

			unloadSubformById(xform.getSubformId());
			loadedInDOM = false;

		}
	}

	private static native void closeAllXFDialogs() /*-{
		$wnd.XsltForms_browser.dialog.hideALL();
	}-*/;

	/**
	 * Закрывает форму, снимая всю ранее выставленную Javascript-инструментовку.
	 */
	public static void unloadAllSubforms() {

		closeAllXFDialogs();

		List<UIDataPanelTab> uiDataPanel = AppCurrContext.getInstance().getUiDataPanel();
		for (int i = 0; i < uiDataPanel.size(); i++) {
			List<UIDataPanelElement> uiElements = uiDataPanel.get(i).getUiElements();
			for (int j = 0; j < uiElements.size(); j++) {
				if (uiElements.get(j).getElementPanel().getElementInfo()
						.getType() == DataPanelElementType.XFORMS) {
					if (((XFormPanel) uiElements.get(j).getElementPanel()).xform != null) {

						XFormPanel xfp = (XFormPanel) uiElements.get(j).getElementPanel();
						xfp.unloadSubform();

					}
				}
			}
		}
	}

	/**
	 * Восстанавливает закешированную XFormPanel.
	 * 
	 */
	public void restoreCacheXForm(final Widget el) {
		setNeedResetLocalContext(false);

		setXFormPanelByXForms(xform);

		setXFormCacheInstances(xform.getSubformId(), cacheInstances);
	}

	private static native JavaScriptObject getXFormCacheInstances(final String subformId) /*-{
		return $wnd.getXFormCacheInstances(subformId);
	}-*/;

	private static native void setXFormCacheInstances(final String subformId,
			final JavaScriptObject xFormCacheInstances) /*-{
		$wnd.setXFormCacheInstances(subformId, xFormCacheInstances);
	}-*/;

	@Override
	public XFormContext getDetailedContext() {
		XFormContext context = new XFormContext(getContext(), fillAndGetMainInstance());
		context.setKeepUserSettings(!isNeedResetLocalContext());
		context.setNeedReload(needReload);
		return context;
	}

}
