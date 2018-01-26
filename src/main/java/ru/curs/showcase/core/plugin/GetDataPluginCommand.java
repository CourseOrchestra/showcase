package ru.curs.showcase.core.plugin;

import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.command.ServiceLayerCommand;
import ru.curs.showcase.core.html.plugin.PluginPostProcessJythonGateway;

/**
 * Команда получения данных для плагина.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginCommand extends ServiceLayerCommand<ResultPluginData> {
	private final RequestData requestData;

	public GetDataPluginCommand(final RequestData oRequestData) {
		super(oRequestData.getContext());
		this.requestData = oRequestData;
	}

	@Override
	protected void mainProc() throws Exception {
		GetDataPluginGatewayFactory gf =
			new GetDataPluginGatewayFactory(this.requestData.getElInfo().getGetDataProcName());
		GetDataPluginGateway gateway = gf.getGateway();
		ResultPluginData result = gateway.getData(this.requestData);
		if (this.requestData.getElInfo().getPostProcessProcName() != null) {
			PluginPostProcessJythonGateway postProcessGateway = new PluginPostProcessJythonGateway(
					this.requestData.getContext(), this.requestData.getElInfo(), result.getData());
			String[] params = postProcessGateway.postProcess();
			String data = "";
			for (int i = 0; i < params.length; i++) {
				if (i != 0) {
					data += ",";
				}
				data += params[i];
			}
			result.setData(data);
		}
		if (result.getOkMessage() == null) {
			result.setOkMessage(getContext().getOkMessage());
		}
		setResult(result);
	}

}
