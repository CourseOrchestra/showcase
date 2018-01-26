package ru.curs.showcase.core.event;

import java.util.*;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.element.VoidElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Команда, выполняющая серверные действия.
 * 
 * @author den
 * 
 */
public final class ExecServerActivityCommand extends ServiceLayerCommand<VoidElement> {

	private static final String SERVER_ACTION_EXECUTED = "Выполнено действие на сервере: ";

	@InputParam
	public Action getAction() {
		return action;
	}

	private final Action action;

	public ExecServerActivityCommand(final Action aAction) {
		super(aAction.getContext());
		action = aAction;
	}

	/**
	 * Контекст действия в случае server activity должен быть задан.
	 * 
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#initSessionContext()
	 **/
	@Override
	protected void initSessionContext() {
		CompositeContext context = action.getContext();
		if (context.getSession() != null) {
			return;
		}
		XMLSessionContextGenerator generator = new XMLSessionContextGenerator(context);
		String sessionContext = generator.generate();
		action.setSessionContext(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		action.setSessionContext((Map<String, List<String>>) null);
	}

	@Override
	protected void mainProc() throws Exception {
		ActivityGateway gateway = null;
		UserMessage okMessage = null;
		for (Activity act : action.getServerActivities()) {
			ServerActivitySelector selector = new ServerActivitySelector(act);
			gateway = selector.getGateway();
			gateway.exec(act);
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				LOGGER.info(SERVER_ACTION_EXECUTED + getSerializer().serialize(act));
			}

			if ((okMessage == null) && (act.getContext().getOkMessage() != null)) {
				okMessage = act.getContext().getOkMessage();
			}
		}

		VoidElement ve = new VoidElement();
		ve.setOkMessage(okMessage);
		setResult(ve);

	}
}
