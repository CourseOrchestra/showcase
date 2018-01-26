package ru.curs.showcase.core.html.xform;

import java.util.Date;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.util.LoggerHelper;

/**
 * Команда получения xforms.
 * 
 * @author den
 * 
 */
public final class XFormGetCommand extends XFormContextCommand<XForm> {

	public XFormGetCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {

		Date dt1 = new Date();

		HtmlSelector selector = new HtmlSelector(getElementInfo());
		HTMLGateway gateway = selector.getGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		if (getContext().getKeepUserSettings() && (getContext().getFormData() != null)) {
			raw.setData(getContext().getFormData());
		}

		Date dt2 = new Date();
		LoggerHelper.profileToLog(getElementInfo().getFullId() + ". Работа с базой данных.", dt1,
				dt2, getElementInfo().getType().toString(), "");

		XFormFactory factory = new XFormFactory(raw);
		setResult(factory.build());

	}
}
