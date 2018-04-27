package ru.curs.showcase.app.server.websockets;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.python.core.PyObject;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.celesta.CelestaException;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.exception.BaseException;
import ru.curs.showcase.util.xml.*;

/**
 * Класс, реализующий вспомогательный фунционал для WebSockets.
 * 
 * @author s.borodanev
 *
 */
public class WebSocketManager {

	/**
	 * Карта имён челеста-процедур и соответствующих им интервалов, через
	 * которые происходит их повторный вызов. Соответствует файлу
	 * websockets.xml.
	 */
	private final Map<String, Integer> procIntervalMap = Collections
			.synchronizedMap(new HashMap<String, Integer>());

	public Map<String, Integer> getProcIntervalMap() {
		return procIntervalMap;
	}

	static private class ShowcaseWebSocketException extends BaseException {

		private static final long serialVersionUID = 6725288887093385522L;

		ShowcaseWebSocketException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	public WebSocketManager() {
		File websocketsXML =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + File.separator
					+ "websockets.xml");
		if (websocketsXML.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(websocketsXML);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (fis != null) {
				XMLUtils.xsdValidateAppDataSafe(fis, "websockets.xsd");
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			DefaultHandler myHandler = new DefaultHandler() {
				@Override
				public void startElement(final String namespaceURI, final String lname,
						final String qname, final Attributes attrs) throws SAXException {
					if ("websocket".equals(lname)) {
						Integer refreshInterval = null;
						String aProcName = null;
						for (int i = 0; i < attrs.getLength(); i++) {
							String aname = attrs.getLocalName(i);
							if (aname.equals("refreshInterval"))
								refreshInterval = Integer.valueOf(attrs.getValue(i));
							if (aname.equals("proc")) {
								aProcName = attrs.getValue(i);
								final int tri = 3;
								final int vosem = 8;
								if (aProcName.endsWith(".cl")) {
									aProcName = aProcName.substring(0, aProcName.length() - tri);
								}
								if (aProcName.endsWith(".celesta")) {
									aProcName = aProcName.substring(0, aProcName.length() - vosem);
								}
							}
						}
						if (refreshInterval != null && aProcName != null) {
							getProcIntervalMap().put(aProcName, refreshInterval);
						}
					}
				}

			};

			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			try (InputStream fisss = new FileInputStream(websocketsXML);) {
				factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
				SAXParser saxParser = factory.newSAXParser();
				saxParser.parse(fisss, myHandler);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				throw new SAXError(e);
			}

		}
	}

	/**
	 * Метод, запускающий челеста-процедуру.
	 * 
	 * @param procName
	 *            имя челеста-процедуры
	 * 
	 * @return экземпляр класса, содержащего строку, пришедшую из
	 *         челеста-процедуры
	 */
	public JythonDTO execProc(final String procName) {
		JythonDTO object = null;

		String tempSesId = "WebSocketsSesId";
		try {
			AppInfoSingleton.getAppInfo().getCelestaInstance().login(tempSesId, "userCelestaSid");
			AppInfoSingleton.getAppInfo().getSessionSidsMap().put(tempSesId, "userCelestaSid");

			PyObject pObj =
				AppInfoSingleton.getAppInfo().getCelestaInstance().runPython(tempSesId, procName);

			Object obj = pObj.__tojava__(Object.class);

			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(JythonDTO.class)) {
				return (JythonDTO) obj;
			}

		} catch (CelestaException e) {
			throw new ShowcaseWebSocketException(ExceptionType.SOLUTION,
					"При запуске процедуры Celesta для WebSocket произошла ошибка: "
							+ e.getMessage());
		} finally {
			try {
				AppInfoSingleton.getAppInfo().getCelestaInstance().logout(tempSesId, false);
				AppInfoSingleton.getAppInfo().getSessionSidsMap().remove(tempSesId);
			} catch (Exception e) {
				throw new ShowcaseWebSocketException(ExceptionType.SOLUTION,
						"Пля выполнении WebSocket операции произошла ошибка при попытке выйти из сессии в celesta: "
								+ e.getMessage());
			}
		}
		return null;
	}

}
