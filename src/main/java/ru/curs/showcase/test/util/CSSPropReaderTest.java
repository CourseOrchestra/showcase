package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.server.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.CSSPropReader;
import ru.curs.showcase.util.exception.CSSReadException;

/**
 * Тесты CSSPropReader.
 * 
 * @author den
 * 
 */
public class CSSPropReaderTest extends AbstractTest {

	private static final String TEST_CSS =
		"WEB-INF/classes/ru/curs/showcase/test/ShowcaseDataGrid_test.css";

	@Test
	public void testCSSReadException() {
		CSSPropReader reader = new CSSPropReader();
		final String cssPath = "fakePath";
		try {
			reader.read(cssPath, "fakeSelector", "fakeProp");
			fail();
		} catch (CSSReadException e) {
			assertTrue(e.getLocalizedMessage().contains(cssPath));
		}
	}

	@Test
	public void testCSSReadNotCSS() {
		CSSPropReader reader = new CSSPropReader();
		final String cssPath =
			AppInfoSingleton.getAppInfo().getWebAppPath() + "/" + PreProcessFilter.INDEX_PAGE;
		String result = reader.read(cssPath, "fakeSelector", "fakeProp");
		assertNull(result);
	}

	/**
	 * Проверка считывания из CSS ".webmain-SmartGrid .headerGap".
	 * 
	 * @see ru.curs.showcase.util.CSSPropReader CSSPropReader
	 */
	@Test
	public void testGridColumnGapRead() {
		CSSPropReader reader = new CSSPropReader();

		String width =
			reader.read(AppInfoSingleton.getAppInfo().getWebAppPath() + "/" + TEST_CSS,
					".webmain-SmartGrid>.headerGap", ProductionModeInitializer.WIDTH_PROP);
		assertEquals("2px", width);

		width =
			reader.read(AppInfoSingleton.getAppInfo().getWebAppPath() + "/" + TEST_CSS,
					".webmain-SmartGrid:first-line", ProductionModeInitializer.WIDTH_PROP);
		assertEquals("1px", width);

		width =
			reader.read(AppInfoSingleton.getAppInfo().getWebAppPath() + "/" + TEST_CSS,
					".webmain-SmartGrid[class=\"SmartGrid\"]",
					ProductionModeInitializer.WIDTH_PROP);
		assertEquals("0px", width);

		width =
			reader.read(AppInfoSingleton.getAppInfo().getWebAppPath() + "/" + TEST_CSS, "#someId",
					ProductionModeInitializer.WIDTH_PROP);
		assertEquals("100px", width);

		width =
			reader.read(AppInfoSingleton.getAppInfo().getWebAppPath() + "/" + TEST_CSS,
					":lang(ru)", ProductionModeInitializer.WIDTH_PROP);
		assertEquals("9px", width);

		width =
			reader.read(AppInfoSingleton.getAppInfo().getWebAppPath() + "/" + TEST_CSS, "h1+h2",
					ProductionModeInitializer.WIDTH_PROP);
		assertEquals("90px", width);
	}
}
