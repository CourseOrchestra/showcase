package ru.curs.showcase.test.event;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.junit.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.event.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.ReflectionUtils;

/**
 * Тесты для действий и контекста.
 * 
 * @author den
 * 
 */
public class ActionAndContextTest extends AbstractTestWithDefaultUserData {
	private static final String TEST_ACTIVITY_NAME = "test";
	private static final String SESSION_CONDITION =
		"<sessioncontext><username>master</username><urlparams></urlparams></sessioncontext>";
	private static final int DEF_SIZE_VALUE = 100;
	private static final String NEW_ADD_CONDITION = "New add condition";
	private static final String MOSCOW_CONTEXT = "Москва";
	private static final String TAB_1 = "1";

	/**
	 * Тест клонирования Action и составляющих его объектов.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@Test
	public void testClone() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Action action = createComplexTestAction();
		Action clone = action.gwtClone();

		assertEquals(DataPanelActionType.RELOAD_PANEL, clone.getDataPanelActionType());
		assertEquals(NavigatorActionType.CHANGE_NODE, clone.getNavigatorActionType());
		assertFalse(clone.getKeepUserSettings());
		assertEquals(ShowInMode.MODAL_WINDOW, clone.getShowInMode());

		ModalWindowInfo mwi = clone.getModalWindowInfo();
		assertNotNull(mwi);
		assertEquals("mwi", mwi.getCaption());
		assertEquals(DEF_SIZE_VALUE, mwi.getHeight().intValue());
		assertEquals(DEF_SIZE_VALUE, mwi.getWidth().intValue());
		assertTrue(mwi.getShowCloseBottomButton());

		CompositeContext context = getComplexTestContext();
		assertNotNull(clone.getContext());
		assertTrue(ReflectionUtils.equals(context, clone.getContext()));

		DataPanelLink link = clone.getDataPanelLink();
		assertNotNull(link);
		assertFalse(link.getFirstOrCurrentTab());
		assertEquals(TEST_XML, link.getDataPanelId().getString());
		assertEquals(TAB_2, link.getTabId().getString());
		assertEquals(1, link.getElementLinks().size());
		assertEquals(EL_06, link.getElementLinks().get(0).getId().getString());
		assertEquals(ADD_CONDITION, link.getElementLinks().get(0).getContext().getAdditional());
		assertTrue(link.getElementLinks().get(0).getKeepUserSettings());

		assertNotNull(clone.getNavigatorElementLink());
		assertEquals("nLink", clone.getNavigatorElementLink().getId().getString());
		assertTrue(clone.getNavigatorElementLink().getRefresh());

		assertNotSame(action, clone);
		assertNotSame(action.getDataPanelLink(), clone.getDataPanelLink());
		assertNotSame(action.getContext(), clone.getContext());
		assertNotSame(action.getDataPanelLink().getElementLinks().get(0), clone.getDataPanelLink()
				.getElementLinks().get(0));

		assertTrue(clone.containsServerActivity());
		Activity act = clone.getServerActivities().get(0);
		assertNotNull(act);
		assertTrue(act.getOnServerSide());
		assertEquals("01", act.getId().getString());
		assertEquals(TEST_ACTIVITY_NAME, act.getName());
		assertEquals(ADD_CONDITION, act.getContext().getAdditional());
		assertNotSame(action.getServerActivities().get(0), act);

		assertTrue(clone.containsClientActivity());
		act = clone.getClientActivities().get(0);
		assertNotNull(act);
		assertFalse(act.getOnServerSide());
		assertEquals("01", act.getId().getString());
		assertEquals("testJS", act.getName());
		assertEquals(ADD_CONDITION, act.getContext().getAdditional());
		assertNotSame(action.getClientActivities().get(0), act);
	}

	/**
	 * Проверка создания текущего контекста.
	 */
	@Test
	public void testCreateCurrentContext() {
		CompositeContext cc = CompositeContext.createCurrent();
		assertTrue(cc.addIsCurrent());
		assertTrue(cc.mainIsCurrent());
		assertNull(cc.getFilter());
		assertNull(cc.getSession());
	}

