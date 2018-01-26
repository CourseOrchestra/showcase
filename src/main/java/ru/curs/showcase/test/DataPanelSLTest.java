package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.primelements.datapanel.DataPanelGetCommand;

/**
 * Тесты для фабрики информационных панелей.
 * 
 * @author den
 * 
 */
public class DataPanelSLTest extends AbstractTest {

	private static final String DATAPANEL_TEST_SQL = "datapanel/test221.sql";

	@Test
	public void testBySLFromFile() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId(TEST2_XML);
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		DataPanelElementInfo elInfo = panel.getTabs().get(0).getElements().get(0);
		assertFalse(elInfo.getCacheData());
		assertFalse(elInfo.getRefreshByTimer());
		assertEquals(DataPanelElementInfo.DEF_TIMER_INTERVAL, elInfo.getRefreshInterval()
				.intValue());
	}

	@Test
	@Ignore
	// !!!
			public
			void testBySLFromDB() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("dp0903");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		checkTestDP(panel);
	}

	@Test
	public void datapanelCanBeGeneratedByMSSQLScript() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId(DATAPANEL_TEST_SQL);
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();
		assertNotNull(panel);
		assertEquals("test221", panel.getId().toString());
		final int tabsCount = 3;
		assertEquals(tabsCount, panel.getTabs().size());
	}

	@Test
	@Ignore
	// !!!
			public
			void testBySLFromJython() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("datapanel/dp0903.py");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		checkTestDP(panel);
	}

	private void checkTestDP(final DataPanel panel) {
		assertEquals("dp0903", panel.getId().getString());
		final int tabsCount = 5;
		assertEquals(tabsCount, panel.getTabs().size());
		final int elCount = 3;
		assertEquals(elCount, panel.getTabById("02").getElements().size());
		assertNotNull(panel.getTabById("02").getElementInfoById("0202"));
	}

	@Test
	public void testShowLoadingMessage() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("1103.xml");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		assertTrue(panel.getTabById("08").getElementInfoById("0801").getShowLoadingMessage());
		assertFalse(panel.getTabById("08").getElementInfoById("d1").getShowLoadingMessage());
		assertFalse(panel.getTabById("08").getElementInfoById("d2").getShowLoadingMessage());
		assertFalse(new DataPanelElementInfo().getShowLoadingMessage());
	}

	@Test
	public void testGridElementAttrsReading() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("test.xml");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		DataPanelElementInfo elementInfo = panel.getTabById("4").getElementInfoById("401");
		assertNotNull(elementInfo);
		assertEquals(DataPanelElementSubType.JS_TREE_GRID, elementInfo.getSubtype());
		assertEquals(true, elementInfo.getEditable());
	}

	@Test
	public void testComplexTableReading() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("test.xml");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		DataPanelElementInfo elementInfo = panel.getTabById("5").getElementInfoById("e502");
		assertNotNull(elementInfo);
		assertEquals(1, elementInfo.getProcs().size());
		assertEquals(1, elementInfo.getRelated().size());
	}

	@Test
	public void testGetActiveTabForAction() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId(TEST2_XML);
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		action = new Action();
		NavigatorElementLink nlink = new NavigatorElementLink();
		nlink.setRefresh(true);
		action.setNavigatorElementLink(nlink);

		assertEquals(panel.getTabs().get(0), panel.getActiveTabForAction(action));

		action = new Action();
		DataPanelLink dlink = new DataPanelLink();
		dlink.setDataPanelId(TEST2_XML);
		dlink.setTabId("3");
		action.setDataPanelLink(dlink);

		assertEquals(panel.getTabs().get(2), panel.getActiveTabForAction(action));
	}
}
