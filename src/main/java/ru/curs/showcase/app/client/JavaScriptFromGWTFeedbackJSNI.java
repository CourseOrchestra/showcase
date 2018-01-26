/**
 * 
 */
package ru.curs.showcase.app.client;

/**
 * 
 * Класс определяющий Java функции (JSNI-технология), которые будут выполняться
 * при их вызове из GWT кода. Двнные js-функции должны быть определены в
 * dom-модели Showcase.
 * 
 * @author anlug
 * 
 */
public final class JavaScriptFromGWTFeedbackJSNI {

	private JavaScriptFromGWTFeedbackJSNI() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Функция подставляющая в html-контрол dom модели главной страницы
	 * index.jsp окна приветствия и в верхнем и в нижнем колонтитуле текущее имя
	 * пользователя.
	 * 
	 * @param preffix
	 *            - префикс элемента для которого будет отработана функция.
	 *            (Возможные значения: HEADER, FOOTER, WELCOM).
	 */
	public static native void setCurrentUserDetailsForViewInHTMLControl(final String preffix) /*-{
		$wnd.setCurrentUserDetailsForViewInHTMLControl(preffix);

	}-*/;

}
