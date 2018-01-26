package ru.curs.showcase.core.chart;

import java.io.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * 
 * @author s.borodanev
 *
 */
public class ChartCelestaGateway implements RecordSetElementGateway<CompositeContext> {

	private static final String SAX_ERROR_MES = "обобщенные настройки (настройки плюс данные)";

	@Override
	public RecordSetElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		final String elementId = elementInfo.getId().getString();
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context, JythonDTO.class);
		JythonDTO result = helper.runPython(elementInfo.getProcName(), elementId);
		RecordSetElementRawData rawData = new RecordSetElementRawData(elementInfo, context);
		fillValidatedSettings(rawData, result.getSettings());
		if (rawData.getXmlDS() == null && result.getData() != null) {
			InputStream inData = TextUtils.stringToStream(result.getData());
			rawData.setXmlDS(inData);
		}
		return rawData;
	}

	protected void fillValidatedSettings(final RecordSetElementRawData rawData,
			final String settings) {
		if (settings != null) {
			InputStream inSettings = TextUtils.stringToStream(settings);

			ByteArrayOutputStream osSettings = new ByteArrayOutputStream();
			ByteArrayOutputStream osDS = new ByteArrayOutputStream();

			SimpleSAX sax =
				new SimpleSAX(inSettings, new StreamDivider(osSettings, osDS), SAX_ERROR_MES);
			sax.parse();

			InputStream isSettings = StreamConvertor.outputToInputStream(osSettings);
			String settingsSchemaName = rawData.getElementInfo().getType().getSettingsSchemaName();
			if (settingsSchemaName != null) {
				rawData.setSettings(XMLUtils
						.xsdValidateAppDataSafe(isSettings, settingsSchemaName));
			} else {
				rawData.setSettings(isSettings);
			}

			if (osDS.size() == 0) {
				rawData.setXmlDS(null);
			} else {
				rawData.setXmlDS(StreamConvertor.outputToInputStream(osDS));
			}
		}
	}

}
