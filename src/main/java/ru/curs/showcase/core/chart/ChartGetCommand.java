package ru.curs.showcase.core.chart;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.command.DataPanelElementCommand;
import ru.curs.showcase.core.sp.*;

/**
 * Команда получения графика.
 * 
 * @author den
 * 
 */
public final class ChartGetCommand extends DataPanelElementCommand<Chart> {

	public ChartGetCommand(final CompositeContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void postProcess() {
		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(getResult());
		getResult().setJavaDynamicData(null);
	}

	@Override
	protected void mainProc() throws Exception {
		SourceSelector<RecordSetElementGateway<CompositeContext>> selector =
			new ChartSelector(getElementInfo());
		RecordSetElementGateway<CompositeContext> gateway = selector.getGateway();
		RecordSetElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		ChartFactory factory = new ChartFactory(raw);
		setResult(factory.build());
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.CHART;
	}

}
