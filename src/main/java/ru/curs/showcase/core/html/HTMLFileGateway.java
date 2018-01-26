package ru.curs.showcase.core.html;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;

/**
 * Шлюз для получения данных и настроек элемента из xml файла.
 * 
 * @author den
 * 
 */
public class HTMLFileGateway extends GeneralXMLHelper implements HTMLGateway {
	/**
	 * Каталог для данных в userdata.
	 */
	public static final String DATA_DIR = "data";

	private DataPanelElementInfo elementInfo;

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
		Document doc = null;
		InputStream settings = null;
		String fileName =
			String.format("%s/%s/%s", DATA_DIR, elementInfo.getType().toString().toLowerCase(),
					elementInfo.getProcName());
		File file =
			new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/" + fileName);
		if (!file.exists()) {
			// file =
			// new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
			// + UserDataUtils.GENERAL_RES_ROOT + "/" + fileName);
			File fileRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
			file =
				new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + "common.sys"
						+ "/" + fileName);
			if (!(file.exists())) {
				File[] files = fileRoot.listFiles();
				for (File f : files) {
					if (f.getName().startsWith("common.") && !("common.sys".equals(f.getName()))) {
						File fileN =
							new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
									+ f.getName() + "/" + fileName);
						if (fileN.exists()) {
							file = fileN;
							break;
						}
					}
				}
			}
			if (!file.exists()) {
				throw new SettingsFileOpenException(elementInfo.getProcName(),
						SettingsFileType.XM_DATA);
			}
		}
		try {
			DocumentBuilder db = XMLUtils.createBuilder();
			InputStream stream = null;
			if ((new File(UserDataUtils.getUserDataCatalog() + "/" + fileName)).exists()) {
				stream = UserDataUtils.loadUserDataToStream(fileName);
			} else {
				stream = UserDataUtils.loadGeneralToStream(fileName);
			}
			doc = db.parse(stream);
		} catch (SAXException | IOException e) {
			throw new SettingsFileExchangeException(elementInfo.getProcName(), e,
					SettingsFileType.XM_DATA);
		}
		fileName =
			String.format("%s/%s/%s", DATA_DIR, elementInfo.getType().toString().toLowerCase(),
					getSettingsFileName());
		file = new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/" + fileName);
		if (file.exists()) {
			try {
				settings = UserDataUtils.loadUserDataToStream(fileName);
			} catch (IOException e) {
				throw new SettingsFileExchangeException(getSettingsFileName(), e,
						SettingsFileType.XM_DATA);
			}
		} else {
			// file =
			// new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
			// + UserDataUtils.GENERAL_RES_ROOT + File.separator + fileName);
			// if (file.exists()) {
			try {
				settings = UserDataUtils.loadGeneralToStream(fileName);
			} catch (IOException e) {
				throw new SettingsFileExchangeException(getSettingsFileName(), e,
						SettingsFileType.XM_DATA);
			}
			// }
		}
		return new HTMLBasedElementRawData(doc, settings, elementInfo, context);
	}

	private String getSettingsFileName() {
		return TextUtils.extractFileName(elementInfo.getProcName()) + ".settings.xml";
	}

}
