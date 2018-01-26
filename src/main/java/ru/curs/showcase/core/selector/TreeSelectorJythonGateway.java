package ru.curs.showcase.core.selector;

import ru.curs.showcase.app.api.selector.TreeDataRequest;
import ru.curs.showcase.core.jython.*;

/**
 * Шлюз для получения данных, источником которых являются Jython скрипты.
 * 
 */
public class TreeSelectorJythonGateway extends JythonQuery<JythonDTO>
		implements TreeSelectorGateway {
	private TreeDataRequest request;

	public TreeSelectorJythonGateway() {
		super(JythonDTO.class);
	}

	@Override
	public ResultTreeSelectorData getData(final TreeDataRequest aRequest) throws Exception {
		request = aRequest;
		runTemplateMethod();
		JythonDTO jytResult = getResult();
		ResultTreeSelectorData result = new ResultTreeSelectorData();
		result.setData(jytResult.getData());
		result.setOkMessage(jytResult.getUserMessage());
		return result;
	}

	@Override
	protected Object execute() {
		DataTreeSelectorAttributes attr = new DataTreeSelectorAttributes();

		attr.setParams(request.getParams());
		attr.setCurValue(request.getCurValue());
		attr.setStartsWith(request.isStartsWith());
		attr.setParentId(request.getParentId());

		return getProc().getTreeSelectorData(request.getContext(), attr);
	}

	@Override
	protected String getJythonProcName() {
		return request.getElInfo().getGetDataProcName();
	}

}
