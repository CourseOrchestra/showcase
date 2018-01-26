package ru.curs.showcase.core.html.plugin;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.command.DataPanelElementCommand;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.plugin.*;

/**
 * Команда для создания UI плагина.
 * 
 * @author den
 * 
 */
public final class PluginCommand extends DataPanelElementCommand<Plugin> {
	private final RequestData requestData;

	public PluginCommand(final RequestData oRequestData) {
		super(oRequestData.getContext(), oRequestData.getElInfo());
		this.requestData = oRequestData;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.PLUGIN;
	}

	@Override
	protected void mainProc() throws Exception {
		PluginSelector selector = new PluginSelector(requestData);
		HTMLGateway wtgateway = selector.getGateway();
		HTMLBasedElementRawData rawWT = null;
		if (getElementInfo().getProcName() != null) {
			rawWT = wtgateway.getRawData(getContext(), getElementInfo());
		} else {
			rawWT = new HTMLBasedElementRawData(getElementInfo(), getContext());
		}
		PluginFactory factory = new PluginFactory(rawWT);
		setResult(factory.build());
	}

}
