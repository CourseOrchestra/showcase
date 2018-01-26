package ru.curs.showcase.core.selector;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.core.command.DataPanelElementCommand;
import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Команда получения данных селектора компоненты XFORMS.
 * 
 * @author bogatov
 * 
 */
public class SelectorGetCommand extends DataPanelElementCommand<ResultSelectorData> {
	private final DataRequest dataRequest;

	public SelectorGetCommand(final DataRequest aDataRequest) {
		super((CompositeContext) aDataRequest.getAddData().getContext(),
				(DataPanelElementInfo) aDataRequest.getAddData().getElementInfo());
		this.dataRequest = aDataRequest;
	}

	@Override
	protected void preProcess() {

	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	protected void mainProc() throws Exception {
		SelectorGatewayFactory gf = new SelectorGatewayFactory(this.dataRequest.getProcName());
		SelectorGateway gateway = gf.getGateway();
		ResultSelectorData result = gateway.getData(dataRequest);
		// Начало перевода с помощью Gettext
		for (DataRecord dr : result.getDataRecordList()) {
			dr.setName(UserDataUtils.modifyVariables(dr.getName()));
		}
		// Конец перевода с помощью Gettext
		if (result.getOkMessage() == null) {
			result.setOkMessage(getContext().getOkMessage());
		}
		setResult(result);
	}

}
