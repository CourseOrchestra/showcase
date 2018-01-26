package ru.curs.showcase.core.grid;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Шлюз для получения настроек элемента grid, где источником данных является
 * celesta скрипта.
 * 
 * @author bogatov
 * 
 */
public class GridCelestaSettingsGateway implements ElementSettingsGateway {

	@Override
	public RecordSetElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo element) {
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context,
				JythonDTO.class);
		String procName = element.getMetadataProc().getName();
		JythonDTO result = helper.runPython(procName, element.getId()
				.getString());

		RecordSetElementRawData rawData = new RecordSetElementRawData(element,
				context);
		if (result.getSettings() != null) {
			InputStream inSettings = TextUtils.stringToStream(result
					.getSettings());
			rawData.setSettings(inSettings);
		}

		return rawData;
	}

	@Override
	public Object getSession() {
		return null;
	}

}
