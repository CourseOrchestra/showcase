package ru.curs.showcase.core.plugin;

import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.jython.*;

/**
 * Шлюз для получения данных, источником которых являются Jython скрипты.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginJythonGateway extends JythonQuery<JythonDTO>
		implements GetDataPluginGateway {
	private RequestData request;

	public GetDataPluginJythonGateway() {
		super(JythonDTO.class);
	}

	@Override
	public ResultPluginData getData(final RequestData oRequest) throws Exception {
		this.request = oRequest;
		runTemplateMethod();
		JythonDTO jytResult = getResult();
		ResultPluginData result = new ResultPluginData();
		result.setData(jytResult.getData());
		result.setOkMessage(jytResult.getUserMessage());
		return result;
	}

	@Override
	protected Object execute() {
		PluginAttributes pluginAttributes = new PluginAttributes();
		pluginAttributes.setXmlParams(this.request.getXmlParams());
		return getProc().getPluginData(request.getContext(), pluginAttributes);
	}

	@Override
	protected String getJythonProcName() {
		return this.request.getElInfo().getGetDataProcName();
	}

}
