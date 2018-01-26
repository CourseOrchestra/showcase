package ru.curs.showcase.core.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.JythonQuery;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Шлюз к БД для загрузки частей, требуемых для построения элемента.
 * 
 * @author den
 * 
 */
public class ElementPartsJythonGateway extends JythonQuery<String> implements ElementPartsGateway {
	private String sourceName;
	private CompositeContext context;
	private DataPanelElementInfo elementInfo;

	public ElementPartsJythonGateway() {
		super(String.class);
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		context = aContext;
		elementInfo = aElementInfo;
		runTemplateMethod();
		InputStream data = TextUtils.stringToStream(getResult());
		return new DataFile<InputStream>(data, sourceName);
	}

	@Override
	public DataFile<InputStream> getRawDataForPartTemplate(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		return getRawData(aContext, aElementInfo);
	}

	@Override
	protected Object execute() {
		return getProc().getRawData(context, elementInfo.getId().getString());
	}

	@Override
	protected String getJythonProcName() {
		return sourceName;
	}

	@Override
	public void setSource(final String aSourceName) {
		sourceName = aSourceName;
	}

	@Override
	public void setType(final SettingsFileType aType) {
		// не используется
	}

}
