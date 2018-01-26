package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.sql.SQLException;

import org.junit.*;
import org.slf4j.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.AppInitializer;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.chart.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.event.*;
import ru.curs.showcase.core.frame.*;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.html.webtext.*;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.core.jython.JythonWrongClassException;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.core.primelements.datapanel.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тесты для серверных исключений. Как правило сюда включены тесты, не
 * относящиеся к конкретному компоненту: навигатору, инф. панели или ее
 * элементу.
 * 
 * @author den
 * 
 */
public class ExceptionsTest extends AbstractTestWithDefaultUserData {
	private static final String ERROR_USER_MESSAGE = "Ошибка";
	/**
	 * Имя несуществующей схемы.
	 */
	private static final String PHANTOM_XSD = "phantom26052011.xsd";

	@Test
	public void testExceptionConfig() {
		ExceptionConfig config = AppRegistry.getExceptionConfig();
		final int simpleExcCount = 4;
		assertEquals(simpleExcCount, config.getNoDatailedInfoExceptions().size());
	}

	/**
	 * Тест на считывание несуществующего параметра из файла настроек.
	 * 
	 */
	@Test(expected = SettingsFileRequiredPropException.class)
	public final void testReadNotExistingValue() {
		UserDataUtils.getRequiredProp("blabla");
	}

	/**
	 * Тест на считывание параметра в неверном формате из файла настроек.
	 * 
	 */
	@Test(expected = SettingsFilePropValueFormatException.class)
	public final void testReadWrongValue() {
		ProfileReader gp =
			new ProfileReader("default.properties", SettingsFileType.GRID_PROPERTIES);
		gp.init();
		gp.getIntValue("def.column.hor.align");
	}

	/**
	 * Тест на несуществующую информационную панель.
	 * 
	 */
	@Test(expected = SettingsFileOpenException.class)
	public final void testWrongDP() {
		PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
		gateway.getRawData(new CompositeContext(), "verysecretandhidden.xml");
	}

