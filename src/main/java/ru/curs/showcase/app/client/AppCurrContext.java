/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.constantsShowcase;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * Текущий контекст приложения (клиентской части) - СИНГЛЕТОН.
 * 
 * @author anlug
 * 
 */
public final class AppCurrContext extends ActionTransformer {

	private final constantsShowcase internationalizedMessages = GWT
			.create(constantsShowcase.class);

	/**
	 * Карта, отвечающая за полную отрисовку вкладки датапанели (т.н. состояние
	 * ready).
	 */
	private static HashMap<DataPanelElementInfo, Boolean> readyStateMap =
		new HashMap<DataPanelElementInfo, Boolean>();

	/**
	 * Карта, включающая элементы, пришедшие из действия.
	 */
	private static HashMap<DataPanelElementInfo, Boolean> fromActionElementsMap =
		new HashMap<DataPanelElementInfo, Boolean>();

	/**
	 * Карта, включающая элементы со свойствами neverShowInPanel и hideOnLoad,
	 * равными true.
	 */
	private static HashMap<DataPanelElementInfo, Boolean> neverShowInPanelElementsReadyStateMap =
		new HashMap<DataPanelElementInfo, Boolean>();

	/**
	 * Карта, включающая элементы, пришедшие из действия, со свойствами
	 * neverShowInPanel и hideOnLoad, равными true.
	 */
	private static HashMap<DataPanelElementInfo, Boolean> neverShowInPanelElementsFromActionMap =
		new HashMap<DataPanelElementInfo, Boolean>();

	private boolean navigatorItemSelected = false;

	private boolean webTextXformTrueStateForReadyStateMap = false;

	private boolean gridWithToolbarWebtextTrueStateForReadyStateMap = false;

	private boolean gridWithoutToolbarWebtextTrueStateForReadyStateMap = false;

	private boolean chartXformTrueStateForReadyStateMap = false;

	private boolean gridWithToolbarChartTrueStateForReadyStateMap = false;

	private boolean gridWithoutToolbarChartTrueStateForReadyStateMap = false;

	private boolean geoMapXformTrueStateForReadyStateMap = false;

	private boolean gridWithToolbarGeoMapTrueStateForReadyStateMap = false;

	private boolean gridWithoutToolbarGeoMapTrueStateForReadyStateMap = false;

	private boolean pluginXformTrueStateForReadyStateMap = false;

	private boolean gridWithToolbarPluginTrueStateForReadyStateMap = false;

	private boolean gridWithoutToolbarPluginTrueStateForReadyStateMap = false;

	private boolean gridWithToolbarGridTrueStateForReadyStateMap = false;

	/**
	 * Имя домена (имя пакетного файла без разширения) для перевода клиенсткой
	 * части Showcase с помощью Gettext.
	 */
	private String domain = "";

	/**
	 * Список id элементов, для которых были добавлены js и css из внешних
	 * файлов в DOM-модель главной страницы index.jsp (например, элементу
	 * информационной панели Plugin для работы внешнего компонента FleshD
	 * требуется подключить внешние js и css файлы). Данная переменная
	 * необходима для исключения дублирования кода в index.jsp
	 */
	private static List<String> listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel =
		new ArrayList<String>();

	public constantsShowcase getInternationalizedMessages() {
		return internationalizedMessages;
	}

	/**
	 * Переменная хранящая в структуре Map закэшированные элементы
	 * BasicElementPanelBasis, которые "закэшированы".
	 */
	private static HashMap<String, BasicElementPanelBasis> mapOfDataPanelElementsToBeCached =
		new HashMap<String, BasicElementPanelBasis>();

	/**
	 * @return the mapOfDataPanelElements
	 */
	public HashMap<String, BasicElementPanelBasis> getMapOfDataPanelElements() {
		return mapOfDataPanelElementsToBeCached;
	}

