package ru.curs.showcase.test.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.xml.XMLFormatException;

/**
 * Проверка работы механизма ActionTabFinder.
 * 
 * @author den
 * 
 */
public class ActionTabFinderTest extends AbstractTestWithDefaultUserData {
	@Test
	// @Ignore
	// !!!
			public
			void testReadFirstTabForFileDPFromNavigator() {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);

		assertEquals("1", action.getDataPanelLink().getTabId().getString());
	}

	@Test
	// @Ignore
	// !!!
			public
			void testReadFirstTabFromDBFromNavigator() {
		final int actionNumber = 3;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);

		assertEquals("01", action.getDataPanelLink().getTabId().getString());
	}

	@Test
	// @Ignore
	// !!!
			public
			void testReadFirstTabFromDBFromNavigatorDynMainContext() {
		final int actionNumber = 4;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);

		assertEquals("01", action.getDataPanelLink().getTabId().getString());
	}

	@Test(expected = XMLFormatException.class)
	public void testReadFirstTabIdFromDBFromNavigatorDynamicEmpty() {
		getAction("tree_multilevel.wrong2.xml", 0, 0);
	}
}
