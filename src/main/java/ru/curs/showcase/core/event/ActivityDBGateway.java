package ru.curs.showcase.core.event;

import java.sql.SQLException;

import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.util.Description;

/**
 * Класс шлюза-исполнителя вызовов SQL хранимых процедур.
 * 
 * @author den
 * 
 */
@Description(process = "Вызов хранимых процедур на сервере SQL")
public class ActivityDBGateway extends SPQuery implements ActivityGateway {

	private static final int ERROR_MES_INDEX = 6;

	@Override
	public void exec(final Activity activity) {
		try (SPQuery query = this) {
			try {
				setContext(activity.getContext());
				setProcName(activity.getName());

				prepareStatementWithErrorMes();
				setupGeneralParameters();
				execute();
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s (?, ?, ?, ?, ?)}";
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

}
