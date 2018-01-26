package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Класс точки входа в GWT часть приложения. Используется функция
 * <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * Метод точки входа в приложение Showcase.
	 */
	@Override
	public void onModuleLoad() {

		RootPanel.getBodyElement().removeClassName("ready");

		AppCurrContext.getInstance().setNavigatorItemSelected(false);

		ProgressWindow.showProgressWindow();

		final CompositeContext context = getCurrentContext();
		// setBundleMapForConstants(context);
		setLocalizationBundleDomain(context);
	}

	// private void setBundleMapForConstants(final CompositeContext context) {
	// if (dataService == null) {
	// dataService = GWT.create(DataService.class);
	// }
	//
	// dataService.getBundle(context, new GWTServiceCallback<Map<String,
	// String>>(
	// "Error for bundleMap loading") {
	//
	// // new AsyncCallback<Map<String, String>>() {
	//
	// @Override
	// public void onSuccess(final Map<String, String> arg0) {
	// AppCurrContext.getInstance().setBundleMap(arg0);
	// initialize(context);
	// setDomain();
	// }
	//
	// // @Override
	// // public void onFailure(final Throwable arg0) {
	// // MessageBox.showSimpleMessage("error", "bundleMap");
	// // }
	// });
	// }

	/**
	 * Метод, устанавливающий имя домена (имя пакетного файла без расширения),
	 * служащего для перевода клиентской части Showcase.
	 * 
	 * @param context
	 *            - начальный контекст
	 */
	private void setLocalizationBundleDomain(final CompositeContext context) {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getLocalizationBundleDomainName(context, new AsyncCallback<String>() {

			@Override
			public void onSuccess(final String arg0) {
				AppCurrContext.getInstance().setDomain(arg0);
				CourseClientLocalization.setGettextVariable(arg0);
				initialize(context);
			}

			@Override
			public void onFailure(final Throwable arg0) {
				// MessageBox
				// .showSimpleMessage(
				// "Error",
				// "Session is not authenticated. Please log out and log in
				// again, or reopen browser window and log in");
				WebUtils.onFailure(arg0, "Error");
			}
		});
	}

	/**
	 * Метод, устанавливающий имя домена (имя пакетного файла без расширения),
	 * служащего для перевода клиентской части Showcase. Метод не
	 * предусматривает дальнейшего раскручивания приложения и применяется
	 * локально. Служит для локализации с помощью Gettext, когда язык
	 * устанавливается пользователем вне файла app.properties.
	 * 
	 * @param context
	 *            - начальный контекст
	 */

	private void initialize(final CompositeContext context) {
		XFormsUtils.initXForms();
		FeedbackJSNI.initFeedbackJSNIFunctions();
		// AppCurrContext.appCurrContext = AppCurrContext.getInstance();
		AppCurrContext.getInstance();

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getServerCurrentState(context, new
		// GWTServiceCallback<ServerState>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_server_current_state_retrieving_from_server()) {
		dataService.getServerCurrentState(
				context,
				new GWTServiceCallback<ServerState>(
				// AppCurrContext.getInstance().getBundleMap().
				// get("error_of_server_current_state_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving current application state data")) {

					@Override
					public void onSuccess(final ServerState serverCurrentState) {

						if (serverCurrentState != null) {

							if (serverCurrentState.isPreloadGrids()) {
								preloadGrids();
							}

							AppCurrContext.getInstance().setServerCurrentState(serverCurrentState);
							IDSettings.getInstance().setCaseSensivity(
									serverCurrentState.getCaseSensivityIDs());
							getAndFillMainPage();

						}
					}
				});

	}

	private native void preloadGrids() /*-{
		$wnd.preloadGrids();
	}-*/;

	private void getAndFillMainPage() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}
		CompositeContext context = getCurrentContext();
		// dataService.getMainPage(context, new
		// GWTServiceCallback<MainPage>(AppCurrContext
		// .getInstance().getInternationalizedMessages()
		// .error_of_main_page_retrieving_from_server()) {
		dataService.getMainPage(
				context,
				new GWTServiceCallback<MainPage>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_main_page_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving main application page")) {

					@Override
					public void onSuccess(final MainPage mainPage) {
						AppCurrContext.getInstance().setMainPage(mainPage);
						fillMainPage();
					}

				});
	}

	// генерация и размещение приложения в DOM модели Showcase.
	private void fillMainPage() {

		// генерация и размещение заглавной части (шапки) приложения
		// Showcase
		Header head = new Header();

		RootPanel.get("showcaseHeaderContainer").add(head.generateHeader());
		JavaScriptFromGWTFeedbackJSNI.setCurrentUserDetailsForViewInHTMLControl("HEADER");

		// генерация и размещение нижней части (колонтитул) приложения
		// Showcase
		Footer bottom = new Footer();
		RootPanel.get("showcaseBottomContainer").add(bottom.generateBottom());
		JavaScriptFromGWTFeedbackJSNI.setCurrentUserDetailsForViewInHTMLControl("FOOTER");

		// генерация и размещение главной части (главной) приложения
		// Showcase
		MainPanel mainPanel = new MainPanel();
		AppCurrContext.getInstance().setMainPanel(mainPanel);
		RootPanel.get("showcaseAppContainer").add(mainPanel.startMainPanelCreation());
		// добавляем свои стили после инициализации GWT-шных

		if (AppCurrContext.getInstance().getMainPage().getSolutionCSSFileName() != null
				&& AppCurrContext.getInstance().getMainPage().getSolutionGridCSSFileName() != null
				&& AppCurrContext.getInstance().getMainPage().getProgressBarCSSFileName() != null) {

			addUserDataCSS(AppCurrContext.getInstance().getMainPage().getSolutionCSSFileName(),
					AppCurrContext.getInstance().getMainPage().getSolutionGridCSSFileName(),
					AppCurrContext.getInstance().getMainPage().getProgressBarCSSFileName());

		} else {
			addUserDataCSS("solution.css", "solutionGrid.css", "progressBar.css");
		}
	}

	private CompositeContext getCurrentContext() {
		Map<String, List<String>> params =
			com.google.gwt.user.client.Window.Location.getParameterMap();
		CompositeContext context;
		context = new CompositeContext(params);
		return context;
	}

	private void addUserDataCSS(final String solutionCSS, final String solutionGridCSS,
			final String progressBarCSS) {
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + progressBarCSS));
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + solutionCSS));
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + solutionGridCSS));
	}

}