	/**
	 * @param amapOfDataPanelElements
	 *            the mapOfDataPanelElements to set
	 */
	public void setMapOfDataPanelElements(
			final HashMap<String, BasicElementPanelBasis> amapOfDataPanelElements) {
		AppCurrContext.mapOfDataPanelElementsToBeCached = amapOfDataPanelElements;
	}

	/**
	 * Переменная хранящая объект MainPage приложения (в нем находится
	 * информация о главной странице, например высота заголовка и пр.).
	 */
	private static MainPage mainPage;

	/**
	 * Переменная хранящая текущее состояние приложение (версия, текущее имя
	 * пользователя и т.п.).
	 */
	private static ServerState serverCurrentState;

	/**
	 * Синглетон клиентской части приложения.
	 */
	private static AppCurrContext appCurrContext;

	/**
	 * Содержит текущий открытый ProgressWindow (окно прогресса). Если свойство
	 * равно null, то нет открытого окна прогресса.
	 */
	private static ProgressWindow progressWindow;

	/**
	 * MainPanel для текущей сессии.
	 */
	private static MainPanel mainPanel;

	/**
	 * Переменная которая хранит в себе ссылку на текущее открытое окно (скорее
	 * всего модальное) с элементами информационной панели. Если ссылка равно
	 * null то активного открытого окна не открыто.
	 */
	private static WindowWithDataPanelElement currentOpenWindowWithDataPanelElement;

	/**
	 * Переменная регистрации обработчика onSelect на TabPanel.
	 */
	private HandlerRegistration regTabPanelSelectionHandler;

	private Map<String, String> bundleMap;

	private Integer datapanelTabIndex = 0;

	private String navigatorItemId = null;

	/**
	 * @return the regTabPanelSelectionHandler
	 */
	public HandlerRegistration getRegTabPanelSelectionHandler() {
		return regTabPanelSelectionHandler;
	}

	/**
	 * @param aregTabPanelSelectionHandler
	 *            the regTabPanelSelectionHandler to set
	 */
	public void setRegTabPanelSelectionHandler(
			final HandlerRegistration aregTabPanelSelectionHandler) {
		this.regTabPanelSelectionHandler = aregTabPanelSelectionHandler;
	}

	/**
	 * Метаданные информационной панели.
	 */
	private static DataPanel dataPanelMetaData;

	/**
	 * Коллекция объектов, которая связывает UI элементы DataPanel (вкладки и
	 * элементы на вкладках) c метаданными.
	 */
	private static List<UIDataPanelTab> uiDataPanel = new ArrayList<UIDataPanelTab>();

	/**
	 * @return the uiWidgetsAndData
	 */
	public List<UIDataPanelTab> getUiDataPanel() {
		return uiDataPanel;
	}

	/**
	 * @param auiDataPanel
	 *            the List<UIDataPanelTab> to set
	 */
	public void setUiDataPanel(final List<UIDataPanelTab> auiDataPanel) {
		uiDataPanel = auiDataPanel;
	}

	private AppCurrContext() {
		super();
	}

	/**
	 * @return возвращает синглетон AppCurrContext.
	 */
	public static AppCurrContext getInstance() {
		if (appCurrContext == null) {
			appCurrContext = new AppCurrContext();
			AppCurrContext.currentOpenWindowWithDataPanelElement = null;
			AppCurrContext.serverCurrentState = null;
			AppCurrContext.mainPage = null;
		}
		return appCurrContext;
	}

	/**
	 * @param acurrentOpenWindowWithDataPanelElement
	 *            the currentOpenWindowWithDataPanelElement to set
	 */
	public void setCurrentOpenWindowWithDataPanelElement(
			final WindowWithDataPanelElement acurrentOpenWindowWithDataPanelElement) {
		AppCurrContext.currentOpenWindowWithDataPanelElement =
			acurrentOpenWindowWithDataPanelElement;
	}

	/**
	 * @return the currentOpenWindowWithDataPanelElement
	 */
	public WindowWithDataPanelElement getCurrentOpenWindowWithDataPanelElement() {
		return currentOpenWindowWithDataPanelElement;
	}

