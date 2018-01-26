package ru.curs.showcase.core.primelements.datapanel;

import java.io.*;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.primelements.PrimElementsGateway;
import ru.curs.showcase.util.*;

/**
 * Шлюз для элемента DataPanel, источник данных для которого является Celesta.
 * 
 * @author bogatov
 * 
 */
public class DataPanelCelestaGateway implements PrimElementsGateway {
	private String procName;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context) {
		CelestaHelper<String> helper = new CelestaHelper<String>(context, String.class) {
			@Override
			protected Object[] mergeAddAndGeneralParameters(final CompositeContext context,
					final Object[] additionalParams) {
				return additionalParams;
			}
		};
		// String json = XMLUtils.convertXmlToJson(context.getSession());
		String json = null;
		try {
			json = XMLJSONConverter.xmlToJson(context.getSession());
		} catch (SAXException | IOException e) {
			throw new XMLJSONConverterException(e);
		}
		String result = helper.runPython(procName, new Object[] { context.getMain(), json });
		InputStream stream = TextUtils.stringToStream(result);
		return new DataFile<InputStream>(stream, procName);
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final String aSourceName) {
		setSourceName(aSourceName);
		return getRawData(aContext);
	}

	@Override
	public void setSourceName(final String aSourceName) {
		this.procName = aSourceName;
	}

	@Override
	public void close() {
	}

}
