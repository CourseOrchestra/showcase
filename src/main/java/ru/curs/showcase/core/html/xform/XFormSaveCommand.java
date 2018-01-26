package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.VoidElement;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.HTMLAdvGateway;

/**
 * Команда сохранения xforms.
 * 
 * @author den
 * 
 */
public final class XFormSaveCommand extends XFormContextCommand<VoidElement> {

	public XFormSaveCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		DataPanelElementProc proc = getElementInfo().getSaveProc();

		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(getContext().getFormData(), proc, getContext(),
					getElementInfo());
		transformer.transform();

		XFormSaveSelector selector = new XFormSaveSelector(proc);
		HTMLAdvGateway gateway = selector.getGateway();
		gateway.saveData(getContext(), getElementInfo(), transformer.getStringResult());

		VoidElement ve = new VoidElement();
		ve.setOkMessage(getContext().getOkMessage());
		setResult(ve);
	}
}