	/**
	 * Проверка создания действия для обновления элементов открытой вкладки
	 * инф.панели.
	 */
	@Test
	public void testCreateRefreshElementsAction() {
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		assertEquals(ID.createCurrentID(), action.getDataPanelLink().getDataPanelId());
		assertEquals(ID.createCurrentID(), action.getDataPanelLink().getTabId());
		assertTrue(action.getContext().mainIsCurrent());
		assertTrue(action.getContext().addIsCurrent());
		assertEquals(ShowInMode.PANEL, action.getShowInMode());
	}

	/**
	 * Проверка создания Action для Tab.
	 */
	@Test
	public void testGetTabAction() {
		DataPanelTab tab = createStdTab();
		Action action = tab.getAction();
		assertNull(action.getNavigatorElementLink());
		assertEquals(NavigatorActionType.DO_NOTHING, action.getNavigatorActionType());
		assertEquals(DataPanelActionType.REFRESH_TAB, action.getDataPanelActionType());
		assertTrue(action.getContext().addIsCurrent());
		assertTrue(action.getContext().mainIsCurrent());

		final DataPanelLink dataPanelLink = action.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(ID.createCurrentID(), dataPanelLink.getDataPanelId());
		assertEquals(TAB_2, dataPanelLink.getTabId().getString());
		assertFalse(dataPanelLink.getFirstOrCurrentTab());
		assertEquals(0, dataPanelLink.getElementLinks().size());
	}

	/**
	 * Проверка актуализации действия типа firstOrCurrent.
	 * 
	 */
	@Test
	public void testFirstOrCurrentActualize() {
		Action first = createSimpleTestAction();
		first.getDataPanelLink().setTabId(TAB_2);
		first.getContext().setAdditional(ADD_CONDITION);

		Action foc = new Action(DataPanelActionType.REFRESH_TAB);
		DataPanelLink link = foc.getDataPanelLink();
		CompositeContext cc = new CompositeContext();
		foc.setContext(cc);
		cc.setMain(MOSCOW_CONTEXT);
		link.setDataPanelId(TEST_XML);
		link.setTabId(TAB_1);
		link.setFirstOrCurrentTab(true);
		Action actual = foc.gwtClone().actualizeBy(first);

		assertEquals(NavigatorActionType.DO_NOTHING, actual.getNavigatorActionType());
		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		assertEquals(MOSCOW_CONTEXT, actual.getContext().getMain());
		assertNull(actual.getContext().getAdditional()); // !

		final DataPanelLink dataPanelLink = actual.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(TEST_XML, dataPanelLink.getDataPanelId().getString());
		assertEquals(new ID(TAB_2), dataPanelLink.getTabId()); // !
		assertTrue(dataPanelLink.getFirstOrCurrentTab()); // !
		assertEquals(0, dataPanelLink.getElementLinks().size()); // !
	}

	/**
	 * Тест на обновление дополнительного контекста.
	 * 
	 */
	@Test
	public void testActualizeActions() {
		// Grid grid = createTestGrid();
		// CompositeContext context = new CompositeContext();
		// context.setMain(MAIN_CONDITION);
		// context.setAdditional(NEW_ADD_CONDITION);
		// grid.actualizeActions(context);
		//
		// assertEquals(MAIN_CONDITION,
		// grid.getEventManager().getEvents().get(0).getAction()
		// .getDataPanelLink().getElementLinks().get(0).getContext().getMain());
		// assertEquals(MAIN_CONDITION,
		// grid.getDefaultAction().getDataPanelLink().getElementLinks()
		// .get(0).getContext().getMain());
		// assertEquals(MAIN_CONDITION,
		// grid.getDefaultAction().getServerActivities().get(0)
		// .getContext().getMain());
		//
		// assertEquals(NEW_ADD_CONDITION,
		// grid.getEventManager().getEvents().get(0).getAction()
		// .getDataPanelLink().getElementLinks().get(0).getContext().getAdditional());
		// assertEquals(NEW_ADD_CONDITION,
		// grid.getDefaultAction().getDataPanelLink()
		// .getElementLinks().get(0).getContext().getAdditional());
		// assertEquals(NEW_ADD_CONDITION,
		// grid.getDefaultAction().getServerActivities().get(0)
		// .getContext().getAdditional());
		// assertEquals(NEW_ADD_CONDITION,
		// grid.getDefaultAction().getClientActivities().get(0)
		// .getContext().getAdditional());
	}

