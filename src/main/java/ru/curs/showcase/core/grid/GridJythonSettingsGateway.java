package ru.curs.showcase.core.grid;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Шлюз для получения настроек элемента grid c помощью выполнения Jython
 * скрипта.
 * 
 * @author bogatov
 * 
 */
public class GridJythonSettingsGateway extends JythonQuery<JythonDTO> implements
		ElementSettingsGateway {
	private CompositeContext context;
	private DataPanelElementInfo element;

	public GridJythonSettingsGateway() {
		super(JythonDTO.class);
	}

	@Override
	public RecordSetElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElement) {
		this.context = aContext;
		this.element = aElement;

		RecordSetElementRawData rawData = new RecordSetElementRawData(aElement,
				aContext);
		runTemplateMethod();
		if (getResult().getSettings() != null) {
			InputStream inSettings = TextUtils.stringToStream(getResult()
					.getSettings());
			rawData.setSettings(inSettings);
		}

		return rawData;
	}

	@Override
	public Object getSession() {
		return null;
	}

	@Override
	protected Object execute() {
		return getProc().getRawData(context, element.getId().getString());
	}

	@Override
	protected String getJythonProcName() {
		return element.getMetadataProc().getName();
	}
}
