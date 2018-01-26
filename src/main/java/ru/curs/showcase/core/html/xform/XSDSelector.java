package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.util.xml.*;

/**
 * Класс для выбора источника для XSD схемы.
 * 
 * @author den
 * 
 */
public class XSDSelector extends SourceSelector<XSDSource> {

	private final DataPanelElementContext context;

	public XSDSelector(final CompositeContext aContext, final DataPanelElementInfo aElInfo,
			final DataPanelElementProc proc) {
		super(proc.getSchemaName());
		context = new DataPanelElementContext(aContext, aElInfo);
	}

	@Override
	public XSDSource getGateway() {
		XSDSource result;
		switch (sourceType()) {
		case JYTHON:
			result = new JythonXSDSource(context, getSourceName());
			break;
		case FILE:
			result = new UserDataXSDSource();
			break;
		default:
			result = new DatabaseXSDSource(context, getSourceName());
		}
		return result;
	}

	@Override
	protected String getFileExt() {
		return "xsd";
	}

}
