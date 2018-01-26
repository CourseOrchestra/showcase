package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementProc;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для сохранения XForms.
 * 
 * @author den
 * 
 */
public class XFormSaveSelector extends SourceSelector<HTMLAdvGateway> {

	public XFormSaveSelector(final DataPanelElementProc proc) {
		super(proc.getName());
	}

	public XFormSaveSelector(final String procName) {
		super(procName);
	}

	@Override
	public HTMLAdvGateway getGateway() {
		HTMLAdvGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new XFormJythonGateway();
			break;
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
				res = new HtmlMSSQLExecGateway();
				break;
			case POSTGRESQL:
				res = new HtmlPostgreSQLExecGateway();
				break;
			default:
				throw new NotImplementedYetException();
			}
			break;
		case FILE:
			res = new XFormFileGateway();
			break;
		case CELESTA:
			return new XFormCelestaGateway();
		default:
			res = new HtmlDBGateway();
		}
		return res;
	}
}
