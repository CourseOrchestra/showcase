package ru.curs.showcase.app.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.GridUtils;
import ru.curs.showcase.core.selector.*;

/**
 * Реализация сервиса для селектора.
 */
public class SelectorDataServiceImpl extends RemoteServiceServlet implements SelectorDataService {

	private static final long serialVersionUID = 8719830458845626545L;

	@Override
	public DataSet getSelectorData(final DataRequest req) {
		DataSet ds = new DataSet();
		try {

			if (req.getAddData().getContext() instanceof GridContext) {
				GridUtils.fillFilterContextByListOfValuesInfo(
						(GridContext) req.getAddData().getContext());
			}

			SelectorGetCommand command = new SelectorGetCommand(req);
			ResultSelectorData result = command.execute();

			ds.setFirstRecord(req.getFirstRecord());
			ds.setRecords(result.getDataRecordList());
			ds.setTotalCount(result.getCount());

			ds.setOkMessage(result.getOkMessage());

		} catch (GeneralException e) {
			// вернётся пустой датасет.
			ds.setTotalCount(0);

			throw e;
		} catch (Exception e) {
			// вернётся пустой датасет.
			ds.setTotalCount(0);

			throw GeneralExceptionFactory.build(e);
		}

		return ds;
	}
}
