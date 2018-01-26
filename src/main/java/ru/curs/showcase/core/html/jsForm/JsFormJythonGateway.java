package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.JythonQuery;

/**
 * Шлюз получения данных для jsForms используя Jython.
 * 
 * @author bogatov
 * 
 */
public class JsFormJythonGateway extends JythonQuery<String> implements JsFormSubmitGateway {
	private CompositeContext context;
	private DataPanelElementInfo elementInfo;
	private final String procName;
	private String data;

	public JsFormJythonGateway(final String proc) {
		super(String.class);
		this.procName = proc;
	}

	@Override
	public String getData(final CompositeContext aContext, final DataPanelElementInfo aElementInfo,
			final String inData) {
		context = aContext;
		elementInfo = aElementInfo;
		data = inData;
		runTemplateMethod();
		return getResult();
	}

	@Override
	protected Object execute() {
		return getProc().submiJsForm(context, elementInfo.getId().getString(), data);
	}

	@Override
	protected String getJythonProcName() {
		return procName;
	}

}
