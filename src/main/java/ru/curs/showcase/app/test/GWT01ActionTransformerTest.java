package ru.curs.showcase.app.test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ru.curs.showcase.app.api.CanBeCurrent;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.client.api.ActionTransformer;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.ReflectionUtils;

/**
 * Тесты для ActionTransformer.
 * 
 * @author den
 * 
 */
public class GWT01ActionTransformerTest extends AbstractTest {

	/**
	 * Проверка актуализации Action для Tab на основе Action при обновлении
	 * данных на открытой вкладке.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * 
	 */
	@Test
	public void testRefreshTab() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Action first = createSimpleTestAction();

		DataPanelTab tab = createStdTab();
		ActionTransformer ah = new ActionTransformer();
		ah.setNavigatorAction(first.gwtClone());
		ah.setNavigatorActionFromTab(tab.getAction());
		Action actual = ah.getNavigatorAction();

		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		assertTrue(actual.getKeepUserSettings());
		final DataPanelLink dataPanelLink = actual.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(TEST_XML, dataPanelLink.getDataPanelId().getString());
		assertEquals(TAB_2, dataPanelLink.getTabId().getString());
		assertFalse(dataPanelLink.getFirstOrCurrentTab());
		assertEquals(1, dataPanelLink.getElementLinks().size());

		CompositeContext context = getSimpleTestContext();
		assertTrue(ReflectionUtils.equals(context, actual.getContext()));
	}

	/**
	 * Проверка установки DataPanelActionType.RELOAD_TAB при открытии новой
	 * вкладки на уже открытой панели.
	 * 
	 */
	@Test
	public void testSwitchToNewTab() {
		Action first = createSimpleTestAction();
		DataPanelTab tab = createStdTab();
		tab.setId("1");

		ActionTransformer ah = new ActionTransformer();
		ah.setNavigatorAction(first.gwtClone());
		ah.setNavigatorActionFromTab(tab.getAction());
		Action actual = ah.getNavigatorAction();
		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		assertFalse(actual.getKeepUserSettings());
	}

	/**
	 * Проверка работы функции setCurrentAction у ActionHolder.
	 * 
	 */
	@Test
	public void testActionHolderSetCurrentAction() {
		Action first = createSimpleTestAction();
		ActionTransformer ah = new ActionTransformer();
		ah.setNavigatorAction(first);
		first.filterBy(FILTER_CONDITION);
		ah.setCurrentActionFromElement(first, new Chart(new DataPanelElementInfo("1",
				DataPanelElementType.CHART)));

		Action insideAction = new Action(DataPanelActionType.REFRESH_TAB);
		insideAction.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId(CanBeCurrent.CURRENT_ID);
		dpLink.setTabId(TAB_2);
		insideAction.setDataPanelLink(dpLink);
		insideAction.determineState();
		Activity act = Activity.newServerActivity("01", "test");
		act.setContext(CompositeContext.createCurrent());
		insideAction.getServerActivities().add(act);
		act = Activity.newClientActivity("01", "test");
		act.setContext(CompositeContext.createCurrent());
		insideAction.getClientActivities().add(act);
		assertNotNull(ah.getCurrentAction());
		assertEquals(DataPanelActionType.REFRESH_TAB, ah.getCurrentAction()
				.getDataPanelActionType());
		assertEquals(FILTER_CONDITION, ah.getCurrentAction().getContext().getFilter());
		assertTrue(ah.getCurrentAction().getKeepUserSettings());
		assertEquals(MAIN_CONDITION, ah.getCurrentAction().getContext().getMain());
		assertEquals(MAIN_CONDITION, ah.getCurrentAction().getServerActivities().get(0)
				.getContext().getMain());
		assertEquals(MAIN_CONDITION, ah.getCurrentAction().getClientActivities().get(0)
				.getContext().getMain());
		assertEquals("2", ah.getCurrentElementId().getString());
	}

}
