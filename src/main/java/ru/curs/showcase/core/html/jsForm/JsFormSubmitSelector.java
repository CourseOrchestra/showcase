package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.core.SourceSelector;

/**
 * Класс для выбора источника данных JsForm.
 * 
 * @author bogatov
 *
 */
public class JsFormSubmitSelector extends SourceSelector<JsFormSubmitGateway> {

	public JsFormSubmitSelector(final String procName) {
		super(procName);
	}

	@Override
	public JsFormSubmitGateway getGateway() {
		JsFormSubmitGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new JsFormJythonGateway(getSourceName());
			break;
		case CELESTA:
			res = new JsFormCelestaGateway(getSourceName());
			break;
		default:
			res = new JsFormDBGateway(getSourceName());
		}
		return res;
	}

}
