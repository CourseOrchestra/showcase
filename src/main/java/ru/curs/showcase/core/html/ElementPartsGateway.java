package ru.curs.showcase.core.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Интерфейс шлюза для загрузки частей, требуемых для построения элемента.
 * 
 * @author den
 * 
 */
public interface ElementPartsGateway {
	DataFile<InputStream> getRawData(CompositeContext aContext, DataPanelElementInfo aElementInfo);

	DataFile<InputStream> getRawDataForPartTemplate(CompositeContext aContext,
			DataPanelElementInfo aElementInfo);

	void setSource(String aSourceName);

	void setType(SettingsFileType aType);
}
