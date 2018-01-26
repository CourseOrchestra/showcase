package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.command.*;

/**
 * Базовый класс для всех команд XForms.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип результата.
 */
public abstract class XFormContextCommand<T> extends DataPanelElementCommand<T> {
	@Override
	@InputParam
	public XFormContext getContext() {
		return (XFormContext) super.getContext();
	}

	public XFormContextCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.XFORMS;
	}
}
