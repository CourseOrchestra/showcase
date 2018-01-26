package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Класс для выбора источника данных для шаблона XForms.
 * 
 * @author den
 * 
 */
public class XFormTemplateSelector extends SourceSelector<ElementPartsGateway> {

	public XFormTemplateSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getTemplateName());
	}

	public XFormTemplateSelector(final String ruleTemplateName) {
		super(ruleTemplateName);
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
		gateway.setType(SettingsFileType.XFORM);
		return gateway;
	}

}
