package ru.curs.showcase.app.server;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.apache.tomcat.websocket.WsSession;
import org.json.*;

import ru.curs.lyra.BasicGridForm;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.LyraGridContext;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.security.SignedUsernamePasswordAuthenticationToken;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Вебсокет для обработки обратного движения ползунка лирагрид.
 * 
 */
@ServerEndpoint("/secured/JSLyraGridScrollBack")
public class JSLyraGridScrollBack {

	@OnMessage
	public void onMessage(final String msg, final Session session) {
		try {
			JSONTokener jt = new JSONTokener(msg);
			JSONObject jo = new JSONObject(jt);

			String stringLyraGridContext = jo.getString("gridContextValue");
			if (stringLyraGridContext == null) {
				throw new HTTPRequestRequiredParamAbsentException(LyraGridContext.class.getName());
			}
			String stringElementInfo = jo.getString("elementInfoValue");
			if (stringElementInfo == null) {
				throw new HTTPRequestRequiredParamAbsentException(PluginInfo.class.getName());
			}

			LyraGridContext context =
				(LyraGridContext) ServletUtils.deserializeObject(stringLyraGridContext);
			DataPanelElementInfo element =
				(DataPanelElementInfo) ServletUtils.deserializeObject(stringElementInfo);

			GridUtils.fillFilterContextByFilterInfo(context);

			if (((WsSession) session)
					.getUserPrincipal() instanceof SignedUsernamePasswordAuthenticationToken) {

				UserAndSessionDetails usd =
					(UserAndSessionDetails) ((SignedUsernamePasswordAuthenticationToken) ((WsSession) session)
							.getUserPrincipal()).getDetails();

				CompositeContextOnBasisOfUserAndSessionDetails contextWithSessionContext =
					new CompositeContextOnBasisOfUserAndSessionDetails(usd);

				for (Entry<String, ArrayList<String>> entry : context.getSessionParamsMap()
						.entrySet()) {
					ArrayList<String> values = new ArrayList<String>();
					for (String value : entry.getValue()) {
						values.add(value);
					}
					contextWithSessionContext.getSessionParamsMap().put(entry.getKey(), values);
				}
				for (Entry<ID, CompositeContext> entry : context.getRelated().entrySet()) {
					contextWithSessionContext.getRelated().put(entry.getKey(),
							entry.getValue().gwtClone());
				}
				contextWithSessionContext
						.setCurrentDatapanelWidth(context.getCurrentDatapanelWidth());
				contextWithSessionContext
						.setCurrentDatapanelHeight(context.getCurrentDatapanelHeight());

				XMLSessionContextGenerator generator =
					new XMLSessionContextGenerator(contextWithSessionContext, element);
				String sessionContext = generator.generate();

				context.setSession(sessionContext);

			}

			LyraGridGateway lgateway = new LyraGridGateway();
			BasicGridForm basicGridForm = lgateway.getLyraFormInstance(context, element,
					((WsSession) session).getHttpSessionId());

			((LyraGridScrollBack) basicGridForm.getChangeNotifier()).setWebSocketSession(session);

		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}
	}

	@OnError
	public void onError(final Session session, final Throwable thr) {
	}
}