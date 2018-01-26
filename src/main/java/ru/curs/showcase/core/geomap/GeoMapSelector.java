package ru.curs.showcase.core.geomap;

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
public class GeoMapSelector extends SourceSelector<RecordSetElementGateway<CompositeContext>> {

	@Override
	public RecordSetElementGateway<CompositeContext> getGateway() {
		RecordSetElementGateway<CompositeContext> gateway = null;
		switch (sourceType()) {
		case JYTHON:
			gateway = new RecordSetElementJythonGateway();
			break;
		case FILE:
			throw new NotImplementedYetException();
		default:
			gateway = new GeoMapDBGateway();
			break;
		}
		return gateway;
	}

	public GeoMapSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getProcName());
	}

}