	/**
	 * @param adataPanelMetaData
	 *            the dataPanelMetaData to set
	 */
	public void setDataPanelMetaData(final DataPanel adataPanelMetaData) {
		dataPanelMetaData = adataPanelMetaData;
	}

	/**
	 * @return the dataPanelMetaData
	 */
	public DataPanel getDataPanelMetaData() {
		return dataPanelMetaData;
	}

	/**
	 * @param aserverCurrentState
	 *            the serverCurrentState to set
	 */
	public void setServerCurrentState(final ServerState aserverCurrentState) {
		serverCurrentState = aserverCurrentState;
	}

	/**
	 * @return the serverCurrentState
	 */
	public ServerState getServerCurrentState() {
		return serverCurrentState;
	}

	/**
	 * @param amainPanel
	 *            the mainPanel to set
	 */
	public void setMainPanel(final MainPanel amainPanel) {
		AppCurrContext.mainPanel = amainPanel;
	}

	/**
	 * @return the mainPanel
	 */
	public MainPanel getMainPanel() {
		return mainPanel;
	}

	/**
	 * @param aprogressWindow
	 *            the progressWindow to set
	 */
	public void setProgressWindow(final ProgressWindow aprogressWindow) {
		AppCurrContext.progressWindow = aprogressWindow;
	}

	/**
	 * @return the progressWindow
	 */
	public ProgressWindow getProgressWindow() {
		return progressWindow;
	}

	/**
	 * @param amainPage
	 *            the mainPage to set
	 */
	public void setMainPage(final MainPage amainPage) {
		AppCurrContext.mainPage = amainPage;
	}

	/**
	 * @return the mainPage
	 */
	public MainPage getMainPage() {
		return mainPage;
	}

	/**
	 * @return the listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel
	 */
	public List<String> getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel() {
		return listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel;
	}

	/**
	 * @param listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel
	 *            the listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel
	 *            to set
	 */
	public void setListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel(
			final List<String> aListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel) {
		AppCurrContext.listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel =
			aListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String aDomain) {
		domain = aDomain;
	}

	public void setBundleMap(final Map<String, String> aBundleMap) {
		bundleMap = aBundleMap;
	}

	public Map<String, String> getBundleMap() {
		return bundleMap;
	}

	public void setDatapanelTabIndex(final int aDatapanelTabIndex) {
		datapanelTabIndex = aDatapanelTabIndex;
	}

	public Integer getDatapanelTabIndex() {
		return datapanelTabIndex;
	}

	public void setNavigatorItemId(final String aNavigatorItemId) {
		navigatorItemId = aNavigatorItemId;
	}

	public String getNavigatorItemId() {
		return navigatorItemId;
	}

	public static HashMap<DataPanelElementInfo, Boolean> getReadyStateMap() {
		return readyStateMap;
	}

	public static void setReadyStateMap(HashMap<DataPanelElementInfo, Boolean> aMap) {
		readyStateMap = aMap;
	}

	public static HashMap<DataPanelElementInfo, Boolean> getFromActionElementsMap() {
		return fromActionElementsMap;
	}

	public static void setFromActionElementsMap(HashMap<DataPanelElementInfo, Boolean> aMap) {
		fromActionElementsMap = aMap;
	}

	public static HashMap<DataPanelElementInfo, Boolean>
			getNeverShowInPanelElementsReadyStateMap() {
		return neverShowInPanelElementsReadyStateMap;
	}

	public static void setNeverShowInPanelElementsReadyStateMap(
			HashMap<DataPanelElementInfo, Boolean> aMap) {
		neverShowInPanelElementsReadyStateMap = aMap;
	}

	public static HashMap<DataPanelElementInfo, Boolean>
			getNeverShowInPanelElementsFromActionMap() {
		return neverShowInPanelElementsFromActionMap;
	}

	public static void setNeverShowInPanelElementsFromActionMap(
			HashMap<DataPanelElementInfo, Boolean> aMap) {
		neverShowInPanelElementsFromActionMap = aMap;
	}

