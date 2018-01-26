package ru.curs.showcase.app.api.services;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.VoidElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.GridToolBar;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.plugin.*;

import com.google.gwt.user.client.rpc.*;

/**
 * Основной GWT-RPC интерфейс для приложения. Основное назначение - передача
 * данных для отображения в UI. Каждая функция должна объявлять GeneralException
 * даже несмотря на то, что это не контролируемое исключение для корректной
 * работы GWT-RPC.
 */
// CHECKSTYLE:OFF
@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	/**
	 * Функция, возвращающая невизуальный объект навигатора с данными для
	 * пользователя, совершившего вход в систему.
	 * 
	 * @return - объект навигатора.
	 * @throws GeneralException
	 * @param context
	 *            - контекст вызова. Содержит параметры URL.
	 */
	Navigator getNavigator(CompositeContext context) throws GeneralException;

	/**
	 * Возвращает информационную панель по переданному действию. Информационная
	 * панель формируется учитывая все контексты, переданные в действии.
	 * 
	 * @param action
	 *            - действие.
	 * @return - панель.
	 * @throws GeneralException
	 */
	DataPanel getDataPanel(Action action) throws GeneralException;

	/**
	 * Возвращает данные для отрисовки элемента типа WebText по переданному
	 * контексту и описанию элемента.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - WebText.
	 * @throws GeneralException
	 */
	WebText getWebText(CompositeContext context, DataPanelElementInfo element)
			throws GeneralException;

	/**
	 * Возвращает данные для отрисовки элемента типа Grid по переданным
	 * контексту, описанию элемента и требуемым настройкам.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - грид.
	 * @throws GeneralException
	 */

	GridMetadata getGridMetadata(GridContext context, DataPanelElementInfo element)
			throws GeneralException;

	GridData getGridData(GridContext context, DataPanelElementInfo element)
			throws GeneralException;

	LyraGridMetadata getLyraGridMetadata(LyraGridContext context, DataPanelElementInfo element)
			throws GeneralException;

	/**
	 * Возвращает данные для отрисовки графика по переданным контексту и
	 * описанию элемента.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - chart.
	 * @throws GeneralException
	 */
	Chart getChart(CompositeContext context, DataPanelElementInfo element) throws GeneralException;

	/**
	 * Возвращает данные для отрисовки карты.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @return - невизуальный объект карты.
	 * @throws GeneralException
	 */
	GeoMap getGeoMap(CompositeContext context, DataPanelElementInfo element)
			throws GeneralException;

	/**
	 * Возвращает данные для отрисовки формы. Передаваемые в контексте данные
	 * должны заменить данные из БД, реализуя т.об. обновление формы.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @return - логическая форма без данных.
	 * @throws GeneralException
	 */
	XForm getXForms(XFormContext context, DataPanelElementInfo element) throws GeneralException;

	/**
	 * Возвращает данные для отрисовки главной формы.
	 * 
	 * @return - главная форма.
	 * @throws GeneralException
	 */
	List<String> getMainXForms() throws GeneralException;

	/**
	 * Сохраняет данные карточки на основе XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @throws GeneralException
	 */
	VoidElement saveXForms(XFormContext context, DataPanelElementInfo element)
			throws GeneralException;

	/**
	 * Возвращает информацию о текущем состоянии сервера и о текущем сеансе.
	 * 
	 * @return - объект с информацией.
	 * @throws GeneralException
	 * @param context
	 *            -контекст, содержащий параметры URL.
	 */
	ServerState getServerCurrentState(CompositeContext context) throws GeneralException;

	/**
	 * Выполняет действие на сервере.
	 * 
	 * @param action
	 *            - действие.
	 * 
	 * @throws GeneralException
	 */
	VoidElement execServerAction(Action action) throws GeneralException;

	/**
	 * Возвращает данные для формирования главной страницы.
	 * 
	 * @param context
	 *            - контекст, содержащий параметры URL.
	 * 
	 * @throws GeneralException
	 */
	MainPage getMainPage(CompositeContext context) throws GeneralException;

	/**
	 * Возвращает данные для формирования UI плагина.
	 * 
	 * @param requestData
	 *            - параметры запроса.
	 */
	Plugin getPlugin(RequestData requestData) throws GeneralException;

	/**
	 * Возвращает данные для плагина.
	 * 
	 * @param request
	 *            данные запроса
	 * @return набор данных
	 */
	ResponceData getPluginData(RequestData request) throws GeneralException;

	/**
	 * Функция для записи в лог приложения на сервере из клиентской части. Все
	 * клиентские сообщения попадут в веб-консоль со специальным значением
	 * атрибута process = client.
	 * 
	 * @param context
	 *            - контекст.
	 * @param message
	 *            - сообщение.
	 * @param messageType
	 *            - тип сообщения: информация, предупреждение или ошибка.
	 */
	void writeToLog(CompositeContext aContext, String message, MessageType messageType)
			throws GeneralException;

	/**
	 * Функция получения панели инструментов грида
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @return панель инструментов
	 */
	GridToolBar getGridToolBar(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException;

	Map<String, String> getBundle(final CompositeContext context) throws GeneralException;

	String getLocalizationBundleDomainName(final CompositeContext context);

	void copyToClipboard(final String message);

	void fakeRPC();
}
