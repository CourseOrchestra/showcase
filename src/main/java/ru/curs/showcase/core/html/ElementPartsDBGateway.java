package ru.curs.showcase.core.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Шлюз к БД для загрузки частей, требуемых для построения элемента. Примером
 * частей являются шаблоны, трансформации, XSD схемы.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка шаблонов, схем или трансформаций для вебтекста или xforms из БД")
public class ElementPartsDBGateway implements ElementPartsGateway {
	private String sourceName;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		if (isEmpty()) {
			return new DataFile<InputStream>(null, sourceName);
		}
		ElementSettingsGateway gateway = new ElementSettingsDBGateway() {
			@Override
			protected String getSettingsSchema() {
				return null;
			}

			@Override
			public String getProcName() {
				return sourceName;
			}
		};

		try (RecordSetElementRawData data = gateway.getRawData(aContext, aElementInfo)) {
			data.prepareSettings();
			DataFile<InputStream> df = new DataFile<InputStream>(data.getSettings(), sourceName);
			df.setEncoding(TextUtils.JDBC_ENCODING);
			return df;
		}
	}

	@Override
	public DataFile<InputStream> getRawDataForPartTemplate(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		return getRawData(aContext, aElementInfo);
	}

	private boolean isEmpty() {
		return (sourceName == null) || sourceName.isEmpty();
	}

	@Override
	public void setSource(final String aSourceName) {
		sourceName = aSourceName;
	}

	@Override
	public void setType(final SettingsFileType aType) {
		// не используется
	}

}
