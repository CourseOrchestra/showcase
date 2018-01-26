package ru.curs.showcase.core.command;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.ElementInfoChecker;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Базовый класс команды для работы с элементом инф. панели - его загрузкой или
 * обработкой.
 * 
 * @author den
 * 
 * @param <T>
 *            - класс результата.
 */
public abstract class DataPanelElementCommand<T> extends ServiceLayerCommand<T> {

	private final DataPanelElementInfo elementInfo;

	public DataPanelElementCommand(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		super(aContext);
		elementInfo = aElementInfo;
	}

	@InputParam
	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	@Override
	protected void preProcess() {
		super.preProcess();
		ElementInfoChecker checker = new ElementInfoChecker();
		checker.check(elementInfo, getRequestedElementType());
	}

	protected abstract DataPanelElementType getRequestedElementType();

	@Override
	protected DataPanelElementContext generateDataPanelElementContext() {
		return new DataPanelElementContext(getContext(), elementInfo);
	}

	@Override
	protected XMLSessionContextGenerator setupGenerator() {
		return new XMLSessionContextGenerator(getContext(), elementInfo);
	}
}
