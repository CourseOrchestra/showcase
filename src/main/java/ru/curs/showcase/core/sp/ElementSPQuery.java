package ru.curs.showcase.core.sp;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Помощник для загрузки информации об элементе инф. панели.
 * 
 * @author den
 * 
 */
public abstract class ElementSPQuery extends SPQuery {

	/**
	 * Информация об элементе, данные которого загружает процедура.
	 */
	private DataPanelElementInfo elementInfo = null;

	/**
	 * Проверенные (XSD) настройки элемента.
	 */
	private InputStream validatedSettings;

	/**
	 * Данные, полученные из тега поля settings.
	 */
	private InputStream xmlDS = null;

	/**
	 * Функция для настройки общих параметров запроса: контекста и фильтров.
	 * 
	 * @throws SQLException
	 */
	protected void setupGeneralElementParameters() throws SQLException {
		setupGeneralParameters();

		setStringParam(getElementIdIndex(getTemplateIndex()), elementInfo.getId().getString());
	}

	/**
	 * Возвращает индекс OUT параметра с настройками элемента.
	 * 
	 * @return - индекс параметра.
	 */
	protected abstract int getOutSettingsParam();

	/**
	 * Стандартная функция подготовки CallableStatement для выборки данных для
	 * элемента информационной панели.
	 * 
	 * @throws SQLException
	 * 
	 */
	protected void prepareStdStatement() throws SQLException {
		prepareSQL();
		setupGeneralElementParameters();
		getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
	}

	/**
	 * Новая функция подготовки CallableStatement - с возвратом кода ошибки.
	 */
	protected void prepareElementStatementWithErrorMes() throws SQLException {
		super.prepareStatementWithErrorMes();
		setupGeneralElementParameters();
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
	}

	/**
	 * Определяет, нужно ли загружать данные и метаданные отдельно.
	 */
	protected boolean separatedMetadataLoad() {
		return elementInfo.getMetadataProc() != null;
	}

	/**
	 * Стандартная процедура инициализации.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param aElementInfo
	 *            - инф. об элементе.
	 */
	protected void init(final CompositeContext aContext, final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
		setContext(aContext);

		// setProcName(elementInfo.getProcName());
		if (aContext.getPartialUpdate()) {
			setProcName(elementInfo.getProcByType(DataPanelElementProcType.PARTIALUPDATE)
					.getName());
		} else if ((aContext instanceof GridContext)
				&& (((GridContext) aContext).getUpdateParents())) {
			setProcName(elementInfo.getProcByType(DataPanelElementProcType.UPDATEPARENTS)
					.getName());
		} else {
			setProcName(elementInfo.getProcName());
		}

	}

	/**
	 * Возвращает поток с настройками элемента, который можно использовать с SAX
	 * парсере.
	 */
	protected InputStream getValidatedSettings() throws SQLException {
		if (validatedSettings == null) {
			fillValidatedSettings();
		}
		return validatedSettings;
	}

	protected void fillValidatedSettings() throws SQLException {
		InputStream settings = getInputStreamForXMLParam(getOutSettingsParam());
		if (settings != null) {
			if (getSettingsSchema() != null) {
				validatedSettings = XMLUtils.xsdValidateAppDataSafe(settings, getSettingsSchema());
			} else {
				validatedSettings = settings;
			}
		}
	}

	protected void setValidatedSettings(final InputStream aValidatedSettings) {
		validatedSettings = aValidatedSettings;
	}

	public InputStream getXmlDS() {
		return xmlDS;
	}

	public void setXmlDS(final InputStream aXmlDS) {
		xmlDS = aXmlDS;
	}

	/**
	 * Возвращает имя схемы для проверки свойств элемента.
	 */
	protected String getSettingsSchema() {
		return getElementInfo().getType().getSettingsSchemaName();
	}

	private int getElementIdIndex(final int index) {
		return getMainContextIndex() + 2 + 2;
	}

}
