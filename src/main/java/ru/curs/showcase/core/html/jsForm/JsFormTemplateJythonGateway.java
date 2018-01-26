package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.*;

/**
 * Шлюз получения данных для jsForms используя Jython.
 * 
 * @author bogatov
 * 
 */
public class JsFormTemplateJythonGateway extends JythonQuery<JythonDTO>
		implements JsFormTemplateGateway {
	private CompositeContext context;
	private DataPanelElementInfo elementInfo;
	private final String procName;

	public JsFormTemplateJythonGateway(final String proc) {
		super(JythonDTO.class);
		this.procName = proc;
	}

	@Override
	public JsFormData getData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		context = aContext;
		elementInfo = aElementInfo;
		runTemplateMethod();
		JythonDTO result = getResult();
		return new JsFormData(result.getData(), result.getSettings());
	}

	@Override
	protected Object execute() {
		return getProc().templateJsForm(context, elementInfo.getId().getString());
	}

	@Override
	protected String getJythonProcName() {
		return procName;
	}

}
