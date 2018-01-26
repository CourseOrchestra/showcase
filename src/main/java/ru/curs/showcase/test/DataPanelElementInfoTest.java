package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.core.html.xform.XFormInfoFactory;

/**
 * Класс для тестирования функций DataPanelElementInfo и его наследников.
 * 
 * @author den
 * 
 */
public final class DataPanelElementInfoTest extends AbstractTestWithDefaultUserData {
	private static final String TRANSFORM_NAME = "transformName";
	private static final String PROC_NAME = "procName";

	/**
	 * Проверка значений атрибутов панели по умолчанию.
	 */
	@Test
	public void testCreateDPElementInfo() {
		DataPanelElementInfo dpei = new DataPanelElementInfo("01", DataPanelElementType.GEOMAP);
		assertFalse(dpei.getCacheData());
		assertFalse(dpei.getRefreshByTimer());
		assertEquals(DataPanelElementInfo.DEF_TIMER_INTERVAL, dpei.getRefreshInterval().intValue());
	}

	@Test
	public void testCreateDPWithTable() {
		DataPanel dp = new DataPanel("zzz");
		DataPanelTab tab = dp.add("t01", "Табличная вкладка");
		tab.setLayout(DataPanelTabLayout.TABLE);
		DataPanelTR tr = tab.addTR();
		DataPanelTD td = tr.add();
		DataPanelElementInfo dpei = td.add("elId", DataPanelElementType.CHART);

		DataPanelTab tab2 = dp.add("t02", "Вертикальная вкладка");
		DataPanelElementInfo dpei2 = tab2.addElement("elId2", DataPanelElementType.GEOMAP);

		assertEquals(tab, dpei.getTab());
		assertEquals(tab, tr.getTab());
		assertEquals(tr, td.getTR());
		assertTrue(tab.getTrs().contains(tr));
		assertTrue(tr.getTds().contains(td));
		assertEquals(dpei, td.getElement());
		assertTrue(tab.getElements().isEmpty());

		assertEquals(tab2, dpei2.getTab());
		assertTrue(tab2.getElements().contains(dpei2));
		assertTrue(tab2.getTrs().isEmpty());
	}

	@Test
	public void testGenerateXFormsSQLSubmissionInfo() {
		DataPanelElementInfo elInfo = XFormInfoFactory.generateXFormsSQLSubmissionInfo(PROC_NAME);

		assertEquals(PROC_NAME, elInfo.getProcName());
		assertEquals(DataPanelElementType.XFORMS, elInfo.getType());
		assertNotNull(elInfo.getId());
	}

	@Test
	public void testgenerateXFormsTransformationInfo() {
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo(TRANSFORM_NAME);

		assertEquals(TRANSFORM_NAME, elInfo.getTransformName());
		assertEquals(DataPanelElementType.XFORMS, elInfo.getType());
		assertNotNull(elInfo.getId());
	}
}
