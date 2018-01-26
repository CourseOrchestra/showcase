package ru.curs.showcase.core.html.plugin;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.*;

/**
 * Шлюз для пост-обработки данных плагинов.
 * 
 * @author den
 * 
 */
public final class PluginPostProcessJythonGateway extends JythonQuery<JythonDTO> {

	public PluginPostProcessJythonGateway(final CompositeContext aContext,
			final PluginInfo aElementInfo, final String aData) {
		super(JythonDTO.class);
		context = aContext;
		elementInfo = aElementInfo;
		data = aData;
	}

	private final CompositeContext context;
	private final PluginInfo elementInfo;
	private final String data;

	public String[] postProcess() {
		runTemplateMethod();
		return getResult().getDataArray();
	}

	@Override
	protected Object execute() {
		return getProc().postProcess(context, elementInfo.getId().getString(), data);
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getPostProcessProcName();
	}

}