	/**
	 * Проверка GeneralServerException, вызванного
	 * DataPanelFileNotFoundException.
	 */
	@Test
	public final void testWrongDPByServiceLayer() {
		Action action = new Action();
		action.setDataPanelActionType(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("verysecretandhidden.xml");
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		try {
			DataPanelGetCommand command = new DataPanelGetCommand(action);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(SettingsFileOpenException.class.getName(), e.getOriginalExceptionClass());
			assertNotNull(e.getOriginalMessage());
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за несуществующей хранимой процедуры.
	 */
	@Test
	public final void testPhantomChartSP() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "3", "31");

		try {
			ChartGetCommand command = new ChartGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(SPNotExistsException.class.getName(), e.getOriginalExceptionClass());
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за хранимой процедуры c неверными параметрами.
	 */
	@Test
	@Ignore
	// !!!
			public final
			void testWrongChartSP() {
		CompositeContext context = getTestContext2();

		DataPanelElementInfo element = getDPElement(TEST2_XML, "3", "33");
		final String procName = "chart_pas_wrong_param";

		try {
			ChartGetCommand command = new ChartGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(DBQueryException.class.getName(), e.getOriginalExceptionClass());
			assertTrue(e.getOriginalMessage().indexOf(procName) > -1);
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за хранимой процедуры, не вернувшей данные.
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public final
			void testWrongChartSPWithNoResult() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "3", "32");

		try {
			ChartGetCommand command = new ChartGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(DBQueryException.class.getName(), e.getOriginalExceptionClass());
			assertTrue(e.getMessage().indexOf(CompBasedElementSPQuery.NO_RESULTSET_ERROR) > -1);
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку для несуществующей хранимой процедуру для Submission.
	 */
	@Test
	public final void testWrongChartSPForSubmission() {

		try {
			XFormContext context = new XFormContext();
			DataPanelElementInfo elInfo =
				XFormInfoFactory.generateXFormsSQLSubmissionInfo("no_exist_proc");
			XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(SPNotExistsException.class.getName(), e.getOriginalExceptionClass());
			assertTrue(e.getMessage().indexOf("no_exist_proc") > -1);
			return;
		}
		fail();
	}

	/**
	 * Проверка на ошибку при передаче WebText с неполной информацией.
	 * 
	 */
	@Test
	public final void testWrongElement() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = new DataPanelElementInfo();
		element.setId("11");
		element.setType(DataPanelElementType.WEBTEXT);

		try {
			WebTextGetCommand command = new WebTextGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class.getName(), e.getOriginalExceptionClass());
			return;
		}
		fail();
	}

	/**
	 * Тест на срабатывание проверки на ввод неверного autoSelectRecordId.
	 * 
	 * @throws Exception
	 */
	// !!! @Test(expected = InconsistentSettingsFromDBException.class)
	public void testInconsistentSettings() throws Exception {
		GridContext gc = getTestGridContext1();
		DataPanelElementInfo element = getDPElement(TEST_XML, "3", "5");

		GridGateway gateway = new GridDBGateway();
		RecordSetElementRawData raw = gateway.getRawDataAndSettings(gc, element);
		GridMetaFactory factory = new GridMetaFactory(raw, new GridServerState());
		factory.build();
	}

	/**
	 * Тест проверки схемы XSD для неверного элемента.
	 * 
	 */
	// !!! @Test(expected = XSDValidateException.class)
	public void testXSDValidateException() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST_XML, "3", "6");

		HTMLGateway gateway = new HtmlDBGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Пытается проверить XML несуществующей пользовательской схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testUserXSDNotFoundException() {
		XMLUtils.xsdValidateUserData(FileUtils.loadClassPathResToStream(TEST_XML_FILE),
				PHANTOM_XSD);
	}

	/**
	 * Пытается проверить XML несуществующей системной схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testXSDNotFoundException() {
		XMLUtils.xsdValidateAppDataSafe(FileUtils.loadClassPathResToStream(TEST_XML_FILE),
				PHANTOM_XSD);
	}

	/**
	 * Функция проверки функционала SolutionDBException.
	 */
	@Test
	public void testSolutionException() {
		SQLException exc = new SQLException(UserMessageFactory.SOL_MES_PREFIX);
		assertFalse(UserMessageFactory.isExplicitRaised(exc));
		exc =
			new SQLException(String.format("%stest1%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		assertTrue(UserMessageFactory.isExplicitRaised(exc));
		UserMessageFactory factory = new UserMessageFactory();
		ValidateException solEx = new ValidateException(factory.build(exc));
		assertNotNull(solEx.getUserMessage());
		assertEquals("test1", solEx.getUserMessage().getId());
		assertEquals(MessageType.ERROR, solEx.getUserMessage().getType());
		assertEquals(ERROR_USER_MESSAGE, solEx.getUserMessage().getText());
		exc =
			new SQLException(String.format("%stest2%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		factory = new UserMessageFactory();
		solEx = new ValidateException(factory.build(exc));
		assertEquals("Предупреждение", solEx.getUserMessage().getText());
	}

	/**
	 * Проверка случая, когда из БД приходит ссылка на несуществующее сообщение
	 * решения.
	 */
	@Test(expected = SettingsFileRequiredPropException.class)
	public void testSolutionExceptionMesNotFound() {
		SQLException exc =
			new SQLException(String.format("%stestN%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		UserMessageFactory factory = new UserMessageFactory();
		throw new ValidateException(factory.build(exc));
	}

	/**
	 * Проверка обработки пользовательского исключения в БД на сервисном уровне.
	 */
	@Test
	public void testSolutionExceptionBySL() {
		SQLException exc =
			new SQLException(String.format("%stest1%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		UserMessageFactory factory = new UserMessageFactory();
		ValidateException exc2 = new ValidateException(factory.build(exc));
		GeneralException gse = GeneralExceptionFactory.build(exc2);
		assertFalse(GeneralException.needDetailedInfo(gse));
		assertEquals(ERROR_USER_MESSAGE, exc2.getUserMessage().getText());
		GeneralException.generateDetailedInfo(gse);
	}

	/**
	 * Проверка создания DBQueryException через SL.
	 */
	@Test
	public void testDBQueryExceptionBySL() {
		CompositeContext context = getTestContext1();
		PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel dp = factory.fromStream(file);

		DBQueryException dbqe =
			new DBQueryException(dp.getTabById("2").getElementInfoById("2"), "error");
		GeneralException gse =
			GeneralExceptionFactory.build(dbqe, new DataPanelElementContext(context));

		final String errorMes =
			"Произошла ошибка при выполнении хранимой процедуры grid_bal. Подробности: error.";
		assertEquals(errorMes, gse.getMessage());
		assertNull(gse.getOriginalMessage());
		assertEquals(DBQueryException.class.getName(), gse.getOriginalExceptionClass());
		assertNotNull(gse.getStackTrace());
		assertEquals(MessageType.ERROR, gse.getMessageType());
		assertNotNull(gse.getContext());
		assertEquals("Ввоз, включая импорт - Всего", gse.getContext().getCompositeContext()
				.getMain());
		assertTrue(GeneralException.needDetailedInfo(gse));
		GeneralException.generateDetailedInfo(gse);
	}

	/**
	 * Тесты для статических функций GeneralServerException, работающих с любыми
	 * исключениями.
	 */
	@Test
	public void testGSEStaticFunctions() {
		Exception exc = new Exception();
		assertEquals(MessageType.ERROR, GeneralException.getMessageType(exc));
		assertEquals(ExceptionType.JAVA, GeneralException.getType(exc));
		assertTrue(GeneralException.needDetailedInfo(exc));
		assertNotNull(GeneralException.generateDetailedInfo(exc));
	}

	/**
	 * Проверка возврата ошибки с кодом из БД.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testReturnErrorFromDB() {
		MainPageFrameGateway gateway = new MainPageFrameDBGateway();
		CompositeContext context = getTestContext1();

		try {
			gateway.getRawData(context, "header_proc_with_error");
		} catch (ValidateException e) {
			assertEquals("Ошибка, переданная через @error_mes (1)", e.getUserMessage().getText());
			return;
		}
		fail();
	}

	/**
	 * Проверка на исключение при неверном номере вкладки инф. панели в
	 * действии.
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongTab() {
		final int elID = 3;
		getAction("tree_multilevel.wrong.xml", 0, elID);
	}

	/**
	 * Проверка на исключение при неверном столбце сортировки в гриде.
	 */
	// !!! @Test(expected = DBQueryException.class)
	public void testDBQueryExceptionWithWrongGridSorting() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		GridContext gc = new GridContext();
		gc.assignNullValues(context);

		GridGateway gateway = new GridDBGateway();
		gateway.getRawData(gc, elInfo);
	}

	/**
	 * Проверка на исключение при попытке получить настройки элемента при
	 * загрузке только данных.
	 */
	// !!! @Test(expected = ResultSetHandleException.class)
	public void testErrorWhenGetSettingsForDataOnlyProc() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		GridGateway gateway = new GridDBGateway();
		GridContext gc = new GridContext();
		gc.assignNullValues(context);
		RecordSetElementRawData res = gateway.getRawData(gc, elInfo);
		res.prepareSettings();
	}

	@Test(expected = IncorrectElementException.class)
	public void testElementActionWrong() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elInfo.setProcName("xforms_proc_wrong_1");
		generateTestTabWithElement(elInfo);
		ElementInfoChecker checker = new ElementInfoChecker();
		checker.check(elInfo, DataPanelElementType.XFORMS);
	}

	@Test
	@Ignore
	// !!!
			public
			void testErrorCodeReturn() {
		XFormContext context = new XFormContext();
		context.setMain(MAIN_CONTEXT_TAG);
		context.setAdditional(ADD_CONDITION);
		HTMLAdvGateway gateway = new HtmlDBGateway();
		final String procName = "xforms_submission_ec";

		try {
			gateway.scriptTransform(procName, context);
		} catch (ValidateException e) {
			assertEquals("Ошибка в SP (-1)", e.getMessage());
		}

		try {
			context.setFormData("<mesid>555</mesid>");
			gateway.scriptTransform(procName, context);
		} catch (ValidateException e) {
			assertEquals("Отформатированное сообщение: Ошибка в SP. Спасибо!", e.getMessage());
		}

		try {
			context.setFormData("<mesid>556</mesid>");
			gateway.scriptTransform(procName, context);
		} catch (ValidateException e) {
			assertEquals("Составное сообщение + Ошибка в SP", e.getMessage());
		}
	}

	@Test
	@Ignore
	// !!!
			public
			void testGeoMapErrorCodeReturn() {
		CompositeContext context = new CompositeContext();
		context.setMain(MAIN_CONTEXT_TAG);
		context.setAdditional("<mesid>556</mesid>");
		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		DataPanelElementInfo dpei = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		dpei.setProcName("geomap_ec");
		try {
			gateway.getRawData(context, dpei);
		} catch (ValidateException e) {
			assertEquals("Составное сообщение + ", e.getMessage());
		}
	}

	@Test
	public void testContextToExceptionApply() {
		XFormContext context = new XFormContext();
		context.setMain(MAIN_CONTEXT_TAG);
		context.setAdditional(ADD_CONDITION);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("test_bad.xsl");

		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		try {
			command.execute();
			fail();
		} catch (GeneralException e) {
			assertNotNull(e.getContext());
			assertEquals(context, e.getContext().getCompositeContext());
			assertEquals(elInfo, e.getContext().getElementInfo());
		}
	}

	@Test
	public void testJythonLibDirError() {
		final String path = JythonIterpretatorFactory.LIB_JYTHON_PATH;
		try {
			try {
				JythonIterpretatorFactory.getInstance().setLibDir(path + "_");
				JythonIterpretatorFactory.getInstance().clear();
				JythonIterpretatorFactory.getInstance().acquire();
				fail();
			} catch (ServerLogicError e) {
				assertEquals(
						"Каталог со стандартными python скриптами '/WEB-INF/libJython_' не найден",
						e.getMessage());
			}
		} finally {
			JythonIterpretatorFactory.getInstance().resetLibDir();
		}
	}

	@Test(expected = IncorrectElementException.class)
	public void testIncorrectElementException() throws Exception {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		final String webtextFile = "wrong2.xml";
		element.setProcName(webtextFile);

		HTMLGateway wtgateway = new HTMLFileGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(context, element);
		WebTextFactory factory = new WebTextFactory(rawWT);
		factory.build();
	}

	@Test
	@Ignore
	// !!!
			public
			void testUserMessageByException() {
		XFormContext context = new XFormContext();
		context.setFormData("<model/>");
		HTMLAdvGateway gateway = new HtmlDBGateway();
		try {
			gateway.scriptTransform("xforms_submission_um", context);
			fail();
		} catch (ValidateException e) {
			assertEquals(ERROR_USER_MESSAGE, e.getLocalizedMessage());
		}
	}

	// !!! @Test
	public void testUserMessageStorageAbsent() {
		UserMessageFactory ufactory = new UserMessageFactory();
		ufactory.setMessageFile(UserMessageFactory.SOL_MESSAGES_FILE + "_");
		try {
			ufactory.build(1, "Error mes");
			fail();
		} catch (SettingsFileOpenException e) {
			assertEquals(SettingsFileType.SOLUTION_MESSAGES, e.getFileType());
		}
	}

	// !!! @Test(expected = FileIsAbsentInDBException.class)
	public void testFileIsAbsentInDBException() {
		GridGateway gateway = new GridDBGateway();
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("1", DataPanelElementType.GRID);
		ID procID = new ID("1");
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(procID);
		proc.setName("grid_download_null");
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		elementInfo.getProcs().put(procID, proc);
		generateTestTabWithElement(elementInfo);
		gateway.downloadFile(getTestGridContext1(), elementInfo, procID, "1");
	}

	@Test
	public void testExceptionInfo() {
		GeneralException exc = GeneralExceptionFactory.build(new RuntimeException());

		assertTrue(exc.getNeedDatailedInfo());
		assertEquals(ExceptionType.JAVA, exc.getType());
		assertEquals(ExceptionType.JAVA, GeneralException.getType(exc));
		assertEquals(MessageType.ERROR, GeneralException.getMessageType(exc));

	}

	@Test
	public void testGeneralExceptionCreation() {
		GeneralException exc = new GeneralException();

		assertNull(exc.getType());
		assertNull(exc.getMessage());
		assertNull(exc.getType());
		assertNull(exc.getNeedDatailedInfo());
	}

	@Test
	public void testLogExceptionStack() {
		final String loggerName = ExceptionsTest.class.getName();
		Logger logger = LoggerFactory.getLogger(loggerName);
		final String message = "message text";
		final String excMessage = "exception text";
		final String causeMessage = "cause text";
		final String testMarker = "testMarker";
		Marker marker = MarkerFactory.getDetachedMarker(testMarker);
		marker.add(HandlingDirection.INPUT.getMarker());
		final String params = "exception params";
		marker.add(MarkerFactory.getDetachedMarker(params));
		logger.warn(marker, message, new RuntimeException(excMessage, new RuntimeException(
				causeMessage)));
		for (LoggingEventDecorator event : AppInfoSingleton.getAppInfo().getLastLogEvents()) {
			if (testMarker.equals(event.getProcess())) {
				assertEquals(HandlingDirection.INPUT.name(), event.getDirection());
				assertEquals(params, event.getParams());
				assertTrue(event.getMessage().contains(message));
				assertTrue(event.getMessage().contains(excMessage));
				assertTrue(event.getMessage().contains(LoggingEventDecorator.EXCEPTION_SOURCE));
				assertTrue(event.getMessage().contains(causeMessage));
				assertTrue(event.getMessage().contains(RuntimeException.class.getSimpleName()));
				assertTrue(event.getMessage().contains(loggerName));
				assertNotNull(event.getTime());
				return;
			}
		}
		fail();
	}

	@Test(expected = NoSuchRootPathUserDataException.class)
	public void testNoSuchRootPathUserDataException() {
		String testProps = "ru/curs/showcase/test/" + FileUtils.GENERAL_PROPERTIES;

		AppInfoSingleton.getAppInfo().getUserdatas().clear();
		try {
			String rootpath = FileUtils.getTestUserdataRoot(testProps);
			AppInitializer.checkUserDataDir(rootpath, testProps);
		} finally {
			AppInitializer.finishUserdataSetupAndCheckLoggingOverride();
		}
	}

	@Test(expected = NotImplementedYetException.class)
	public void testNotImplementedYetException() {
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.CHART);
		elementInfo.setProcName("chart.xml");
		ChartSelector selector = new ChartSelector(elementInfo);
		selector.getGateway();
	}

	@Test
	@Ignore
	// !!!
			public
			void testWrongJythonFile() {
		final String source = "WrongJythonProc";
		Activity activity = Activity.newServerActivity("id", source + ".py");
		CompositeContext context =
			new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
		context.setMain(MAIN_CONDITION);
		activity.setContext(context);
		ActivityGateway gateway = new ActivityJythonGateway();
		try {
			gateway.exec(activity);
			fail();
		} catch (JythonWrongClassException e) {
			assertEquals(
					"Имя Jython класса-обработчика команд Showacase должно совпадать с именем файла 'WrongJythonProc'",
					e.getLocalizedMessage());
		}
	}

	@Test
	public void testSettingsFileExchangeException() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		element.setProcName("WrongXML.xm");

		HTMLGateway gateway = new HTMLFileGateway();
		try {
			gateway.getRawData(context, element);
			fail();
		} catch (SettingsFileExchangeException e) {
			assertEquals(
					"XML-файл с данными 'WrongXML.xm' - ошибка при обмене данными. Возможно файл поврежден или указан ошибочно.",
					e.getLocalizedMessage());
		}
	}
}
