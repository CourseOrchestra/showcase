package ru.curs.showcase.core.chart;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Класс для выбора шлюза по имени источника для графиков.
 * 
 * @author den
 * 
 */
public class ChartSelector extends SourceSelector<RecordSetElementGateway<CompositeContext>> {

	@Override
	public RecordSetElementGateway<CompositeContext> getGateway() {
		RecordSetElementGateway<CompositeContext> gateway = null;
		switch (sourceType()) {
		case CELESTA:
			gateway = new ChartCelestaGateway();
			break;
		case JYTHON:
			gateway = new RecordSetElementJythonGateway();
			break;
		case FILE:
			throw new NotImplementedYetException();
		default:
			gateway = new ChartDBGateway();
			break;
		}
		return gateway;
	}

	public ChartSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getProcName());
	}

}
