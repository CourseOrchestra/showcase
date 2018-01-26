package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.jython.JythonDTO;

/**
 * Шлюз получения данных для jsForms используя Celesta скрипт.
 * 
 * @author bogatov
 *
 */
public class JsFormTemplateCelestaGateway implements JsFormTemplateGateway {
	private final String procName;

	public JsFormTemplateCelestaGateway(final String sProcName) {
		this.procName = sProcName;
	}

	@Override
	public JsFormData getData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context, JythonDTO.class);
		JythonDTO result = helper.runPython(procName, elementInfo.getId().getString());
		return new JsFormData(result.getData(), result.getSettings());
	}

}
