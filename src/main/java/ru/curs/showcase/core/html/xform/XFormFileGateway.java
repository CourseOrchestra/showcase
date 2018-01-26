package ru.curs.showcase.core.html.xform;

import java.io.*;

import javax.xml.transform.TransformerException;

import org.slf4j.*;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Шлюз для работы с файлами данных XForms. Используется в отладочных целях.
 * TODO пока сделан просто вывод данных о полученном файле или submission в
 * консоль и upload файла из classes по его имени.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для XForm из файлов")
public final class XFormFileGateway extends HTMLFileGateway implements HTMLAdvGateway {

	private static final Logger LOGGER = LoggerFactory.getLogger(XFormFileGateway.class);
	/**
	 * Тестовый каталог для данных, сохраняемых через шлюз.
	 */
	public static final String TMP_TEST_DATA_DIR = "tmp/tmp.test.data";

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo element,
			final String data) {
		String fileName =
			String.format(TMP_TEST_DATA_DIR + "/%s_updated.xml", element.getProcName());
		try {
			XMLUtils.stringToXMLFile(data, fileName);
		} catch (SAXException | IOException | TransformerException e) {
			throw new SettingsFileExchangeException(fileName, e, SettingsFileType.XM_DATA);
		}
	}

	@Override
	public String scriptTransform(final String aProcName, final XFormContext aInputData) {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info(String.format(
					"Заглушка: выполнение Submission процедуры '%s' c данными формы  %s",
					aProcName, aInputData));
		}
		return null;
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final ID linkId) {
		StreamConvertor dup;
		try {
			dup = new StreamConvertor(FileUtils.loadClassPathResToStream(linkId.getString()));
		} catch (IOException e) {
			throw new SettingsFileExchangeException(linkId.getString(), e,
					SettingsFileType.XM_DATA);
		}
		OutputStreamDataFile file =
			new OutputStreamDataFile(dup.getOutputStream(), linkId.getString());
		file.setEncoding(TextUtils.JDBC_ENCODING);
		return file;
	}

	@Override
	public void uploadFile(final XFormContext aContext, final DataPanelElementInfo aElementInfo,
			final ID aLinkId, final DataFile<InputStream> aFile) {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info(String
					.format("Заглушка: сохранение файла '%s' с контекстом %s из элемента %s, ссылка %s, данные формы %s",
							aFile.getName(), aContext, aElementInfo, aLinkId,
							aContext.getFormData()));
		}

	}
}
