package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.command.DataPanelElementCommand;

/**
 * Команда отправки данных элемента jsForm.
 * 
 */
public class JsFormSubmitCommand extends DataPanelElementCommand<String> {
	private static final String NO_SUBMIT_PROC_ERROR = "Не найдена submit процедура для linkId=";

	private final ID linkId;
	private final String data;

	public JsFormSubmitCommand(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo, final ID alinkId, final String oData) {
		super(aContext, aElementInfo);
		this.linkId = alinkId;
		this.data = oData;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.JSFORM;
	}

	@Override
	protected void mainProc() throws Exception {
		DataPanelElementProc proc = getElementInfo().getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_SUBMIT_PROC_ERROR + linkId);
		}
		JsFormSubmitSelector selector = new JsFormSubmitSelector(proc.getName());
		JsFormSubmitGateway gateway = selector.getGateway();
		String rData = gateway.getData(getContext(), getElementInfo(), this.data);
		setResult(rData);
	}

}