	// private Grid createTestGrid() {
	// Grid grid = new Grid();
	// GridEvent event = new GridEvent();
	// event.setRecordId("01");
	// Action action = createCurrentTestAction();
	// event.setAction(action);
	// grid.getEventManager().getEvents().add(event);
	// grid.setDefaultAction(action);
	// return grid;
	// }

	/**
	 * Тест на действие обновления навигатора.
	 * 
	 */
	@Test
	public void testRefreshNavigatorAction() {
		final int el2 = 2;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, el2);
		assertTrue(action.getNavigatorElementLink().getRefresh());
		final int el3 = 3;
		action = getAction(TREE_MULTILEVEL_XML, 0, el3);
		assertTrue(action.getNavigatorElementLink().getRefresh());
		assertNotNull(action.getNavigatorElementLink().getId());
		final int el4 = 4;
		action = getAction(TREE_MULTILEVEL_XML, 0, el4);
		assertNotNull(action.getNavigatorElementLink().getId());
	}

	/**
	 * Проверка считывания информации о модальном окне для действия.
	 * 
	 */
	@Test
	public void testActionModalInfo() {
		Action action = new Action();
		assertNull(action.getModalWindowInfo());

		final int elNum = 5;
		action = getAction(TREE_MULTILEVEL_XML, 0, elNum);
		assertEquals("test_action_name", action.getModalWindowInfo().getCaption());
		final int mwWidth = 99;
		assertEquals(mwWidth, action.getModalWindowInfo().getWidth().intValue());
		final int mwHeight = 98;
		assertEquals(mwHeight, action.getModalWindowInfo().getHeight().intValue());
		assertTrue(action.getModalWindowInfo().getShowCloseBottomButton());
	}

	/**
	 * Создаем тестовое действие с не дефолтными значениями всех возможных
	 * атрибутов.
	 * 
	 * @return - действие.
	 */
	private Action createComplexTestAction() {
		Action action = createSimpleTestAction();

		action.setKeepUserSettings(false);
		action.setShowInMode(ShowInMode.MODAL_WINDOW);

		ModalWindowInfo mwi = new ModalWindowInfo();
		mwi.setCaption("mwi");
		mwi.setHeight(DEF_SIZE_VALUE);
		mwi.setWidth(DEF_SIZE_VALUE);
		mwi.setShowCloseBottomButton(true);
		action.setModalWindowInfo(mwi);

		NavigatorElementLink nLink = new NavigatorElementLink();
		nLink.setId("nLink");
		nLink.setRefresh(true);
		action.setNavigatorElementLink(nLink);

		action.setContext(getComplexTestContext());

		DataPanelElementLink elLink = action.getDataPanelLink().getElementLinkById(EL_06);
		elLink.setKeepUserSettings(true);

		Activity act = Activity.newServerActivity("01", TEST_ACTIVITY_NAME);
		act.setContext(getComplexTestContext());
		action.getServerActivities().add(act);

		act = Activity.newClientActivity("01", "testJS");
		act.setContext(getComplexTestContext());
		action.getClientActivities().add(act);

		action.determineState();

		return action;
	}

	@SuppressWarnings("unused")
	private Action createCurrentTestAction() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		CompositeContext context = CompositeContext.createCurrent();
		action.setContext(context);

		DataPanelLink link = action.getDataPanelLink();
		link.setDataPanelId(CanBeCurrent.CURRENT_ID);
		link.setTabId(CanBeCurrent.CURRENT_ID);
		CompositeContext elContext = context.gwtClone();

		DataPanelElementLink elLink = new DataPanelElementLink(EL_06, elContext);
		link.getElementLinks().add(elLink);

		Activity act = Activity.newServerActivity("01", TEST_ACTIVITY_NAME);
		act.setContext(context);
		action.getServerActivities().add(act);

		act = Activity.newClientActivity("01", TEST_ACTIVITY_NAME);
		act.setContext(context);
		action.getClientActivities().add(act);

		action.determineState();
		return action;
	}

	private CompositeContext getComplexTestContext() {
		CompositeContext context = getSimpleTestContext();
		context.setFilter(FILTER_CONDITION);
		context.setAdditional(ADD_CONDITION);
		context.setSession(SESSION_CONDITION);

		return context;
	}

	/**
	 * Проверка работы функции Action.filterBy.
	 * 
	 */
	@Test
	public void testActionFilterBy() {
		final int actionWithChildNumber = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
		action.filterBy(FILTER_CONDITION);
		assertEquals(FILTER_CONDITION, action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getFilter());
		assertEquals(FILTER_CONDITION, action.getContext().getFilter());
		assertEquals(FILTER_CONDITION, action.getServerActivities().get(0).getContext()
				.getFilter());
		assertEquals(FILTER_CONDITION, action.getClientActivities().get(0).getContext()
				.getFilter());
	}

	/**
	 * Проверка генерации фильтрующего контекста.
	 */
	@Test
	public void testActionGenerateFilterContextLine() {
		String filter = CompositeContext.generateFilterContextLine("add_context1");
		filter = filter + CompositeContext.generateFilterContextLine("add_context2");
		filter = CompositeContext.generateFilterContextGeneralPart(filter);
		assertEquals(
				"<filter><context>add_context1</context><context>add_context2</context></filter>",
				filter);
	}

	/**
	 * Тест для настройки keepUserSettings.
	 * 
	 */
	@Test
	public void testActionKeepUserSettings() {
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		action.getDataPanelLink().getElementLinks().add(new DataPanelElementLink());
		action.determineState();
		assertTrue(action.getKeepUserSettings());
		assertFalse(action.getDataPanelLink().getElementLinks().get(0).getKeepUserSettings());

		final int actionWithChildNumber = 5;
		action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
		action.determineState();
		assertFalse(action.getKeepUserSettings());
		assertFalse(action.getDataPanelLink().getElementLinks().get(0).getKeepUserSettings());
		assertTrue(action.getDataPanelLink().getElementLinks().get(1).getKeepUserSettings());

		action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		action.getDataPanelLink().getElementLinks().add(new DataPanelElementLink());
		action.determineState();
		action.setKeepUserSettingsForAll(true);
		assertTrue(action.getKeepUserSettings());
		assertTrue(action.getDataPanelLink().getElementLinks().get(0).getKeepUserSettings());
	}

	/**
	 * Проверка считывания блока действия, касающегося серверной активности.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testServerActivityRead() {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		assertTrue(action.containsServerActivity());
		assertEquals(1, action.getServerActivities().size());
		Activity sa = action.getServerActivities().get(0);
		assertEquals("srv01", sa.getId().getString());
		assertEquals("exec_test", sa.getName());
		assertTrue(sa.getOnServerSide());
		assertNotNull(sa.getContext());
		assertEquals(action.getContext().getMain(), sa.getContext().getMain());
		assertEquals(
				"<context:somexml someattr=\"value\" ><other ></other>test</context:somexml>", sa
						.getContext().getAdditional());
	}

	/**
	 * Проверка работы функции
	 * {@link ru.curs.showcase.app.api.event.Action#needGeneralContext
	 * Action.needGeneralContext}.
	 */
	@Test
	public void testNeedGeneralContext() {
		Action action = new Action();
		DataPanelLink dpl = new DataPanelLink();
		dpl.setDataPanelId("test1.xml");
		dpl.setTabId("1");
		action.setDataPanelLink(dpl);
		action.setContext(CompositeContext.createCurrent());
		action.determineState();
		assertTrue(action.needGeneralContext());

		action = new Action();
		action.setContext(CompositeContext.createCurrent());
		Activity act = Activity.newServerActivity("01", "test_proc");
		act.setContext(CompositeContext.createCurrent());
		action.getServerActivities().add(act);
		action.determineState();
		assertTrue(action.needGeneralContext());

		action = new Action();
		action.setContext(CompositeContext.createCurrent());
		act = Activity.newClientActivity("01", "test_proc");
		act.setContext(CompositeContext.createCurrent());
		action.getClientActivities().add(act);
		action.determineState();
		assertTrue(action.needGeneralContext());

		action = new Action();
		NavigatorElementLink nel = new NavigatorElementLink();
		nel.setId("01");
		action.setNavigatorElementLink(nel);
		action.determineState();
		assertFalse(action.needGeneralContext());

		action = new Action();
		nel = new NavigatorElementLink();
		nel.setRefresh(true);
		action.setNavigatorElementLink(nel);
		action.determineState();
		assertFalse(action.needGeneralContext());
	}

	/**
	 * Проверка считывания действия, содержащего вызовы действия на клиенте, не
	 * связанные с навигатором и инф. панелью.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testReadClientActivity() {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		assertTrue(action.containsClientActivity());
		assertEquals(1, action.getClientActivities().size());
		Activity ac = action.getClientActivities().get(0);
		assertEquals("show_moscow", ac.getName());
		assertEquals("cl01", ac.getId().getString());
		assertFalse(ac.getOnServerSide());
	}

	@Test
	public void testAddRelatedToContext() {
		CompositeContext parent = CompositeContext.createCurrent();
		CompositeContext related = new CompositeContext();
		related.setMain(MAIN_CONDITION);
		related.setAdditional(ADD_CONDITION);
		related.setFilter(FILTER_CONDITION);
		related.setSession(SESSION_CONDITION);
		related.setSessionParamsMap(new TreeMap<String, ArrayList<String>>());
		related.getRelated().put(new ID("rrid"), new CompositeContext());
		parent.addRelated("rid", related);

		assertEquals(1, parent.getRelated().size());
		CompositeContext test = parent.getRelated().values().iterator().next();
		assertNull(test.getMain());
		assertEquals(ADD_CONDITION, test.getAdditional());
		assertEquals(FILTER_CONDITION, test.getFilter());
		assertNull(test.getSession());
		assertTrue(test.getSessionParamsMap().isEmpty());
		assertTrue(test.getRelated().isEmpty());
	}

	@Test
	public void testActionSetAdditionalContext() {
		final int actionWithChildNumber = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
		action.setAdditionalContext(ADD_CONDITION);
		assertEquals(ADD_CONDITION, action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());
		assertEquals(ADD_CONDITION, action.getServerActivities().get(0).getContext()
				.getAdditional());
		assertEquals(ADD_CONDITION, action.getClientActivities().get(0).getContext()
				.getAdditional());
	}

	@Test
	public void testActionSetMainContext() {
		final int actionWithChildNumber = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
		action.setMainContext(MAIN_CONDITION);
		assertEquals(MAIN_CONDITION, action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getMain());
		assertEquals(MAIN_CONDITION, action.getServerActivities().get(0).getContext().getMain());
		assertEquals(MAIN_CONDITION, action.getClientActivities().get(0).getContext().getMain());
	}

	@Test
	public void testSetRelated() {
		final int actionWithChildNumber = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
		GridContext related = getExtGridContext(getTestContext1());
		CompositeContext parent = new CompositeContext();
		parent.addRelated("id", related);
		action.setRelated(parent);

		checkForRelated(action.getServerActivities().get(0).getContext());
		checkForRelated(action.getContext());
	}

	private void checkForRelated(final CompositeContext saContext) {
		assertEquals(1, saContext.getRelated().size());
		assertEquals("curRecordId", ((GridContext) saContext.getRelated().values().iterator()
				.next()).getCurrentRecordId());
	}

	@Test
	public void testGridContext() {
		CompositeContext context = getComplexTestContext();
		GridContext ces = new GridContext(context);

		assertEquals(MAIN_CONDITION, ces.getMain());
		assertEquals(ADD_CONDITION, ces.getAdditional());
		assertEquals(FILTER_CONDITION, ces.getFilter());
		assertEquals(SESSION_CONDITION, ces.getSession());
	}

	// !!! @Test(expected = JythonException.class)
	public void testJythonActivityException() {
		Activity activity = Activity.newServerActivity("id", "TestJythonProc.py");
		CompositeContext context = new CompositeContext();
		activity.setContext(context);
		ActivityGateway gateway = new ActivityJythonGateway();
		gateway.exec(activity);
	}

	// !!! @Test(expected = ValidateException.class)
	public void testJythonNoValidateActivity() {
		Activity activity = Activity.newServerActivity("id", "NoValidateJythonProc.py");
		CompositeContext context = new CompositeContext();
		context.setMain("Мейн контекст");
		context.setSession("<sessioninfo/>");
		activity.setContext(context);

		ActivityGateway gateway = new ActivityJythonGateway();
		gateway.exec(activity);
	}

	@Test
	public void testComplexDataPanelElementContext() {
		DataPanelElementInfo elementInfo = getTestGridInfo();
		DataPanelElementContext test =
			new DataPanelElementContext(CompositeContext.createCurrent(), elementInfo);

		assertEquals(CompositeContext.createCurrent().getMain(), test.getCompositeContext()
				.getMain());
		assertEquals(CompositeContext.createCurrent().getSession(), test.getCompositeContext()
				.getSession());
		assertEquals(elementInfo, test.getElementInfo());
		assertEquals(elementInfo.getTab().getDataPanel().getId(), test.getPanelId());
		assertEquals(elementInfo.getId(), test.getElementId());
		assertNotNull(test.toString());
	}

	@Test
	public void testSimpleDataPanelElementContext() {
		DataPanelElementContext test =
			new DataPanelElementContext(CompositeContext.createCurrent());

		assertEquals(CompositeContext.createCurrent().getMain(), test.getCompositeContext()
				.getMain());
		assertEquals(CompositeContext.createCurrent().getSession(), test.getCompositeContext()
				.getSession());
		assertNull(test.getElementInfo());
		assertNull(test.getPanelId());
		assertNull(test.getElementId());
		assertNotNull(test.toString());
	}

	@Test(expected = AppLogicError.class)
	public void testWrongGetKeepUserSettings() {
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		Action ac = new Action();
		NavigatorElementLink nlink = new NavigatorElementLink();
		nlink.setId("id");
		ac.setNavigatorElementLink(nlink);
		elementInfo.getKeepUserSettings(ac);
	}

	@Test
	public void testGetKeepUserSettings() {
		final String id = "id";
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo(id, DataPanelElementType.WEBTEXT);
		Action ac = new Action(DataPanelActionType.REFRESH_TAB);
		DataPanelLink dLink = new DataPanelLink();
		dLink.setDataPanelId("a.xml");
		dLink.setTabId("1");
		ac.setDataPanelLink(dLink);
		ac.setKeepUserSettings(true);

		assertEquals(ac.getKeepUserSettings(), elementInfo.getKeepUserSettings(ac));
		ac.setKeepUserSettings(false);
		assertEquals(ac.getKeepUserSettings(), elementInfo.getKeepUserSettings(ac));
	}

	@Test
	public void gwtCloneMustCloneAllXFormContextAttributes() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		XFormContext xcontext = new XFormContext(CompositeContext.createCurrent());
		final String formData = "<test/>";
		xcontext.setFormData(formData);
		xcontext.setKeepUserSettings(false);
		xcontext.addRelated(new ID("01"), xcontext);
		XFormContext xcontext2 = xcontext.gwtClone();
		assertTrue(ReflectionUtils.equals(xcontext, xcontext2));

		XFormContext relatedContext = (XFormContext) xcontext2.getRelated().get(new ID("01"));
		assertEquals(formData, relatedContext.getFormData());
		assertFalse(relatedContext.getKeepUserSettings());
	}

	@Test
	public void xformContextSetKeepUserSettingsToTrueByDefault() {
		XFormContext xcontext = new XFormContext();
		assertTrue(xcontext.getKeepUserSettings());
	}

	@Test
	public void applyCompositeContextShouldOverwriteAllCompositeContextAtributes() {
		GridContext gcontext = new GridContext(CompositeContext.createCurrent());
		final String currentRecordId = "recId";
		gcontext.setCurrentRecordId(currentRecordId);
		gcontext.addRelated(new ID("01"), gcontext);

		assertEquals(CanBeCurrent.CURRENT_ID, gcontext.getMain());
		assertEquals(1, gcontext.getRelated().size());

		CompositeContext appliedContext = getTestContext2();
		gcontext.applyCompositeContext(appliedContext);

		assertEquals(currentRecordId, gcontext.getCurrentRecordId());
		assertEquals(MAIN_CONDITION, gcontext.getMain());
		assertEquals(0, gcontext.getRelated().size());
	}
}
