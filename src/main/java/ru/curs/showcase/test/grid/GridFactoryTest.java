package ru.curs.showcase.test.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.core.grid.GridDataFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тестовый класс для фабрики гридов.
 * 
 * @author den
 * 
 */
public class GridFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String GRIDBAL_TEST_PROPERTIES = "gridbal.test.properties";

	/**
	 * Тестирует задание профайла настроек из хранимой процедуры.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProfileSelection() throws Exception {

		// Refactoring

		// GridContext context = getTestGridContext1();
		// DataPanelElementInfo element = getDPElement("test.xml", "2", "4");
		//
		// GridGateway gateway = new GridDBGateway();
		// RecordSetElementRawData raw = gateway.getRawDataAndSettings(context,
		// element);
		// GridFactory factory = new GridFactory(raw);
		// Grid grid = factory.build();
		// assertEquals(GRIDBAL_TEST_PROPERTIES,
		// factory.serverState().getProfile());
		//
		// assertEquals(1, grid.getDataSet().getRecordSet().getPageNumber());
		//
		// ProfileReader gp =
		// new ProfileReader(GRIDBAL_TEST_PROPERTIES,
		// SettingsFileType.GRID_PROPERTIES);
		// gp.init();
		//
		// Boolean defSelectRecord =
		// gp.getBoolValue(DefaultGridSettingsApplyStrategy.DEF_SELECT_WHOLE_RECORD);
		// assertEquals(defSelectRecord,
		// grid.getUISettings().isSelectOnlyRecords());
		// final String fontWidth = "27";
		// assertEquals(fontWidth,
		// grid.getDataSet().getRecordSet().getRecords().get(0).getFontSize());
	}

	/**
	 * Проверка работы функции
	 * {@link ru.curs.showcase.core.grid.GridMetaFactory#makeSafeXMLAttrValues}
	 * .
	 */
	@Test
	public void testGridLinkReplaceXMLServiceSymbols() {
		assertEquals("<link href=\"ya.ru?search=aa&amp;bla&amp;ab\" "
				+ "image=\"xxx.jpg\"  text=\"&lt;&quot; &lt;&gt; &gt; a&apos;&quot;\"  />",
				GridDataFactory.makeSafeXMLAttrValues("<link href=\"ya.ru?search=aa&amp;bla&ab\" "
						+ "image=\"xxx.jpg\"  text=\"<&quot; &lt;&gt; > a'\"\"  />"));
	}

	@Test
	public void testLoadIDAndCSS() throws Exception {

		// Refactoring

		// GridContext context = new GridContext(getTestContext1());
		// context.setIsFirstLoad(true);
		// DataPanelElementInfo elInfo = new DataPanelElementInfo("01",
		// DataPanelElementType.GRID);
		// elInfo.setProcName("grid_portals_id_and_css");
		// generateTestTabWithElement(elInfo);
		//
		// GridGateway gateway = new GridDBGateway();
		// RecordSetElementRawData raw = gateway.getRawDataAndSettings(context,
		// elInfo);
		// GridFactory factory = new GridFactory(raw);
		// Grid grid = factory.build();
		//
		// assertNotNull(grid.getAutoSelectRecord());
		// final String recId = "<id>77F60A7C-42EB-4E32-B23D-F179E58FB138</id>";
		// assertEquals(recId, grid.getAutoSelectRecord().getId());
		// assertNotNull(grid.getEventManager().getEventForCell(recId, "URL",
		// InteractionType.SINGLE_CLICK));
		// assertEquals("grid-record-bold grid-record-italic",
		// grid.getDataSet().getRecordSet()
		// .getRecords().get(0).getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG));
	}

}
