package ru.curs.showcase.core.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Класс для выбора источника XSL трансформации.
 * 
 * @author den
 * 
 */
public class XSLTransformSelector extends SourceSelector<ElementPartsGateway> {

	private final CompositeContext context;
	private final DataPanelElementInfo elInfo;

	public XSLTransformSelector(final CompositeContext aContext, final DataPanelElementInfo aElInfo) {
		super(aElInfo.getTransformName());
		context = aContext;
		elInfo = aElInfo;
	}

	public XSLTransformSelector(final CompositeContext aContext,
			final DataPanelElementInfo aElInfo, final DataPanelElementProc proc) {
		super(proc.getTransformName());
		context = aContext;
		elInfo = aElInfo;
	}

	@Override
	public ElementPartsGateway getGateway() {
		ElementPartsGateway gateway;
		switch (sourceType()) {
		case CELESTA:
			gateway = new ElementPartsCelestaGateway();
			break;
		case JYTHON:
			gateway = new ElementPartsJythonGateway();
			break;
		case FILE:
			gateway = new ElementPartsFileGateway();
			break;
		default:
			gateway = new ElementPartsDBGateway();
		}
		gateway.setSource(getSourceName());
		gateway.setType(SettingsFileType.XSLT);
		return gateway;
	}

	@Override
	protected String getFileExt() {
		return "xsl";
	}

	public DataFile<InputStream> getData() {
		return getGateway().getRawData(context, elInfo);
	}

}
