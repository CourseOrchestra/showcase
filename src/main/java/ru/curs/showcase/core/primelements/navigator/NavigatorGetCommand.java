package ru.curs.showcase.core.primelements.navigator;

import java.io.InputStream;
import java.util.Date;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.core.command.ServiceLayerCommand;
import ru.curs.showcase.core.primelements.PrimElementsGateway;
import ru.curs.showcase.util.*;

/**
 * Команда получения навигатора.
 * 
 * @author den
 * 
 */
public final class NavigatorGetCommand extends ServiceLayerCommand<Navigator> {

	public NavigatorGetCommand(final CompositeContext aContext) {
		super(aContext);
	}

	@Override
	protected void mainProc() {
		NavigatorSelector selector = new NavigatorSelector();

		try (PrimElementsGateway gw = selector.getGateway()) {

			Date dt1 = new Date();
			DataFile<InputStream> xml = gw.getRawData(getContext());
			Date dt2 = new Date();
			LoggerHelper.profileToLog("Navigator. Работа с базой данных.", dt1, dt2, "NAVIGATOR",
					"");

			NavigatorFactory factory = new NavigatorFactory(getContext());
			setResult(factory.fromStream(xml));
		}
	}
}
