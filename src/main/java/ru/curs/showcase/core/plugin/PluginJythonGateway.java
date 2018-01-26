package ru.curs.showcase.core.plugin;

import java.io.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Шлюз для получения данных, источником которых являются jython скрипт.
 * 
 * @author bogatov
 * 
 */
public class PluginJythonGateway extends JythonQuery<JythonDTO> implements HTMLGateway {

	private final String xmlParams;

	public PluginJythonGateway(final String sXmlParams) {
		super(JythonDTO.class);
		this.xmlParams = sXmlParams;
	}

	private CompositeContext context;
	private DataPanelElementInfo elementInfo;

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		context = aContext;
		elementInfo = aElementInfo;

		runTemplateMethod();
		context.setOkMessage(getResult().getUserMessage());

		Document data = null;
		InputStream settings = null;
		try {
			data = XMLUtils.stringToDocument(getResult().getData());
		} catch (SAXException | IOException e) {
			throw new JythonException(RESULT_FORMAT_ERROR);
		}
		if (getResult().getSettings() != null) {
			settings = TextUtils.stringToStream(getResult().getSettings());
		}
		HTMLBasedElementRawData rawData =
			new HTMLBasedElementRawData(data, settings, elementInfo, context);
		return rawData;
	}

	@Override
	protected Object execute() {
		return getProc().getPluginRawData(context, elementInfo.getId().getString(), xmlParams);
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getProcName();
	}

}
