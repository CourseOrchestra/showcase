package ru.curs.showcase.core.selector;

import ru.curs.showcase.app.api.selector.TreeDataRequest;
import ru.curs.showcase.core.command.ServiceLayerCommand;

/**
 * Команда получения данных нового триселектора.
 * 
 */
public class TreeSelectorGetCommand extends ServiceLayerCommand<ResultTreeSelectorData> {
	private final TreeDataRequest requestData;

	public TreeSelectorGetCommand(final TreeDataRequest oRequestData) {
		super(oRequestData.getContext());
		this.requestData = oRequestData;
	}

	@Override
	protected void mainProc() throws Exception {
		TreeSelectorGatewayFactory gf =
			new TreeSelectorGatewayFactory(this.requestData.getElInfo().getGetDataProcName());
		TreeSelectorGateway gateway = gf.getGateway();
		ResultTreeSelectorData result = gateway.getData(this.requestData);
		if (result.getOkMessage() == null) {
			result.setOkMessage(getContext().getOkMessage());
		}
		setResult(result);
	}

}
