package ru.curs.showcase.test.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.core.html.webtext.WebTextGetCommand;
import ru.curs.showcase.core.primelements.navigator.NavigatorGetCommand;
import ru.curs.showcase.test.AbstractTest;

/**
 * Проверка ActionTabFinder через SL.
 * 
 * @author den
 * 
 */
public class ActionTabFinderSLTest extends AbstractTest {
	// !!! @Test
	public void testReadFirstTabFromDBFromNavigatorDynSessionContext() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator navigator = command.execute();

		assertEquals("1", navigator.getGroupById("00").getElementById("04").getAction()
				.getDataPanelLink().getTabId().getString());
	}

	@Test
	// @Ignore
	// !!!
			public
			void testReadFirstTabFromDBFromEventDynMainContext() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_dyn_dp_main");

		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertEquals("01", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getTabId().getString());
	}

	// !!! @Test
	public void testReadFirstTabFromDBFromEventDynSessionContext() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_dyn_dp_session");

		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertEquals("1", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getTabId().getString());
	}

	@Test
	public void testTabIdCheck() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		final String webtextFile = "test01.xml";
		element.setProcName(webtextFile);

		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();
		assertEquals("2", wt.getEventManager().getEventForLink("1").get(0).getAction()
				.getDataPanelLink().getTabId().toString());
	}
}
