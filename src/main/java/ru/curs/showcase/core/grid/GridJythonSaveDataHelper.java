package ru.curs.showcase.core.grid;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.jython.JythonQuery;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Сохраняет отредактированные данные в гриде.
 * 
 */
public class GridJythonSaveDataHelper extends JythonQuery<GridSaveResult> {
	private final DataPanelElementInfo elementInfo;
	private final CompositeContext context;

	protected GridJythonSaveDataHelper(final CompositeContext aContext,
			final DataPanelElementInfo aElement) {
		super(GridSaveResult.class);
		context = aContext;
		elementInfo = aElement;
	}

	@Override
	protected Object execute() {
		String xml = null;
		try {
			xml = XMLJSONConverter.jsonToXml(((GridContext) context).getEditorData());
		} catch (JSONException | TransformerException | ParserConfigurationException e) {
			throw new XMLJSONConverterException(e);
		}
		xml = xml.substring(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8.length()).trim();
		return getProc().gridSaveData(context, elementInfo.getId().getString(), xml);
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getProcByType(DataPanelElementProcType.SAVE).getName();
	}

}