	public boolean getWebTextXformTrueStateForReadyStateMap() {
		return webTextXformTrueStateForReadyStateMap;
	}

	public void setWebTextXformTrueStateForReadyStateMap(boolean state) {
		webTextXformTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithToolbarWebtextTrueStateForReadyStateMap() {
		return gridWithToolbarWebtextTrueStateForReadyStateMap;
	}

	public void setGridWithToolbarWebtextTrueStateForReadyStateMap(boolean state) {
		gridWithToolbarWebtextTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithoutToolbarWebtextTrueStateForReadyStateMap() {
		return gridWithoutToolbarWebtextTrueStateForReadyStateMap;
	}

	public void setGridWithoutToolbarWebtextTrueStateForReadyStateMap(boolean state) {
		gridWithoutToolbarWebtextTrueStateForReadyStateMap = state;
	}

	public boolean getChartXformTrueStateForReadyStateMap() {
		return chartXformTrueStateForReadyStateMap;
	}

	public void setChartXformTrueStateForReadyStateMap(boolean state) {
		chartXformTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithToolbarChartTrueStateForReadyStateMap() {
		return gridWithToolbarChartTrueStateForReadyStateMap;
	}

	public void setGridWithToolbarChartTrueStateForReadyStateMap(boolean state) {
		gridWithToolbarChartTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithoutToolbarChartTrueStateForReadyStateMap() {
		return gridWithoutToolbarChartTrueStateForReadyStateMap;
	}

	public void setGridWithoutToolbarChartTrueStateForReadyStateMap(boolean state) {
		gridWithoutToolbarChartTrueStateForReadyStateMap = state;
	}

	public boolean getGeoMapXformTrueStateForReadyStateMap() {
		return geoMapXformTrueStateForReadyStateMap;
	}

	public void setGeoMapXformTrueStateForReadyStateMap(boolean state) {
		geoMapXformTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithToolbarGeoMapTrueStateForReadyStateMap() {
		return gridWithToolbarGeoMapTrueStateForReadyStateMap;
	}

	public void setGridWithToolbarGeoMapTrueStateForReadyStateMap(boolean state) {
		gridWithToolbarGeoMapTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithoutToolbarGeoMapTrueStateForReadyStateMap() {
		return gridWithoutToolbarGeoMapTrueStateForReadyStateMap;
	}

	public void setGridWithoutToolbarGeoMapTrueStateForReadyStateMap(boolean state) {
		gridWithoutToolbarGeoMapTrueStateForReadyStateMap = state;
	}

	public boolean getPluginXformTrueStateForReadyStateMap() {
		return pluginXformTrueStateForReadyStateMap;
	}

	public void setPluginXformTrueStateForReadyStateMap(boolean state) {
		pluginXformTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithToolbarPluginTrueStateForReadyStateMap() {
		return gridWithToolbarPluginTrueStateForReadyStateMap;
	}

	public void setGridWithToolbarPluginTrueStateForReadyStateMap(boolean state) {
		gridWithToolbarPluginTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithoutToolbarPluginTrueStateForReadyStateMap() {
		return gridWithoutToolbarPluginTrueStateForReadyStateMap;
	}

	public void setGridWithoutToolbarPluginTrueStateForReadyStateMap(boolean state) {
		gridWithoutToolbarPluginTrueStateForReadyStateMap = state;
	}

	public boolean getGridWithToolbarGridTrueStateForReadyStateMap() {
		return gridWithToolbarGridTrueStateForReadyStateMap;
	}

	public void setGridWithToolbarGridTrueStateForReadyStateMap(boolean state) {
		gridWithToolbarGridTrueStateForReadyStateMap = state;
	}

	public boolean getNavigatorItemSelected() {
		return navigatorItemSelected;
	}

	public void setNavigatorItemSelected(boolean state) {
		navigatorItemSelected = state;
	}
}
