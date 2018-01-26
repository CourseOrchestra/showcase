package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;

/**
 * Класс для выбора источника данных JsForm.
 * 
 * @author bogatov
 *
 */
public class JsFormTemplateSelector extends SourceSelector<JsFormTemplateGateway> {

	public JsFormTemplateSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getTemplateName());
	}

	@Override
	public JsFormTemplateGateway getGateway() {
		JsFormTemplateGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new JsFormTemplateJythonGateway(getSourceName());
			break;
		case FILE:
			res = new JsFormFileGateway();
			break;
		case CELESTA:
			res = new JsFormTemplateCelestaGateway(getSourceName());
			break;
		default:
			res = new JsFormTemplateDBGateway(getSourceName());
		}
		return res;
	}

	@Override
	protected String getFileExt() {
		return "html";
	}

}
