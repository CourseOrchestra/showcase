package ru.curs.showcase.core.selector;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.selector.DataRequest;
import ru.curs.showcase.core.jython.*;

/**
 * Шлюз для получения данных, источником которых являются Jython скрипты.
 * 
 * @author bogatov
 * 
 */
public class SelectorJythonGateway extends JythonQuery<ResultSelectorData>
		implements SelectorGateway {

	private CompositeContext context;
	private String jythonProcName;
	private DataSelectorAttributes attributes;

	protected SelectorJythonGateway() {
		super(ResultSelectorData.class);
	}

	@Override
	public ResultSelectorData getData(final DataRequest req) throws Exception {
		this.context = (CompositeContext) req.getAddData().getContext();
		this.attributes = new DataSelectorAttributes();
		setGetDataParameters(this.attributes, req);

		String procName = req.getProcName();
		if (procName.indexOf(Constants.PROCNAME_SEPARATOR) != -1) {
			// загрузка данных в 2 этапа (выполнение 2-х скриптов)
			String procCount =
				procName.substring(0, procName.indexOf(Constants.PROCNAME_SEPARATOR));
			String procList = procName.substring(procName.indexOf(Constants.PROCNAME_SEPARATOR)
					+ Constants.PROCNAME_SEPARATOR.length());

			// получение кол-ва
			runTemplateMethod(procCount);
			ResultSelectorData rsdCount = getResult();
			// получение данных
			runTemplateMethod(procList);
			ResultSelectorData rsdData = getResult();

			return new ResultSelectorData(rsdData.getDataRecordList(), rsdCount.getCount());

		} else {
			// единый скрипт получения данных и кол-во записей
			runTemplateMethod(procName);
			return getResult();
		}
	}

	private void runTemplateMethod(final String procName) {
		this.jythonProcName = procName;
		runTemplateMethod();
	}

	private void setGetCountParameters(final CountSelectorAttributes aAttributes,
			final DataRequest req) {
		setGeneralParameters(aAttributes);
		aAttributes.setParams(req.getParams());
		aAttributes.setCurValue(req.getCurValue());
		aAttributes.setStartsWith(req.isStartsWith());
	}

	private void setGetDataParameters(final DataSelectorAttributes aAttributes,
			final DataRequest req) {
		setGetCountParameters(aAttributes, req);
		aAttributes.setFirstRecord(req.getFirstRecord());
		aAttributes.setRecordCount(req.getRecordCount());
	}

	private void setGeneralParameters(final InputAttributes aAttributes) {
		aAttributes.setAddContext(context.getAdditional());
		aAttributes.setFilterinfo(context.getFilter());
		aAttributes.setMainContext(context.getMain());
		aAttributes.setSessionContext(context.getSession());
	}

	@Override
	protected Object execute() {
		return getProc().getSelectorData(context, attributes);
	}

	@Override
	protected String getJythonProcName() {
		return this.jythonProcName;
	}

}
