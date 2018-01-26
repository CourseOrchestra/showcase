package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.server.AppInitializer;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.core.primelements.datapanel.DataPanelFactory;
import ru.curs.showcase.core.primelements.navigator.NavigatorFactory;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * Класс абстрактного теста, использующего тестовые файлы с данными.
 * 
 * @author den
 * 
 */
public class AbstractTest extends GeneralXMLHelper {

	private static final String TEST_GOOD_XSD = "test_good.xsd";
	private static final String TEST_GOOD_SMALL_XSD = "test_good_small.xsd";
	private static final String XFORMS_DOWNLOAD2 = "xforms_download2";
	protected static final String TEST_GOOD_XSL = "test_good.xsl";
	protected static final String XFORMS_UPLOAD1 = "xforms_upload1";
	protected static final String TEST_XML = "test.xml";
	protected static final String TEST1_1_XML = "test1.1.xml";
	protected static final String TEST2_XML = "test2.xml";
	protected static final String TEST1_USERDATA = "test1";
	protected static final String TEST2_USERDATA = "test2";
	protected static final String VALUE12 = "value1";
	protected static final String KEY1 = "key1";

	protected static final String TREE_MULTILEVEL_XML = "tree_multilevel.xml";
	protected static final String TREE_MULTILEVEL_V2_XML = "tree_multilevel.v2.xml";

	protected static final String MAIN_CONDITION = "Алтайский край";
	protected static final String FILTER_CONDITION = "filter";
	protected static final String ADD_CONDITION = "add_condition";

	protected static final String TEST_XML_FILE = "logic.xml";

	protected static final String RICH_DP = "m1003.xml";

	protected static final String GEOMAP_WOHEADER_SVG = "geomap_woheader.svg";
	protected static final String GEOMAP_WITH_HEADER_SVG = "geomap.svg";
	protected static final String TEST_ROOT = "ru/curs/showcase/test/";

	protected static final String TAB_2_NAME = "Вкладка 2";
	protected static final String EL_06 = "06";
	protected static final String TAB_2 = "2";
	public static final String FIREFOX_UA =
		"mozilla/5.0 (windows nt 6.1; wow64; rv:2.0.1) gecko/20100101 firefox/4.0.1";

	protected static final String PLUGIN_HANDLE_RADAR_PY = "plugin/handleRadar.py";
	protected static final String PLUGIN_RADAR_PROC = "pluginRadarInfo";
	protected static final String RADAR_COMP = "radar";
	protected static final String TEST_TEXT_SAMPLE_XML = "TestTextSample.xml";
	protected static final String SHOWCASE_TEMPLATE_XML = "Showcase_Template.xml";

	/**
	 * Действия, которые должны выполняться перед запуском любых тестовых
	 * классов.
	 */
	@BeforeClass
	@org.testng.annotations.BeforeClass
	public static void beforeClass() {
		AppInitializer.initialize();
		AppInitializer.finishUserdataSetupAndCheckLoggingOverride();
		initTestSession();
	}

	@Before
	public void beforeTest() {
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setIgnoreWhitespace(true);
	}

	/**
	 * Очистка информации о текущей userdata после каждого теста.
	 */
	@After
	@org.testng.annotations.AfterTest
	public void afterTest() {
		resetUserData();
	}

	protected void resetUserData() {
		AppInfoSingleton.getAppInfo().setCurUserDataId((String) null);
		AppInfoSingleton.getAppInfo().clearSessions();
		// AppInfoSingleton.getAppInfo().getCacheManager().clearAll();
		AppInfoSingleton.getAppInfo().getGridStateCache().clear();
		AppInfoSingleton.getAppInfo().getLyraGridStateCache().clear();
		AppInfoSingleton.getAppInfo().getDataPanelCache().clear();
		IDSettings.getInstance().reset();
	}

	private static void initTestSession() {
		AppInfoSingleton.getAppInfo().clearSessions();
		AppInfoSingleton.getAppInfo().addSession(ServletUtils.TEST_SESSION);
	}

