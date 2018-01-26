package ru.curs.showcase.app.client.internationalization;

public class CourseClientLocalization {

	/**
	 * Возвращает перевод переменной <VAR>msgid</VAR>.
	 * 
	 * @param domain
	 *            - имя используемого для перевода .po-файла без расширения
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена
	 * @return перевод переменной <VAR>msgid</VAR>
	 */
	public static native String gettext(final String domain, final String msgid) /*-{
		return $wnd.GettextInstance.gettext(msgid);
	}-*/;

	/**
	 * Возвращает форму множественного числа для номера <VAR>count</VAR>
	 * перевода переменной <VAR>msgid</VAR>.
	 * 
	 * @param domain
	 *            - имя используемого для перевода .po-файла без расширения
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена
	 * @param msgid_plural
	 *            - её форма множественного числа на английском языке
	 * @return перевод переменной <VAR>msgid</VAR>, в зависимости от переменной
	 *         <VAR>count</VAR>
	 */
	public static native String ngettext(final String domain, final String msgid,
			String msgid_plural, int count) /*-{
		return $wnd.GettextInstance.ngettext(msgid, msgid_plural, count);
	}-*/;

	/**
	 * Устанавливает имя используемого для перевода .po-файла без расширения как
	 * глобальную переменную.
	 * 
	 * @param domain
	 *            - имя используемого для перевода .po-файла без расширения
	 */
	public static native void setGettextVariable(final String domain) /*-{
		$wnd.GettextInstance = new $wnd.Gettext({
			"domain" : domain
		});
	}-*/;

}
