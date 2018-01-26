package ru.curs.showcase.core.plugin;

import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.util.XMLJSONConverter;

/**
 * Шлюз для получения данных, источником которых является celesta.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginCelestaGateway implements GetDataPluginGateway {

	@Override
	public ResultPluginData getData(final RequestData request) throws Exception {
		CelestaHelper<JythonDTO> helper =
			new CelestaHelper<JythonDTO>(request.getContext(), JythonDTO.class);
		String procName = request.getElInfo().getGetDataProcName();
		// String paramsJson =
		// XMLUtils.convertXmlToJson(request.getXmlParams());
		String paramsJson = XMLJSONConverter.xmlToJson(request.getXmlParams());
		JythonDTO jytResult = helper.runPython(procName, paramsJson);
		ResultPluginData result = new ResultPluginData();
		result.setData(jytResult.getData());
		return result;
	}

}
