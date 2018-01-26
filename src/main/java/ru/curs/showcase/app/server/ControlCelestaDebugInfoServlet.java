package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import ru.curs.celesta.CallContext;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Сервлет для контроля используемой Showcase памяти.
 */
public class ControlCelestaDebugInfoServlet extends HttpServlet {
	public static final String UNKNOWN_PARAM_ERROR = "Неизвестное значение параметра trassert";
	public static final String NO_PARAMS_ERROR = "Должен быть задан параметр: trassert";
	public static final String TRASSERT_PARAM = "trassert";
	private static final long serialVersionUID = 2L;

	private static final Logger LOGGER =
		LoggerFactory.getLogger(ControlCelestaDebugInfoServlet.class);

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			StringBuilder sb = new StringBuilder();

			if (AppInfoSingleton.getAppInfo().getCelestaInstance().getActiveContexts()
					.size() > 0) {

				String taleStringRepresentation = " var tr123 = document.createElement('tr'); "
						+ "var td11 = document.createElement('td'); "
						+ "var td22 = document.createElement('td'); "
						+ "var td33 = document.createElement('td'); "
						+ "var td44 = document.createElement('td'); "
						+ "var h11 = document.createElement('h4'); "
						+ "var h22 = document.createElement('h4'); "
						+ "var h33 = document.createElement('h4'); "
						+ "var h44 = document.createElement('h4'); "
						+ "var text11 = document.createTextNode('ProcName'); "
						+ "var text22 = document.createTextNode('UserId'); "
						+ "var text33 = document.createTextNode('DBPid'); "
						+ "var text44 = document.createTextNode('StartTime'); "
						+ "h11.appendChild(text11); " + "h22.appendChild(text22); "
						+ "h33.appendChild(text33); " + "h44.appendChild(text44); "
						+ "td11.appendChild(h11); " + "td22.appendChild(h22); "
						+ "td33.appendChild(h33); " + "td44.appendChild(h44); "
						+ "tr123.appendChild(td11); " + "tr123.appendChild(td22); "
						+ "tr123.appendChild(td33); " + "tr123.appendChild(td44); "
						+ "table.appendChild(tr123); ";

				sb.append(taleStringRepresentation);

				int i = 0;
				for (CallContext cc : AppInfoSingleton.getAppInfo().getCelestaInstance()
						.getActiveContexts()) {
					String procName = cc.getProcName();
					String userId = cc.getUserId();
					int dbPid = cc.getDBPid();
					String startTime = cc.getStartTime().toString();
					String taleStringRepresentation2 = "  tr" + i
							+ "= document.createElement('tr'); t" + i
							+ "td1 = document.createElement('td'); t" + i
							+ "td2 = document.createElement('td'); t" + i
							+ "td3 = document.createElement('td'); t" + i
							+ "td4 = document.createElement('td'); t" + i
							+ "text1 = document.createTextNode('" + procName + "'); t" + i
							+ "text2 = document.createTextNode('" + userId + "'); t" + i
							+ "text3 = document.createTextNode('" + dbPid + "'); t" + i
							+ "text4 = document.createTextNode('" + startTime + "'); t" + i
							+ "td1.appendChild(t" + i + "text1); t" + i + "td2.appendChild(t" + i
							+ "text2); t" + i + "td3.appendChild(t" + i + "text3); t" + i
							+ "td4.appendChild(t" + i + "text4); " + "tr" + i + ".appendChild(t"
							+ i + "td1); " + "tr" + i + ".appendChild(t" + i + "td2); " + "tr" + i
							+ ".appendChild(t" + i + "td3); " + "tr" + i + ".appendChild(t" + i
							+ "td4); " + "table.appendChild(tr" + i + "); ";
					++i;
					sb.append(taleStringRepresentation2);
				}
				response.getWriter().print(sb.toString());
			}
		} catch (Exception ex) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Ошибка инициализации celesta", ex);
			}
			AppInfoSingleton.getAppInfo().setCelestaInitializationException(ex);
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		String trassert = req.getParameter(TRASSERT_PARAM);

		if (trassert != null) {
			switch (trassert) {
			case "on":
				try {
					AppInfoSingleton.getAppInfo().getCelestaInstance().setProfilemode(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "off":
				try {
					AppInfoSingleton.getAppInfo().getCelestaInstance().setProfilemode(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				throw new ServletException(UNKNOWN_PARAM_ERROR);
			}
		}
	}

}
