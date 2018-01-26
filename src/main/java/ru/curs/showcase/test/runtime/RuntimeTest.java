package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.*;

import org.ehcache.Cache;
import org.junit.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.chart.ChartGetCommand;
import ru.curs.showcase.core.command.ServerStateGetCommand;
import ru.curs.showcase.core.event.ExecServerActivityCommand;
import ru.curs.showcase.core.html.xform.XFormDownloadCommand;
import ru.curs.showcase.core.primelements.datapanel.DataPanelGetCommand;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.OutputStreamDataFile;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Тесты на получение информации о сессии пользователя.
 * 
 * @author den
 * 
 */
public class RuntimeTest extends AbstractTest {
	private static final boolean AUTH_VIA_AUTH_SERVER = true;
	private static final String TEMP_PASS = "pass";
	private static final String FAKE_SESSION_ID = "fake-session-id";
	private static final String NOT_EXIST_USERDATA_ID = "test123";

	/**
	 * Простой тест на установку текущего userdataId.
	 */
	@Test
	public void testCurUserDataIdSet() {
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(
				generateTestURLParamsForSL(TEST1_USERDATA));
		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
	}

	/**
	 * Простой тест на запуск функций из SessionUtils.
	 */
	@Test
	public void testSessionUtilsFunctions() {
		assertEquals("", SessionUtils.getCurrentSessionUserName());
		assertEquals(SessionUtils.TEST_SESSION, SessionUtils.getCurrentSessionId());
		assertEquals(SessionUtils.TEST_SID, SessionUtils.getCurrentUserSID());
	}

	/**
	 * Проверка того, что значение getCurrentUserDataId сразу после запуска
	 * равно null.
	 */
	@Test
	public void testInitialCurUserDataIdValue() {
		setDefaultUserData();
		assertEquals(ExchangeConstants.DEFAULT_USERDATA, AppInfoSingleton.getAppInfo()
				.getCurUserDataId());
	}

	/**
	 * Базовый тест на запись и чтение URLParams.
	 */
	// !!! @Test
	public void testSessionInfoForGetChart() {
		Map<String, List<String>> params = generateTestURLParams(TEST1_USERDATA);

		AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(FAKE_SESSION_ID,
				AUTH_VIA_AUTH_SERVER);
		AppInfoSingleton.getAppInfo().setAuthServerCrossAppPasswordForSession(FAKE_SESSION_ID,
				TEMP_PASS);

		CompositeContext context = getTestContext3();
		context.addSessionParams(params);
		DataPanelElementInfo element = getTestChartInfo();

		ChartGetCommand command = new ChartGetCommand(context, element);
		command.execute();

		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
		assertEquals(AUTH_VIA_AUTH_SERVER, AppInfoSingleton.getAppInfo()
				.getAuthViaAuthServerForSession(FAKE_SESSION_ID));
		assertEquals(TEMP_PASS, AppInfoSingleton.getAppInfo()
				.getAuthServerCrossAppPasswordForSession(FAKE_SESSION_ID));

	}

