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
public class JsFormCelestaGateway implements JsFormSubmitGateway {
	private final String procName;

	public JsFormCelestaGateway(final String sProcName) {
		this.procName = sProcName;
	}

	@Override
	public String getData(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String inputData) {
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context, JythonDTO.class);
		JythonDTO jytResult =
			helper.runPython(procName, elementInfo.getId().getString(), inputData);
		return jytResult.getData();
	}

}
