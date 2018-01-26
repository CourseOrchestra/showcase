package ru.curs.showcase.core.selector;

import ru.curs.showcase.app.api.selector.TreeDataRequest;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.jython.JythonDTO;

/**
 * Шлюз для получения данных, источником которых является celesta.
 * 
 */
public class TreeSelectorCelestaGateway implements TreeSelectorGateway {

	@Override
	public ResultTreeSelectorData getData(final TreeDataRequest request) throws Exception {
		CelestaHelper<JythonDTO> helper =
			new CelestaHelper<JythonDTO>(request.getContext(), JythonDTO.class);
		String procName = request.getElInfo().getGetDataProcName();

		JythonDTO jytResult = helper.runPython(procName, request.getParams(),
				request.getCurValue(), request.isStartsWith(), request.getParentId());

		ResultTreeSelectorData result = new ResultTreeSelectorData();
		result.setData(jytResult.getData());
		return result;
	}

}
