/**
 * 
 */
package ru.curs.showcase.app.client.utils;

/**
 * @author anlug
 * 
 */
public final class AccessToDomModel {

	private AccessToDomModel() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Динамически вставляет в страницу javascript-код непосредственно в тело
	 * тега script.
	 * 
	 * @param link
	 *            ссылка на Javacript-код
	 */
	public static native void addScriptLink(final String link) /*-{
		$wnd.safeIncludeJS(link);
	}-*/;

	/**
	 * @return - возвращает текущий ContextPath для данной открытой страницы
	 *         (например, "/showcase")
	 */
	public static native String getAppContextPath() /*-{
		return $wnd.appContextPath;
	}-*/;

	/**
	 * Динамически добавляет ссылку на CSS в DOM.
	 * 
	 * @param link
	 *            - адрес ссылки.
	 */
	public static native void addCSSLink(final String link) /*-{
		if (!@ru.curs.showcase.app.client.utils.AccessToDomModel::isAddedCSSLink(Ljava/lang/String;)(link)) {
			var objCSS = $doc.createElement('link');
			objCSS.rel = 'stylesheet';
			objCSS.href = link;
			objCSS.type = 'text/css';
			var hh1 = $doc.getElementsByTagName('head')[0];
			hh1.appendChild(objCSS);
		}
	}-*/;

	/**
	 * Динамически вставляет в страницу ссылку нa javascript-file, формируя тег
	 * script с атрибутом src.
	 * 
	 * @param link
	 *            - адрес ссылки
	 */
	public static native void addScriptTag(final String link) /*-{
		if (!@ru.curs.showcase.app.client.utils.AccessToDomModel::isAddedScriptLink(Ljava/lang/String;)(link)) {
			var js = $doc.createElement('script');
			js.type = 'text/javascript'
			js.src = link;
			var hh1 = $doc.getElementsByTagName('head')[0];
			hh1.appendChild(js);
		}
	}-*/;

	/**
	 * Проверить добавлена ли ссылка на javascript в DOM.
	 * 
	 * @param link
	 *            - адрес ссылки.
	 */
	public static native boolean isAddedScriptLink(final String link) /*-{
		link = link.toLowerCase();
		var scripts = $doc.getElementsByTagName("script");
		for ( var i = 0; i < scripts.length; i++) {
			if (scripts[i].src
					&& scripts[i].src.toLowerCase().lastIndexOf(link) != -1) {
				return true;
			}
		}
		return false;
	}-*/;

	/**
	 * Проверить добавлена ли ссылка на CSS в DOM.
	 * 
	 * @param link
	 *            - адрес ссылки.
	 */
	public static native boolean isAddedCSSLink(final String link) /*-{
		link = link.toLowerCase();
		var sheets = $doc.styleSheets;
		for ( var i = 0; i < sheets.length; i++) {
			if (sheets[i].href
					&& sheets[i].href.toLowerCase().lastIndexOf(link) != -1) {
				return true;
			}
		}
		return false;
	}-*/;
}
