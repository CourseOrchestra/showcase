package ru.curs.showcase.core.selector;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Шлюз для получения данных, источником которых являются celesta.
 * 
 * @author bogatov
 * 
 */
public class SelectorCelestaGateway implements SelectorGateway {

	@Override
	public ResultSelectorData getData(final DataRequest req) throws Exception {
		CompositeContext context = (CompositeContext) req.getAddData().getContext();
		CelestaHelper<ResultSelectorData> helper =
			new CelestaHelper<ResultSelectorData>(context, ResultSelectorData.class);

		ResultSelectorData result = null;
		String procName = req.getProcName();
		if (procName.indexOf(Constants.PROCNAME_SEPARATOR) != -1) {
			// загрузка данных в 2 этапа (выполнение 2-х скриптов)
			String procCount =
				procName.substring(0, procName.indexOf(Constants.PROCNAME_SEPARATOR));
			String procList = procName.substring(procName.indexOf(Constants.PROCNAME_SEPARATOR)
					+ Constants.PROCNAME_SEPARATOR.length());

			// получение кол-ва
			ResultSelectorData rsdCount = helper.runPython(procCount, req.getParams(),
					req.getCurValue(), req.isStartsWith());
			// получение данных
			ResultSelectorData rsdData =
				helper.runPython(procList, req.getParams(), req.getCurValue(), req.isStartsWith(),
						req.getFirstRecord(), req.getRecordCount());

			result = new ResultSelectorData(rsdData.getDataRecordList(), rsdCount.getCount());
		} else {
			// единый скрипт получения данных и кол-во записей
			result = helper.runPython(procName, req.getParams(), req.getCurValue(),
					req.isStartsWith(), req.getFirstRecord(), req.getRecordCount());
		}

		if ((result != null) && (result.getDataRecordList() != null)) {
			for (DataRecord dr : result.getDataRecordList()) {
				dr.setId(XMLUtils.xmlServiceSymbolsToNormal(dr.getId()));
				dr.setName(XMLUtils.xmlServiceSymbolsToNormal(dr.getName()));
				if (dr.getParameters() != null) {
					for (String key : dr.getParameters().keySet()) {
						dr.getParameters().put(key,
								XMLUtils.xmlServiceSymbolsToNormal(dr.getParameters().get(key)));
					}
				}
			}
		}

		return result;
	}

}
