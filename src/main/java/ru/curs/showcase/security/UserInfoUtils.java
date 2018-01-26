package ru.curs.showcase.security;

import java.io.InputStream;
import java.util.*;

import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.*;

/**
 * Класс утилит для получения информации о пользователе (деталей, таких как
 * e-mail, sid пользователя, полное имя пользователя, телефон), например
 * получаемой из AuthServer.
 * 
 * @author anlug
 * 
 */
public final class UserInfoUtils {

	private UserInfoUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Метод для обработки полученного XML для извлечения из него данных.
	 * 
	 * @param is
	 *            - поток.
	 * @throws TransformerException
	 *             вызывается при ошибке трансформации.
	 */
	public static List<UserInfo> parseStream(final InputStream is) throws TransformerException {
		final List<UserInfo> result = new LinkedList<UserInfo>();
		final ContentHandler ch = new DefaultHandler() {
			@Override
			public void startElement(final String uri, final String localName,
					final String prefixedName, final Attributes atts) throws SAXException {
				if ("user".equals(localName)) {
					String[] params = new String[atts.getLength()];
					String[] values = new String[atts.getLength()];
					for (int i = 0; i < atts.getLength(); i++) {
						params[i] = atts.getLocalName(i);
						values[i] = atts.getValue(params[i]);
						AppInfoSingleton.getAppInfo().getAdditionalParametersList().add(params[i]);
					}
					UserInfo ui =
						new UserInfo(atts.getValue("login"), atts.getValue("SID"),
								atts.getValue("name"), atts.getValue("email"),
								atts.getValue("phone"), values);
					result.add(ui);
				}
			}
		};
		// TODO нужна ли здесь трансформация или хватит про SAXParser
		XSLTransformerPoolFactory.getTransformerFactory().newTransformer()
				.transform(new StreamSource(is), new SAXResult(ch));
		return result;
	}

}