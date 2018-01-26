package ru.curs.showcase.core.event;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * Абстрактная фабрика с шаблонным методом построения сложных объектов -
 * наследников FormattedDataPanelElement. Используется для построения таких
 * объектов, как грид, график, карта.
 * 
 * @author den
 * 
 */
public abstract class TemplateMethodFactory extends GeneralXMLHelper {

	protected static final String CHECK_ACTION_ERROR =
		"Некорректное описание действия в элементе инф. панели: ";

	/**
	 * Исходные сырые данные для построения элемента.
	 */
	private ElementRawData source;

	public TemplateMethodFactory(final ElementRawData aSource) {
		super();
		source = aSource;
	}

	protected final InputStream getSettings() {
		return source.getSettings();
	}

	public CompositeContext getCallContext() {
		return source.getCallContext();
	}

	public ElementRawData getSource() {
		return source;
	}

	public void setSource(final RecordSetElementRawData aSource) {
		source = aSource;
	}

	/**
	 * Функция, возвращающая результат работы фабрики.
	 * 
	 * @return - результат работы.
	 */
	public abstract DataPanelElement getResult();

	/**
	 * Основная функция построения объекта на основе данных, уже содержащихся в
	 * фабрике.
	 * 
	 * @return - DataPanelElement.
	 */
	public DataPanelElement build() throws Exception {
		initResult();
		prepareData();
		prepareSettings();
		checkSourceError();
		releaseResources();
		setupDynamicSettings();
		fillResultByData();
		postProcess();
		return getResult();
	}

	/**
	 * Метод для проверки на возврат источником кода ошибки. В случае ошибки
	 * процесс построения элемента прерывается.
	 */
	protected void checkSourceError() {
		// По умолчанию ничего не делает - данный функционал не всегда нужен
	}

	protected void postProcess() {

		getResult().setOkMessage(getCallContext().getOkMessage());

		getResult().actualizeActions(getCallContext());
		Action wrong = getResult().checkActions();
		if (wrong != null) {
			throw new IncorrectElementException(CHECK_ACTION_ERROR, wrong);
		}
		correctSettingsAndData();
	}

	/**
	 * Загружает динамические настройки результата из settings.
	 * 
	 */
	protected abstract void setupDynamicSettings();

	/**
	 * Метод, в котором происходит загрузка в результат динамических данных.
	 * 
	 * @throws SQLException
	 */
	protected abstract void fillResultByData() throws SQLException;

	/**
	 * Выполняет настройку свойств результата работы фабрики на основании
	 * динамических данных, загруженных в методе fillResultByData.
	 */
	protected abstract void correctSettingsAndData();

	/**
	 * Функция для инициализации результата работы фабрики. В данной функции
	 * создается объект, а также настраиваются его свойства по умолчанию.
	 * 
	 */
	protected abstract void initResult();

	/**
	 * Функция подготовки данных для создания объекта.
	 * 
	 * @throws ResultSetHandleException
	 */
	protected abstract void prepareData();

	/**
	 * Функция подготовки настроек для создания объекта.
	 * 
	 */
	protected abstract void prepareSettings();

	/**
	 * Функция освобождения ресурсов, необходимых для создания объекта.
	 * 
	 */
	protected abstract void releaseResources();

	public DataPanelElementInfo getElementInfo() {
		return source.getElementInfo();
	}

	/**
	 * Стандартный метод для замены строк шаблонов в элементах инф. панели.
	 * 
	 * @param in
	 *            - входная строка.
	 */
	protected String replaceVariables(final String in) {
		String out = in;
		out = out.replace(ELEMENT_ID, getElementInfo().getId());
		out = UserDataUtils.replaceVariables(out);
		return out;
	}

}
