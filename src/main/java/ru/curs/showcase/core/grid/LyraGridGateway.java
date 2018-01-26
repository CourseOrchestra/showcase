package ru.curs.showcase.core.grid;

import ru.curs.lyra.BasicGridForm;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;

/**
 * Шлюз для лиры-грида.
 */

public class LyraGridGateway {

	public BasicGridForm getLyraFormInstance(final CompositeContext context,
			final DataPanelElementInfo element) {

		CelestaHelper<BasicGridForm> helper =
			new CelestaHelper<BasicGridForm>(context, BasicGridForm.class);
		String procName = element.getProcName();
		BasicGridForm result = helper.runLyraPython(procName, element.getId().getString());

		return result;

	}

	public BasicGridForm getLyraFormInstance(final CompositeContext context,
			final DataPanelElementInfo element, final String currentSessionId) {

		CelestaHelper<BasicGridForm> helper =
			new CelestaHelper<BasicGridForm>(context, BasicGridForm.class);
		String procName = element.getProcName();
		BasicGridForm result =
			helper.runLyraPython(procName, element.getId().getString(), currentSessionId);

		return result;

	}

}
