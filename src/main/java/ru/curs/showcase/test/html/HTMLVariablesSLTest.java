package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.MainPage;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.frame.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTest;

/**
 * Специальный тестовый класс для проверки замены переменных в HTML блоках в
 * Showcase. TODO - доделать.
 * 
 * @author den
 * 
 */
public class HTMLVariablesSLTest extends AbstractTest {

	/**
	 * Проверка переменных в коде фреймов главной страницы.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testFramesVariables() {
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		MainPageGetCommand command = new MainPageGetCommand(context);
		MainPage page = command.execute();

		MainPageFrameSelector selector = new MainPageFrameSelector(MainPageFrameType.WELCOME);
		MainPageFrameGateway gateway = selector.getGateway();
		String raw = gateway.getRawData(context, selector.getSourceName());
		assertTrue(raw.indexOf(UserDataUtils.CURRENT_USERDATA_TEMPLATE) > -1);
		assertTrue(raw.indexOf(UserDataUtils.IMAGES_IN_GRID_DIR) > -1);

		assertEquals(-1, page.getWelcome().indexOf(UserDataUtils.CURRENT_USERDATA_TEMPLATE));
		assertEquals(-1, page.getWelcome().indexOf(UserDataUtils.IMAGES_IN_GRID_DIR));
		assertTrue(page.getWelcome().indexOf(AppInfoSingleton.getAppInfo().getCurUserDataId()) > -1);
		assertTrue(page.getWelcome().indexOf(
				UserDataUtils.getRequiredProp(UserDataUtils.IMAGES_IN_GRID_DIR)) > -1);
	}
}
