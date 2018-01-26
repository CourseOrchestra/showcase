package ru.curs.showcase.core.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Шлюз к Celesta для загрузки частей, требуемых для построения элемента.
 * 
 */
public class ElementPartsCelestaGateway implements ElementPartsGateway {
	private String sourceName;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context, JythonDTO.class);
		JythonDTO result =
			helper.runPython(elementInfo.getTemplateName(), elementInfo.getId().getString());
		InputStream data = TextUtils.stringToStream(result.getData());
		return new DataFile<InputStream>(data, sourceName);
	}

	@Override
	public DataFile<InputStream> getRawDataForPartTemplate(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context, JythonDTO.class);
		JythonDTO result = helper.runPython(sourceName, elementInfo.getId().getString());
		InputStream data = TextUtils.stringToStream(result.getData());
		return new DataFile<InputStream>(data, sourceName);
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
