package ru.curs.showcase.security.logging;

import java.util.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.security.logging.Event.TypeEvent;

public class SecurityLoggingCelestaGateway implements SecurityLoggingGateway {

	private final String procName;

	public SecurityLoggingCelestaGateway(final String sProcName) {
		super();
		this.procName = sProcName;
	}

	@Override
	public void doLogging(final Event event) throws Exception {
		CelestaHelper<String> helper =
			new CelestaHelper<String>(event.getContext(), String.class) {
				@Override
				protected Object[] mergeAddAndGeneralParameters(final CompositeContext context,
						final Object[] additionalParams) {
					return additionalParams;
				}
			};

		if (event.getTypeEvent() == TypeEvent.LOGINERROR
				|| event.getTypeEvent() == TypeEvent.SESSIONTIMEOUT) {
			String tempSesId = String.format("Logging%08X", (new Random()).nextInt());
			try {
				AppInfoSingleton.getAppInfo().getCelestaInstance().login(tempSesId,
						"userCelestaSid");
			} catch (Exception e) {
				e.printStackTrace();
			}

			helper.runPythonWithSessionSet(tempSesId, procName,
					new Object[] {
							// event.getContext(),
							event.getXml(), event.getTypeEvent().toString() });

		} else if (event.getTypeEvent() == TypeEvent.LOGIN) {

			int index1 = event.getXml().indexOf("<HttpSessionId>");
			int index2 = event.getXml().indexOf("</HttpSessionId>");
			String sesid = event.getXml().substring(index1 + "<HttpSessionId>".length(), index2);

			int index10 = event.getXml().indexOf("<UserName>");
			int index20 = event.getXml().indexOf("</UserName>");
			String username = event.getXml().substring(index10 + "<UserName>".length(), index20);

			if ("master".equals(username)) {
				try {
					AppInfoSingleton.getAppInfo().getCelestaInstance().login(sesid,
							"userCelestaSid");
				} catch (Exception e) {
					e.printStackTrace();
				}
				Map<String, List<String>> map = new HashMap<>();
				List<String> list = new ArrayList<>();
				list.add("userCelestaSid");
				map.put("sid", list);
				CompositeContext context = event.getContext();
				context.addSessionParams(map);
				helper = new CelestaHelper<String>(context, String.class) {
					@Override
					protected Object[] mergeAddAndGeneralParameters(final CompositeContext context,
							final Object[] additionalParams) {
						return additionalParams;
					}
				};
			}
			helper.runPythonWithSessionSet(sesid, procName,
					new Object[] {
							// event.getContext(),
							event.getXml(), event.getTypeEvent().toString() });
		} else {
			helper.runPython(procName,
					new Object[] {
							// event.getContext(),
							event.getXml(), event.getTypeEvent().toString() });
		}
	}
}