	/**
	 * Получает выходной SQLXML по входному.
	 * 
	 * @param connection
	 *            - соединение.
	 * @param sqlxmlIn
	 *            - входной SQLXML.
	 * @return - выходной SQLXML.
	 * @throws SQLException
	 */
	protected static CallableStatement getOutputByInputSQLXML(final Connection connection,
			final SQLXML sqlxmlIn) throws SQLException {
		String stmt = "DROP PROCEDURE [dbo].[_DebugXMLProcessor2]";
		try (Statement st = connection.createStatement();) {
			try {
				st.executeUpdate(stmt);
			} catch (SQLException e) {
				stmt = "";
			}
			stmt =
				"CREATE PROCEDURE [dbo].[_DebugXMLProcessor2] @par1 xml, @par2 xml Output AS set @par2 = @par1";
			st.executeUpdate(stmt);
		}

		stmt = "{call _DebugXMLProcessor2(?,?)}";
		CallableStatement cs = connection.prepareCall(stmt);
		cs.setSQLXML(1, sqlxmlIn);
		cs.registerOutParameter(2, java.sql.Types.SQLXML);
		cs.execute();

		return cs;

	}

	/**
	 * Возвращает элемент информационной панели для тестов.
	 * 
	 * @param fileName
	 *            - файл панели.
	 * @param tabID
	 *            - идентификатор вкладки.
	 * @param elID
	 *            - идентификатор элемента.
	 * @return элемент.
	 */
	protected DataPanelElementInfo getDPElement(final String fileName, final String tabID,
			final String elID) {
		boolean needReset = false;
		if (AppInfoSingleton.getAppInfo().getCurUserDataId() == null) {
			setDefaultUserData();
			needReset = true;
		}
		try {
			PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
			DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), fileName);
			DataPanelFactory dpFactory = new DataPanelFactory();
			DataPanel panel = dpFactory.fromStream(file);
			DataPanelElementInfo element = panel.getTabById(tabID).getElementInfoById(elID);
			assertTrue(element.isCorrect());
			return element;
		} finally {
			if (needReset) {
				resetUserData();
			}
		}
	}

	protected void setDefaultUserData() {
		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
	}

	/**
	 * Генерирует описание грида для тестов.
	 * 
	 * @return DataPanelElementInfo
	 * 
	 */
	protected DataPanelElementInfo getTestGridInfo() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("2", DataPanelElementType.GRID);
		elInfo.setPosition(1);
		elInfo.setProcName("grid_bal");
		generateTestTabWithElement(elInfo);
		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Генерирует описание грида для тестов.
	 * 
	 * @return DataPanelElementInfo
	 * 
	 */
	protected DataPanelElementInfo getTestGridInfo2() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("2", DataPanelElementType.GRID);
		elInfo.setPosition(1);
		elInfo.setProcName("grid_cities_data");
		generateTestTabWithElement(elInfo);
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("2md");
		proc.setName("grid_cities_metadata");
		proc.setType(DataPanelElementProcType.METADATA);
		elInfo.getProcs().put(proc.getId(), proc);
		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	protected void generateTestTabWithElement(final DataPanelElementInfo elInfo) {
		DataPanel dp = new DataPanel("xxx");
		DataPanelTab tab = new DataPanelTab(0, dp);
		tab.add(elInfo);
	}

	/**
	 * Генерирует описание графика для тестов.
	 * 
	 * @return DataPanelElementInfo
	 */
	protected DataPanelElementInfo getTestChartInfo() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("3", DataPanelElementType.CHART);
		elInfo.setPosition(2);
		elInfo.setProcName("chart_bal");
		elInfo.setHideOnLoad(true);
		generateTestTabWithElement(elInfo);

		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Генерирует описание xforms для тестов.
	 * 
	 * @return DataPanelElementInfo
	 */
	protected DataPanelElementInfo getTestXForms1Info() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("08", DataPanelElementType.XFORMS);
		final int position = 6;
		elInfo.setPosition(position);
		elInfo.setProcName("xforms_proc1");
		elInfo.setTemplateName(SHOWCASE_TEMPLATE_XML);

		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("proc1");
		proc.setName("xforms_saveproc1");
		proc.setType(DataPanelElementProcType.SAVE);
		elInfo.getProcs().put(proc.getId(), proc);
		proc = new DataPanelElementProc();
		proc.setId("proc2");
		proc.setName("xforms_submission1");
		proc.setType(DataPanelElementProcType.SUBMISSION);
		elInfo.getProcs().put(proc.getId(), proc);

		generateTestTabWithElement(elInfo);

		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Генерирует описание xforms для тестов.
	 * 
	 * @return DataPanelElementInfo
	 */
	protected DataPanelElementInfo getTestXForms2Info() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("09", DataPanelElementType.XFORMS);
		final int position = 7;
		elInfo.setPosition(position);
		elInfo.setProcName("xforms_proc1");
		elInfo.setTemplateName("Showcase_Template.xml");

		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("proc3");
		proc.setName("xforms_save_error_proc1");
		proc.setType(DataPanelElementProcType.SAVE);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc4");
		proc.setName("xforms_download1");
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc5");
		proc.setName(XFORMS_UPLOAD1);
		proc.setType(DataPanelElementProcType.UPLOAD);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc6");
		proc.setName(XFORMS_DOWNLOAD2);
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		proc.setSchemaName(TEST_GOOD_SMALL_XSD);
		proc.setTransformName(TEST_GOOD_XSL);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc6jj");
		proc.setName(XFORMS_DOWNLOAD2);
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		proc.setSchemaName("schema/TestGoodSmall.py");
		proc.setTransformName("transform/TestGood.py");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc6spsp");
		proc.setName(XFORMS_DOWNLOAD2);
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		proc.setSchemaName("xformSchemaTestGoodSmall");
		proc.setTransformName("xformTransformTestGood");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc7");
		proc.setName(XFORMS_UPLOAD1);
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setSchemaName(TEST_GOOD_XSD);
		proc.setTransformName(TEST_GOOD_XSL);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc7jj");
		proc.setName(XFORMS_UPLOAD1);
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setSchemaName("schema/TestGood.py");
		proc.setTransformName("transform/TestGood.py");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc7spsp");
		proc.setName(XFORMS_UPLOAD1);
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setSchemaName("xformSchemaTestGood");
		proc.setTransformName("xformTransformTestGood");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc8");
		proc.setName(XFORMS_UPLOAD1);
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setSchemaName("test_bad.xsd");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc9");
		proc.setName(XFORMS_UPLOAD1);
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setTransformName(TEST_GOOD_XSL);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc10");
		proc.setName("xforms_download3_wrong");
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		proc.setSchemaName(TEST_GOOD_SMALL_XSD);
		elInfo.getProcs().put(proc.getId(), proc);

		generateTestTabWithElement(elInfo);

		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Возвращает контекст для тестов из файла навигатора.
	 * 
	 * @param groupID
	 *            - номер группы в файле.
	 * @param elID
	 *            - номер элемента в группе.
	 * @param fileName
	 *            - имя файла с навигатором.
	 * @return - контекст.
	 */
	protected CompositeContext
			getContext(final String fileName, final int groupID, final int elID) {
		CompositeContext context = getAction(fileName, groupID, elID).getContext();
		return context;
	}

	/**
	 * Возвращает контекст для тестов.
	 * 
	 * @return - контекст.
	 */
	protected CompositeContext getTestContext1() {
		CompositeContext context = new CompositeContext();
		context.setMain("Ввоз, включая импорт - Всего");
		return context;
	}

	protected GridContext getTestGridContext1() {
		GridContext gc = GridContext.createFirstLoadDefault();
		gc.assignNullValues(getTestContext1());
		return gc;
	}

	protected GridContext getExtGridContext(final CompositeContext context) {
		GridContext gc = new GridContext();
		gc.setAdditional("<add>value</add>");
		gc.setFilter("<filter>filter_value</filter>");
		// gc.setPageSize(2);
		// Column col = new Column();
		// col.setId("colId");
		// col.setSorting(Sorting.ASC);
		// col.setWidth("10px");
		// gc.getSortedColumns().add(col);
		// gc.getSelectedRecordIds().add("r1");
		gc.getSelectedRecordIds().add("r2");
		gc.setCurrentColumnId("curColumnId");
		gc.setCurrentRecordId("curRecordId");

		gc.setCurrentDatapanelWidth(0);
		gc.setCurrentDatapanelHeight(0);

		context.addRelated("01", gc);

		return gc;
	}

	/**
	 * Возвращает контекст для тестов.
	 * 
	 * @return - контекст.
	 */
	protected CompositeContext getTestContext2() {
		CompositeContext context = new CompositeContext();
		context.setMain(MAIN_CONDITION);
		return context;
	}

	/**
	 * Возвращает контекст для тестов.
	 * 
	 * @return - контекст.
	 */
	protected CompositeContext getTestContext3() {
		CompositeContext context = new CompositeContext();
		context.setMain("Межрегиональный обмен - Всего");
		context.setAdditional(MAIN_CONDITION);
		return context;
	}

	/**
	 * Возвращает действие для тестов из файла навигатора.
	 * 
	 * @param groupID
	 *            - номер группы в файле.
	 * @param elID
	 *            - номер элемента в группе.
	 * @param fileName
	 *            - имя файла с навигатором.
	 * @return - контекст.
	 */
	protected Action getAction(final String fileName, final int groupID, final int elID) {
		boolean needReset = false;
		if (AppInfoSingleton.getAppInfo().getCurUserDataId() == null) {
			setDefaultUserData();
			needReset = true;
		}
		try (PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.NAVIGATOR)) {
			DataFile<InputStream> stream1 = gateway.getRawData(new CompositeContext(), fileName);
			CompositeContext context =
				new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
			NavigatorFactory navFactory = new NavigatorFactory(context);
			Navigator nav = navFactory.fromStream(stream1);
			Action action = nav.getGroups().get(groupID).getElements().get(elID).getAction();
			return action;
		} finally {
			if (needReset) {
				resetUserData();
			}
		}
	}

	/**
	 * Генерирует набор параметров URL c заданной userdata.
	 * 
	 * @param userDataId
	 *            - идентификатор userdata.
	 */
	protected Map<String, List<String>> generateTestURLParams(final String userDataId) {
		Map<String, List<String>> params = new TreeMap<>();
		ArrayList<String> value1 = new ArrayList<>();
		value1.add(VALUE12);
		params.put(KEY1, value1);
		ArrayList<String> value2 = new ArrayList<>();
		value2.add("value21");
		value2.add("value22");
		params.put("key2", value2);
		ArrayList<String> value3 = new ArrayList<>();
		value3.add(userDataId);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		return params;
	}

	/**
	 * Генерирует набор параметров URL c заданной userdata.
	 * 
	 * @param userDataId
	 *            - идентификатор userdata.
	 */
	protected Map<String, ArrayList<String>> generateTestURLParamsForSL(final String userDataId) {
		Map<String, ArrayList<String>> params = new TreeMap<>();
		ArrayList<String> value1 = new ArrayList<>();
		value1.add(VALUE12);
		params.put(KEY1, value1);
		ArrayList<String> value2 = new ArrayList<>();
		value2.add("value21");
		value2.add("value22");
		params.put("key2", value2);
		ArrayList<String> value3 = new ArrayList<>();
		value3.add(userDataId);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		return params;
	}

	protected OutputStreamDataFile getTestFile(final String linkId) throws IOException {
		OutputStreamDataFile file =
			new OutputStreamDataFile(StreamConvertor.inputToOutputStream(FileUtils
					.loadClassPathResToStream(linkId)), linkId);
		return file;
	}

	protected CompositeContext generateContextWithSessionInfo() {
		return new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
	}

	protected Action createSimpleTestAction() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		CompositeContext context = getSimpleTestContext();
		action.setContext(context);

		DataPanelLink link = action.getDataPanelLink();
		link.setDataPanelId(TEST_XML);
		link.setTabId(TAB_2);
		CompositeContext elContext = context.gwtClone();
		elContext.setAdditional(ADD_CONDITION);

		DataPanelElementLink elLink = new DataPanelElementLink(EL_06, elContext);
		link.getElementLinks().add(elLink);

		action.determineState();
		return action;
	}

	protected DataPanelTab createStdTab() {
		DataPanel dp = new DataPanel();
		DataPanelTab tab = dp.add(TAB_2, TAB_2_NAME);
		return tab;
	}

	protected CompositeContext getSimpleTestContext() {
		CompositeContext context = new CompositeContext();
		context.setMain(MAIN_CONDITION);
		return context;
	}

	protected Action generateActionWithServerAactivity(final String procName) {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", procName);
		CompositeContext context =
			new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
		context.setMain(MAIN_CONDITION);
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		return action;
	}

	protected GridContext generateReloadContextForGridBalProc(final int pageSize,
			final int pageNum, final String firstColName) {
		final int maxColIndex = 5;
		GridContext gc = new GridContext();
		// gc.setPageNumber(pageNum);
		// gc.setPageSize(pageSize);
		addSortedColumn(gc, "3кв. 2007г.", maxColIndex);
		addSortedColumn(gc, "3кв. 2006г.", 1);
		addSortedColumn(gc, firstColName, 0);
		assertNull(gc.getCurrentColumnId());
		assertNull(gc.getCurrentRecordId());
		assertEquals(0, gc.getSelectedRecordIds().size());
		gc.setCurrentColumnId(firstColName);
		gc.setCurrentRecordId("1");
		gc.getSelectedRecordIds().add("1");
		gc.assignNullValues(getTestContext1());
		return gc;
	}

	private void addSortedColumn(final GridContext settings, final String name, final int index) {
		// Column col = new Column();
		// col.setId(name);
		// col.setSorting(Sorting.ASC);
		// col.setIndex(index);
		// settings.getSortedColumns().add(col);
	}
}