	/**
	 * Проверка установки информации о сессии для функции получения инф. панели.
	 */
	@Test
	public void testSessionInfoForGetDP() {
		Map<String, List<String>> params = generateTestURLParams(TEST1_USERDATA);
		final int elID = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, elID);
		action.setSessionContext(params);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		command.execute();

		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
	}

	/**
	 * Проверка установки и чтения текущей userdata.
	 */
	@Test
	public void testCurrentUserdata() {
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(
				new TreeMap<String, ArrayList<String>>());
		assertEquals(ExchangeConstants.DEFAULT_USERDATA, AppInfoSingleton.getAppInfo()
				.getCurUserDataId());
		assertNotNull(UserDataUtils.getUserDataCatalog());

		Map<String, ArrayList<String>> params = new TreeMap<>();
		ArrayList<String> value3 = new ArrayList<>();
		value3.add(TEST1_USERDATA);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(params);
		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
		assertNotNull(UserDataUtils.getUserDataCatalog());
	}

	/**
	 * Проверка установки несуществующей userdata.
	 */
	@Test(expected = NoSuchUserDataException.class)
	public void testNotExistUserdata() {
		Map<String, ArrayList<String>> params = new TreeMap<>();
		ArrayList<String> value3 = new ArrayList<>();
		value3.add(NOT_EXIST_USERDATA_ID);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(params);
	}

	/**
	 * Проверка вызова функции getServerCurrentState.
	 */
	@Test
	public void testGetServerCurrentState() {
		CompositeContext context = new CompositeContext();
		ServerStateGetCommand command = new ServerStateGetCommand(context);
		ServerState scs = command.execute();
		checkServerState(scs);
	}

	private void checkServerState(final ServerState scs) {
		assertNotNull(scs);
		assertEquals(AppInfoSingleton.getAppInfo().getServletContainerVersion(),
				scs.getServletContainerVersion());
		assertEquals(System.getProperty("java.version"), scs.getJavaVersion());
	}

	@Test
	public void testCommandContext() {
		CommandContext cc = new CommandContext();
		cc.setUserdata(TEST1_USERDATA);
		cc.setUserName(ExchangeConstants.DEFAULT_USERDATA);
		cc.setCommandName(XFormDownloadCommand.class.getSimpleName());
		cc.setRequestId("1");

		AbstractCommandContext clone = cc.gwtClone();
		assertEquals(cc, clone);
		assertNotSame(cc, clone);
		cc.setRequestId("2");
		assertFalse(cc.equals(clone));
		assertEquals(cc.getUserName(), clone.getUserName());

		CommandContext clone2 = new CommandContext();
		clone2.assignNullValues(cc);
		assertEquals(cc, clone2);
		assertNotSame(cc, clone2);
	}

	@Test
	// @SuppressWarnings("rawtypes")
			public
			void testCache() {
		// assertNotNull(CacheManager.create());
		// assertTrue(CacheManager.getInstance().cacheExists(AppInfoSingleton.GRID_STATE_CACHE));
		// assertEquals(1, CacheManager.getInstance().getCacheNames().length);

		// List list =
		// CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE).getKeys();
		// CacheManager cm = AppInfoSingleton.getAppInfo().getCacheManager();

		Cache<Object, Object> cache = AppInfoSingleton.getAppInfo().getGridStateCache();
		assertNotNull(cache);

		// assertEquals(0, list.size());
		assertEquals(0, AppInfoSingleton.getAppInfo().numberOfGridStateCacheEntries());

		CompositeContext value = new CompositeContext();
		final String key = "key";
		// CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE)
		// .put(new net.sf.ehcache.Element(key, value));
		cache.put(key, value);

		// list =
		// CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE).getKeys();

		// assertEquals(1, list.size());
		assertEquals(1, AppInfoSingleton.getAppInfo().numberOfGridStateCacheEntries());

		// assertEquals(key, list.get(0));
		for (Cache.Entry<Object, Object> ce : cache) {
			assertEquals(key, ce.getKey());
			assertEquals(value, ce.getValue());
		}
		// assertEquals(value,
		// CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE)
		// .get(key).getValue());
	}

	@Test
	public void serverStateGetCommandShouldCorrectDetermineBrowserTypeAndVersion() {
		CompositeContext context = generateContextWithSessionInfo();
		ServerStateGetCommand command = new ServerStateGetCommand(context);
		ServerState serverState = command.execute();
		ClientState clientState =
			new ClientState(serverState, "Opera/9.20 (Windows NT 6.0; U; en)");

		checkServerState(clientState.getServerState());
		assertEquals(BrowserType.OPERA, clientState.getBrowserType());
		assertEquals(BrowserType.VERSION_NOT_DEFINED, clientState.getBrowserVersion());

		clientState =
			new ClientState(serverState,
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
		assertEquals("2.0.0.6", clientState.getBrowserVersion());
	}

	@Test
	@Ignore
	// !!!
			public
			void testExecutedProc() {
		AppInfoSingleton.getAppInfo().getExecutedProc().clear();
		final String procName = "activity_for_test";
		assertFalse(AppInfoSingleton.getAppInfo().getExecutedProc().contains(procName));

		Action action = generateActionWithServerAactivity(procName);
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();

		assertTrue(AppInfoSingleton.getAppInfo().getExecutedProc().contains(procName));
	}

	/**
	 * Проверка работы построителя ServerCurrentState.
	 * 
	 * @see ru.curs.showcase.app.api.ServerState ServerCurrentState
	 * @see ru.curs.showcase.runtime.ServerStateFactory
	 *      ServerCurrentStateBuilder
	 */
	@Test
	@Ignore
	// !!!
			public
			void serverStateFactoryShouldReturnCorrectState() throws SQLException {
		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		ServerState state = ServerStateFactory.build("fake");
		assertNotNull(state);
		assertNotNull(state.getAppVersion());
		assertTrue(state.getAppVersion().endsWith("development"));
		assertNotNull(state.getJavaVersion());
		assertNotNull(state.getServerTime());
		assertNotNull(state.getSqlVersion());
		assertNotNull(state.getDojoVersion());
		assertNotNull(state.getCaseSensivityIDs());
		assertNotNull(state.getEnableClientLog());

		assertEquals("10.0.0.9999", ServerStateFactory.getAppVersion("ru/curs/showcase/test/"));
	}

	@Test
	public void serverStateFactoryShouldRaiseExceptionWhenVersionFileAbsent() {
		try {
			ServerStateFactory.getAppVersion("ru/curs/showcase/test/util/");
			fail();
		} catch (SettingsFileOpenException e) {
			assertTrue(e.getLocalizedMessage().contains(
					"ru/curs/showcase/test/util/version.properties"));
		}
	}

	@Test
	public void serverStateFactoryShouldRaiseExceptionWhenBuildFileAbsent() {
		try {
			ServerStateFactory.getAppVersion("ru/curs/showcase/test/runtime/");
			fail();
		} catch (SettingsFileOpenException e) {
			assertTrue(e.getLocalizedMessage().contains("ru/curs/showcase/test/runtime/build"));
		}
	}

	@Test
	@Ignore
	// !!!
			public
			void testDoAfterCheck() {
		OutputStreamDataFile file = new OutputStreamDataFile();
		file.setName("test.py");
		assertTrue(file.isTextFile());
		file.setName("test.php");
		assertTrue(file.isTextFile());
		file.setName("test.xyz");
		assertFalse(file.isTextFile());
	}
}
