package ru.curs.showcase.core.html.jsForm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Шлюз для получения данных из файла.
 * 
 * @author bogatov
 *
 */
public class JsFormFileGateway implements JsFormTemplateGateway {
	/**
	 * Каталог для данных в userdata.
	 */
	public static final String DATA_DIR = "data";

	@Override
	public JsFormData getData(final CompositeContext context, final DataPanelElementInfo element) {
		JsFormData jsFormData = new JsFormData();
		String fileName = element.getTemplateName();
		try {
			byte[] bytes =
				Files.readAllBytes(Paths.get(AppInfoSingleton.getAppInfo().getCurUserData()
						.getPath()
						+ "/" + SettingsFileType.JSFORM.getFileDir() + "/" + fileName));
			jsFormData.setData(new String(bytes, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new SettingsFileExchangeException(fileName, e, SettingsFileType.DATA);
		}

		String settingsFileName =
			TextUtils.extractFileName(element.getTemplateName()) + ".settings.xml";
		String settingFileName =
			String.format("%s/%s/%s", DATA_DIR, element.getType().toString().toLowerCase(),
					settingsFileName);
		Path settingPath =
			Paths.get(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/"
					+ settingFileName);

		if (Files.exists(settingPath)) {
			try {
				byte[] bytes = Files.readAllBytes(settingPath);
				jsFormData.setSetting(new String(bytes, StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new SettingsFileExchangeException(settingFileName, e,
						SettingsFileType.XM_DATA);
			}
		}

		return jsFormData;
	}

}
