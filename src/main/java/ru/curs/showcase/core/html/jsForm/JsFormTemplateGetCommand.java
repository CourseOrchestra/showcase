package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.JsForm;
import ru.curs.showcase.core.command.DataPanelElementCommand;

/**
 * Команда получения данных для элемента jsForm.
 * 
 */
public class JsFormTemplateGetCommand extends DataPanelElementCommand<JsForm> {

	public JsFormTemplateGetCommand(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		super(aContext, aElementInfo);
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.JSFORM;
	}

	@Override
	protected void mainProc() throws Exception {
		JsFormTemplateSelector templateSelector = new JsFormTemplateSelector(getElementInfo());
		JsFormTemplateGateway templateGateway = templateSelector.getGateway();
		JsFormData data = templateGateway.getData(getContext(), getElementInfo());

		JsFormFactory factory = new JsFormFactory(data, getContext(), getElementInfo());
		setResult(factory.build());
	}

}
