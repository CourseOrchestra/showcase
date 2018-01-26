package ru.curs.showcase.core.primelements;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

/**
 * Шлюз для получения данных об основных элементах - навигаторе и инф. панели -
 * из файловой системы.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для информационной панели или навигатора из файла")
public class PrimElementsFileGateway implements PrimElementsGateway {

	private InputStream stream;

	private String fileName;

	private final SettingsFileType fileType;

	public PrimElementsFileGateway(final SettingsFileType aFileType) {
		super();
		fileType = aFileType;
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final String aDataPanelId) {
		setSourceName(aDataPanelId);
		return getRawData(context);
	}

	@Override
	public void setSourceName(final String aDataPanelId) {
		fileName = aDataPanelId;
	}

	public String getSourceName() {
		return fileName;
	}

	@Override
	public void close() {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, fileName, fileType);
		}
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext) {
		try {
			if (new File(UserDataUtils.getUserDataCatalog() + "/"
					+ String.format("%s/%s", fileType.getFileDir(), fileName)).exists()) {
				stream =
					UserDataUtils.loadUserDataToStream(String.format("%s/%s",
							fileType.getFileDir(), fileName));
			} else {
				stream =
					UserDataUtils.loadGeneralToStream(String.format("%s/%s",
							fileType.getFileDir(), fileName));
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, fileName, SettingsFileType.DATAPANEL);
		}
		if (stream == null) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.DATAPANEL);
		}
		return new DataFile<InputStream>(stream, fileName);
	}
}
