package ru.curs.showcase.test.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.test.AbstractTest;

/**
 * Компонентные тесты PageGrid и его внутренних компонентов.
 * 
 */
public class PageGridComponentTest extends AbstractTest {

	private static final String HEADER =
		"<h3 class=\"testStyle\" >Потери - Всего зерна, тыс. тонн </h3>";
	private static final String FOOTER =
		"<h3 class=\"testStyle\" >Футер. Потери - Всего зерна, тыс. тонн </h3>";

	private static final int LIVE_INFO_OFFSET = 0;
	private static final int LIVE_INFO_LIMIT = 15;
	private static final int LIVE_INFO_TOTALCOUNT = 81;

	private static final Integer UI_SETTINGS_GRID_HEIGHT = 400;
	private static final String UI_SETTINGS_GRID_WIDTH = "95%";

	private static final String COL_ID = "col2";
	private static final String COL_CAPTION = "Картинка";

	@SuppressWarnings("unused")
	private static final String REC_ID = "9";

	@SuppressWarnings("unused")
	private static final int DATA_SIZE = 15;

	// !!! @Test
	public void testPageGridMetadataProc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.JS_PAGE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "8", "81");

		GridMetadataGetCommand command = new GridMetadataGetCommand(context, elInfo);
		GridMetadata lgm = command.execute();

		assertEquals(HEADER, lgm.getHeader());
		assertEquals(FOOTER, lgm.getFooter());

		assertEquals(LIVE_INFO_OFFSET, lgm.getLiveInfo().getOffset());
		assertEquals(LIVE_INFO_LIMIT, lgm.getLiveInfo().getLimit());
		assertEquals(LIVE_INFO_TOTALCOUNT, lgm.getLiveInfo().getTotalCount());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, lgm.getUISettings().getGridHeight());
		assertEquals(UI_SETTINGS_GRID_WIDTH, lgm.getUISettings().getGridWidth());

		assertEquals(COL_ID, lgm.getColumns().get(1).getId());
		assertEquals(COL_CAPTION, lgm.getColumns().get(1).getCaption());
		assertEquals(HorizontalAlignment.CENTER, lgm.getColumns().get(1).getHorizontalAlignment());
		assertEquals(COL_CAPTION, lgm.getColumns().get(1).getId());

	}

	@Test
	public void testPageGridDataProc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.JS_PAGE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "8", "81");

		GridDataGetCommand command = new GridDataGetCommand(context, elInfo, true);
		@SuppressWarnings("unused")
		GridData lgd = command.execute();

		// assertEquals(LIVE_INFO_OFFSET, lgd.getOffset());
		// assertEquals(LIVE_INFO_TOTALCOUNT, lgd.getTotalLength());
		// assertEquals(DATA_SIZE, lgd.getData().size());
		//
		// LiveGridExtradata lge = lgd.getLiveGridExtradata();
		//
		// assertEquals(REC_ID, lge.getAutoSelectRecordId());
		//
		// assertTrue(lge.getDefaultAction().getKeepUserSettings());
		// assertEquals(DataPanelActionType.RELOAD_ELEMENTS,
		// lge.getDefaultAction()
		// .getDataPanelActionType());
		// assertNotNull(lge.getDefaultAction().getContext());

	}

}
