package ru.curs.showcase.util.xml;

import java.lang.reflect.*;

import org.xml.sax.Attributes;

import ru.curs.showcase.util.exception.ServerLogicError;

/**
 * Интерфейс обработчика дополнительных тэгов для SAX парсера.
 * 
 * @author den
 * 
 */
public abstract class SAXTagHandler extends GeneralXMLHelper {
	/**
	 * Определяет, может ли обработать тэг обработчик handleStartTag. Функция
	 * может использоваться как клиентом, так и внутри handleXXX функций
	 * объекта.
	 * 
	 * @param tagName
	 *            - имя тэга.
	 * @return - результат проверки.
	 */
	public boolean canHandleStartTag(final String tagName) {
		for (String tag : getStartTags()) {
			if (tag.equalsIgnoreCase(tagName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Возвращает список обрабатываемых стартовых тэгов.
	 */
	protected abstract String[] getStartTags();

	/**
	 * Определяет, может ли обработать тэг обработчик handleEndTag. Функция
	 * может использоваться как клиентом, так и внутри handleXXX функций
	 * объекта.
	 * 
	 * @param tagName
	 *            - имя тэга.
	 * @return - результат проверки.
	 */
	public boolean canHandleEndTag(final String tagName) {
		for (String tag : getEndTrags()) {
			if (tag.equalsIgnoreCase(tagName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Возвращает список обрабатываемых конечных тэгов.
	 */
	protected abstract String[] getEndTrags();

	/**
	 * Обработчик начала тэга.
	 * 
	 * @param namespaceURI
	 *            - пространство имен.
	 * @param lname
	 *            - локальное имя (без префикса).
	 * @param qname
	 *            - полное имя (с префиксом) - рекомендуется для использования.
	 * @param attrs
	 *            - атрибуты тэга.
	 * @return - null или объект, созданный при обработке тэга.
	 */
	public Object handleStartTag(final String namespaceURI, final String lname,
			final String qname, final Attributes attrs) {
		return standartHandler(qname, attrs, SaxEventType.STARTTAG);
	}

	/**
	 * Обработчик начала тэга.
	 * 
	 * @param namespaceURI
	 *            - пространство имен.
	 * @param lname
	 *            - локальное имя (без префикса).
	 * @param qname
	 *            - полное имя (с префиксом) - рекомендуется для использования.
	 * @return - null или объект, созданный при обработке тэга.
	 */
	public abstract Object handleEndTag(String namespaceURI, String lname, String qname);

	/**
	 * Обработчик содержимого тэга.
	 * 
	 * @param arg0
	 *            - текст.
	 * @param arg1
	 *            - начальная позиция в тексте.
	 * @param arg2
	 *            - длина текста.
	 */
	public abstract void handleCharacters(final char[] arg0, final int arg1, final int arg2);

	/**
	 * Стандартный обработчик тэгов. Для каждого передаваемого в функцию тэга
	 * должен быть определен обработчик.
	 * 
	 * @param qname
	 *            - имя тэга.
	 * @param attrs
	 *            - атрибуты тэга.
	 * @param type
	 *            - тип события SAX.
	 */
	protected Object standartHandler(final String qname, final Attributes attrs,
			final SaxEventType type) {
		String tag = qname.replace("_", "");
		String methodName = String.format("%s%sHandler", tag, type.toString());
		try {
			Method method = this.getClass().getMethod(methodName, Attributes.class);
			return method.invoke(this, attrs);
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
				| IllegalAccessException e) {
			throw new ServerLogicError(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof NoMainContextException) {
				throw new XMLError(e);
			}
			throw new SAXError(e.getTargetException());
		}
	}
}
