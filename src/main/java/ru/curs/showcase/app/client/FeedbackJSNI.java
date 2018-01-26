/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.*;

/**
 * 
 * Класс определяющий Java функции gwt клиентского кода (JSNI-технология),
 * которые будут выполняться при их вызове в javaScript из dom-модели Showcase.
 * 
 * @author anlug
 * 
 */
public final class FeedbackJSNI {

	private FeedbackJSNI() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Функция возвращает последний main-context с которым был отрисован
	 * элемент.
	 * 
	 * @param elementId
	 *            - Id элемента
	 * @return - String
	 */
	public static String getElementMainContext(final String elementId) {
		BasicElementPanel bep = ActionExecuter.getElementPanelById(elementId);
		if (bep != null) {
			if (((BasicElementPanelBasis) bep).getContext() != null) {
				return ((BasicElementPanelBasis) bep).getContext().getMain();
			}
		}
		return null;
	}

	/**
	 * 
	 * Функция возвращает последний addition-context с которым был отрисован
	 * элемент.
	 * 
	 * @param elementId
	 *            - Id элемента
	 * @return - String
	 */
	public static String getElementAdditionContext(final String elementId) {
		BasicElementPanel bep = ActionExecuter.getElementPanelById(elementId);
		if (bep != null) {
			if (((BasicElementPanelBasis) bep).getContext() != null) {
				return ((BasicElementPanelBasis) bep).getContext().getAdditional();
			}
		}
		return null;

	}

	/**
	 * 
	 * Функция возвращает последний addition-context с которым был отрисован
	 * элемент.
	 * 
	 * @param elementId
	 *            - Id элемента
	 * @return - String
	 */
	public static String refreshElementFromBase(final String elementId) {
		BasicElementPanel bep = ActionExecuter.getElementPanelById(elementId);
		if (bep != null) {
			bep.refreshPanel();
		}
		return null;

	}

	/**
	 * 
	 * Возвращает текущее имя пользователя.
	 * 
	 * @return - String
	 */
	public static String getCurrentUserName() {

		return AppCurrContext.getInstance().getServerCurrentState().getUserInfo().getCaption();

	}

	/**
	 * 
	 * Возвращает текущее полное имя пользователя.
	 * 
	 * @return - String
	 */
	public static String getCurrentFullUserName() {

		return AppCurrContext.getInstance().getServerCurrentState().getUserInfo().getFullName();

	}

	/**
	 * 
	 * Возвращает текущий email пользователя.
	 * 
	 * @return - String
	 */
	public static String getCurrentUserEmailName() {

		return AppCurrContext.getInstance().getServerCurrentState().getUserInfo().getEmail();

	}

	/**
	 * 
	 * Возвращает текущей SID пользователя.
	 * 
	 * @return - String
	 */
	public static String getCurrentUserSIDName() {

		return AppCurrContext.getInstance().getServerCurrentState().getUserInfo().getSid();

	}

	/**
	 * 
	 * Возвращает текущий телефон пользователя.
	 * 
	 * @return - String
	 */
	public static String getCurrentUserPhoneName() {

		return AppCurrContext.getInstance().getServerCurrentState().getUserInfo().getPhone();

	}

	/**
	 * 
	 * Возвращает SesionContext на момент старта приложения (загрузки главной
	 * страницы).
	 * 
	 * @return - String
	 */
	public static String getStartingAppSessionContext() {

		return AppCurrContext.getInstance().getServerCurrentState().getStartingAppSessionContext();

	}

	/**
	 * 
	 * Функция которая загружает и применяет в DOM модель страницы стили решения
	 * из файла.
	 * 
	 * @param cssFileName
	 *            - имя файла
	 */
	public static void setAndApplySolutionCSSToDomModel(final String cssFileName) {

		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + cssFileName));

	}

	/**
	 * 
	 * Функция которая загружает и применяет в DOM модель страницы стили для
	 * Grid из файла.
	 * 
	 * @param cssFileName
	 *            - имя файла
	 */
	public static void setAndApplySolutionGridCSSToDomModel(final String cssFileName) {

		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + cssFileName));

	}

	public static void selectNavigatorItem(final String id) {

		AppCurrContext.getInstance().getMainPanel().getAccordeon()
				.selectNesessaryItemInAccordion(new ID(id), true);

	}

	public static void selectDatapanelTab(final String index) {

		GeneralDataPanel.getTabPanel().selectTab(Integer.parseInt(index));

	}

	/**
	 * 
	 * Процедура инициализирующая Java функции gwt клиентского кода
	 * (JSNI-технология), которые будут выполняться при их вызове в javaScript
	 * из dom-модели Showcase. Данная функция должна вызываться при
	 * инициализации-загрузке приложения.
	 * 
	 */
	public static native void initFeedbackJSNIFunctions() /*-{
		$wnd.getMainContextFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getElementMainContext(Ljava/lang/String;);
		$wnd.getAdditionalContextFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getElementAdditionContext(Ljava/lang/String;);
		$wnd.refreshElementFromBaseFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::refreshElementFromBase(Ljava/lang/String;);
        //$wnd.showAboutFeedbackJSNIFunction = 
		//@ru.curs.showcase.app.client.About::showAbout();	
		//CHECKSTYLE:OFF	
        $wnd.showModalWindowWithHTMLContent =
        @ru.curs.showcase.app.client.ModalWindowWithHTMLContent::showWindow(Ljava/lang/String;Ljava/lang/String;ZII);	
        $wnd.showInfoMessageWindow = 
        @ru.curs.showcase.app.client.MessageBox::showInfoMessageWindow(Ljava/lang/String;Ljava/lang/String;);
        $wnd.showWarningMessageWindow =
        @ru.curs.showcase.app.client.MessageBox::showWarningMessageWindow(Ljava/lang/String;Ljava/lang/String;);
        $wnd.showErrorMessageWindow = 
        @ru.curs.showcase.app.client.MessageBox::showErrorMessageWindow(Ljava/lang/String;Ljava/lang/String;);
        //CHECKSTYLE:ON    
        $wnd.closeModalWindowWithHTMLContent =
        @ru.curs.showcase.app.client.ModalWindowWithHTMLContent::closeWindow();	         			
		$wnd.getCurrentUserNameFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getCurrentUserName();	
		
				$wnd.getCurrentUserFullNameFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getCurrentFullUserName();
				$wnd.getCurrentUserEMailFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getCurrentUserEmailName();
				$wnd.getCurrentUserSIDFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getCurrentUserSIDName();
				$wnd.getCurrentUserPhoneFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getCurrentUserPhoneName();
				$wnd.getStartingAppSessionContextFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getStartingAppSessionContext();
		
				$wnd.setAndApplySolutionCSSToDomModel = 
		@ru.curs.showcase.app.client.FeedbackJSNI::setAndApplySolutionCSSToDomModel(Ljava/lang/String;);		
				$wnd.setAndApplySolutionGridCSSToDomModel = 
		@ru.curs.showcase.app.client.FeedbackJSNI::setAndApplySolutionGridCSSToDomModel(Ljava/lang/String;);	
		
		$wnd.showcaseLogout = 
		@ru.curs.showcase.app.client.ShowcaseLogout::showcaseLogout();	
		
		$wnd.selectNavigatorItem = 
	@ru.curs.showcase.app.client.FeedbackJSNI::selectNavigatorItem(Ljava/lang/String;);
		$wnd.selectDatapanelTab = 
	@ru.curs.showcase.app.client.FeedbackJSNI::selectDatapanelTab(Ljava/lang/String;);
	}-*/;

}
