package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.GridMetadataGetCommand;
import ru.curs.showcase.test.AbstractTest;

/**
 * Компонентные тесты TreeGrid и его внутренних компонентов.
 * 
 */
public class TreeGridComponentTest extends AbstractTest {

	private static final String HEADER =
		"<h3 class=\"testStyle\" >Потери - Всего зерна, тыс. тонн </h3>";
	private static final String FOOTER =
		"<h3 class=\"testStyle\" >Футер. Потери - Всего зерна, тыс. тонн </h3>";

	private static final String HEADER2 = "<h3 class=\"testStyle\" >Хедер tree-грида</h3>";
	private static final String FOOTER2 = "<h3 class=\"testStyle\" >Футер tree-грида</h3>";

	private static final Integer UI_SETTINGS_GRID_HEIGHT = 400;
	private static final Integer UI_SETTINGS_GRID_HEIGHT2 = 500;

	private static final String COL_ID = "col1";
	private static final String COL_CAPTION = "Регион";

	private static final String COL_CAPTION2 = "Название";

	private static final String FONT_SIZE = "1em";

	// !!! @Test
	public void testTreeGridMetadata1Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.JS_TREE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "7", "71");

		GridMetadataGetCommand command = new GridMetadataGetCommand(context, elInfo);
		GridMetadata lgm = command.execute();

		assertEquals(HEADER, lgm.getHeader());
		assertEquals(FOOTER, lgm.getFooter());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, lgm.getUISettings().getGridHeight());

		assertEquals(COL_ID, lgm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION, lgm.getColumns().get(0).getCaption());
		assertEquals(HorizontalAlignment.LEFT, lgm.getColumns().get(0).getHorizontalAlignment());
		assertEquals(COL_CAPTION, lgm.getColumns().get(0).getId());

		assertEquals(FONT_SIZE, lgm.getFontSize());

		assertNull(lgm.getTextColor());
		assertNull(lgm.getBackgroundColor());
		assertFalse(lgm.getFontModifiers().contains(FontModifier.BOLD));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.ITALIC));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.UNDERLINE));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.STRIKETHROUGH));

	}

	// !!! @Test
	public void testTreeGridMetadata2Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.JS_TREE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "7", "72");

		GridMetadataGetCommand command = new GridMetadataGetCommand(context, elInfo);
		GridMetadata lgm = command.execute();

		assertEquals(HEADER2, lgm.getHeader());
		assertEquals(FOOTER2, lgm.getFooter());

		assertEquals(UI_SETTINGS_GRID_HEIGHT2, lgm.getUISettings().getGridHeight());

		assertEquals(COL_ID, lgm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION2, lgm.getColumns().get(0).getCaption());
		assertEquals(HorizontalAlignment.LEFT, lgm.getColumns().get(0).getHorizontalAlignment());
		assertEquals(COL_CAPTION2, lgm.getColumns().get(0).getId());
	}

}
