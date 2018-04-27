package ru.curs.showcase.app.server.websockets;

import java.io.IOException;
import java.util.*;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Вэбсокет, для обмена текстовыми данными с клиентом (js-скриптом).
 * 
 * @author s.borodanev
 *
 */

@ServerEndpoint("/secured/ws")
public class WebSocketsEndpoint {

	@OnMessage
	public void onMessage(final String msg, final Session session) {
		WebSocketManager manager = new WebSocketManager();
		Timer t = new Timer();

		if (AppInfoSingleton.getAppInfo().getProcTimerMap().get(session.getId()) == null)
			AppInfoSingleton.getAppInfo().getProcTimerMap()
					.put(session.getId(), new HashMap<String, Timer>());

		if (!msg.startsWith("stop"))
			AppInfoSingleton.getAppInfo().getProcTimerMap().get(session.getId())
					.put(msg.toLowerCase(), t);

		for (String fullProcName : manager.getProcIntervalMap().keySet()) {

			if (fullProcName.contains(msg)) {

				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						JythonDTO object = manager.execProc(fullProcName);

						try {
							if (object != null)
								session.getBasicRemote().sendText(object.getData());
						} catch (IOException e) {
						}

					}
				};

				int period = manager.getProcIntervalMap().get(fullProcName);
				if (period < 0)
					t.schedule(tt, 0);
				else
					t.schedule(tt, 0, period);

			}
		}

		if (msg.startsWith("stop")) {
			String proc = msg.substring("stop".length());
			AppInfoSingleton.getAppInfo().getProcTimerMap().get(session.getId())
					.get(proc.toLowerCase()).cancel();
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		AppInfoSingleton.getAppInfo().getProcTimerMap().clear();
	}
}
