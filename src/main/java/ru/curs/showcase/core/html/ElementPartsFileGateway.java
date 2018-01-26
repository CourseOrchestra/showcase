package ru.curs.showcase.core.html;

import java.io.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.*;

/**
 * Шлюз к файлу для загрузки частей, требуемых для построения элемента.
 * 
 * @author den
 * 
 */
public class ElementPartsFileGateway implements ElementPartsGateway {
	private String sourceName;
	private SettingsFileType type;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		String file = String.format("%s/%s", type.getFileDir(), sourceName);
		try {
			if ((new File(UserDataUtils.getUserDataCatalog() + "/" + file)).exists()) {
				return new DataFile<InputStream>(UserDataUtils.loadUserDataToStream(file),
						sourceName);
			} else {
				return new DataFile<InputStream>(UserDataUtils.loadGeneralToStream(file),
						sourceName);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, sourceName, type);
		}
	}

	@Override
	public DataFile<InputStream> getRawDataForPartTemplate(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return getRawData(context, elementInfo);
	}

	@Override
	public void setSource(final String aSourceName) {
		sourceName = aSourceName;

	}

	@Override
	public void setType(final SettingsFileType aType) {
		type = aType;
	}

}
