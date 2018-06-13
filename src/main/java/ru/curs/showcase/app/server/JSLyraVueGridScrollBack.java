package ru.curs.showcase.app.server;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.apache.tomcat.websocket.WsSession;
import org.json.*;

import ru.curs.lyra.BasicGridForm;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.LyraGridContext;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.security.SignedUsernamePasswordAuthenticationToken;
import ru.curs.showcase.util.UserAndSessionDetails;
import ru.curs.showcase.util.xml.*;

/**
 * Вебсокет для обработки обратного движения ползунка лирагрид.
 * 
 */
@ServerEndpoint("/secured/JSLyraVueGridScrollBack")
public class JSLyraVueGridScrollBack {

	private static final String FORMCLASS = "formClass";
	private static final String INSTANCEID = "instanceId";

	private static final String MAINCONTEXT = "mainContext";
	private static final String ADDCONTEXT = "addContext";

	private static final String USERDATA = "userdata";

	@OnMessage
	public void onMessage(final String msg, final Session session) {
		try {

			JSONTokener jt = new JSONTokener(msg);
			JSONObject jo = new JSONObject(jt);

			String formClass = jo.getString(FORMCLASS);
			if (formClass == null) {
				throw new HTTPRequestRequiredParamAbsentException(FORMCLASS);
			}
			String instanceId = jo.getString(INSTANCEID);
			if (instanceId == null) {
				throw new HTTPRequestRequiredParamAbsentException(INSTANCEID);
			}

			String mainContext = jo.getString(MAINCONTEXT);
			String addContext = jo.getString(ADDCONTEXT);

			String userdata = jo.getString(USERDATA);

			LyraGridContext context = new LyraGridContext();
			context.setMain(mainContext);
			context.setAdditional(addContext);

			if (userdata != null) {
				ArrayList<String> al = new ArrayList<String>(1);
				al.add(userdata);
				context.getSessionParamsMap().put(USERDATA, al);
			}

			DataPanelElementInfo elInfo = new DataPanelElementInfo();
			elInfo.setProcName(formClass);
			elInfo.setId(instanceId);

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
					new XMLSessionContextGenerator(contextWithSessionContext, elInfo);
				String sessionContext = generator.generate();

				context.setSession(sessionContext);

			}

			LyraGridGateway lgateway = new LyraGridGateway();
			BasicGridForm basicGridForm = lgateway.getLyraFormInstance(context, elInfo,
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