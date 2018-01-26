package ru.curs.showcase.app.test;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Класс для тестирования XFormPanel.
 */
public class XFormPanelGWTTest extends GWTTestCase {

	// private static final String DYNASTYLE = "dynastyle";
	// private static final String TARGET = "target";

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	@Override
	public void gwtSetUp() {

		XFormTestsCommon.clearDOM();

	}

	/**
	 * Тест без начального показа XFormPanel.
	 */
	public void testConstr1() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests1();
		// assertNotNull(xfp);
		//
		// assertEquals("1", xfp.getElementInfo().getId().getString());
		// assertNull(xfp.getContext());
		//
		// assertNotNull(xfp.getSelSrv());
		// assertNotNull(xfp.getPanel());
		// assertNull(xfp.getElement());
		// assertNull(xfp.getDataService());
		//
		// UploadWindow uw = new UploadWindow("TestUploadWindow");
		// uw.hide();
		// xfp.setUw(uw);
		// assertEquals("TestUploadWindow", xfp.getUw().getText());
		//
		// xfp.showPanel();
		// assertTrue(xfp.getPanel().isVisible());
		// xfp.hidePanel();
		// assertFalse(xfp.getPanel().isVisible());
		//
		// xfp.setElementInfo(null);
		// assertNull(xfp.getElementInfo());

	}

	/**
	 * Тест с начальным показом XFormPanel.
	 */
	public void testConstr2() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// assertNotNull(xfp);
		//
		// assertNotNull(xfp.getContext());
		// assertEquals("1", xfp.getElementInfo().getId().getString());
		//
		// generalPartTest(xfp);

	}

	// private void generalPartTest(final XFormPanel xfp) {
	// assertEquals(1, xfp.getPanel().getWidgetCount());
	//
	// com.google.gwt.user.client.Element dynastyle =
	// DOM.getElementById(DYNASTYLE);
	// assertEquals(1, dynastyle.getChildCount());
	//
	// com.google.gwt.user.client.Element target = DOM.getElementById(TARGET);
	// assertEquals(2, target.getChildCount());
	//
	// assertEquals(XFormTestsCommon.LEN_MAININSTANCE,
	// xfp.fillAndGetMainInstance().trim()
	// .length());
	//
	// assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
	// .getCurrentAction().getDataPanelActionType());
	// }

	/**
	 * Тест1 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel1() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests1();
		// assertNotNull(xfp);
		//
		// CompositeContext context = new CompositeContext();
		//
		// XForm xform = XFormTestsCommon.createXForms2();
		//
		// xfp.reDrawPanelExt(context, xform);
		// assertNotNull(xfp.getContext());
		//
		// generalPartTest(xfp);

	}

	/**
	 * Тест2 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel2() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// assertNotNull(xfp);
		//
		// CompositeContext context = new CompositeContext();
		//
		// XForm xform = XFormTestsCommon.createXForms2();
		//
		// xfp.reDrawPanelExt(context, xform);
		//
		// assertEquals(1, xfp.getPanel().getWidgetCount());
		//
		// com.google.gwt.user.client.Element dynastyle =
		// DOM.getElementById(DYNASTYLE);
		// assertEquals(1, dynastyle.getChildCount());
		//
		// com.google.gwt.user.client.Element target =
		// DOM.getElementById(TARGET);
		// assertEquals(2, target.getChildCount());

	}

	/**
	 * Тест ф-ции unloadAllSubforms.
	 */
	public void testUnloadAllSubforms() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// assertNotNull(xfp);
		//
		// XFormPanel.unloadAllSubforms();
		//
		// com.google.gwt.user.client.Element dynastyle =
		// DOM.getElementById(DYNASTYLE);
		// assertNull(dynastyle);
		//
		// com.google.gwt.user.client.Element target =
		// DOM.getElementById(TARGET);
		// assertEquals(0, target.getChildCount());

	}

	/**
	 * Тест ф-ции saveSettings.
	 */
	public void testSaveSettings() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// assertNotNull(xfp);
		//
		// xfp.setNeedResetLocalContext(true);
		// xfp.setNeedResetLocalContext(false);
		//
		// com.google.gwt.user.client.Element dynastyle =
		// DOM.getElementById(DYNASTYLE);
		// assertEquals(1, dynastyle.getChildCount());
		//
		// com.google.gwt.user.client.Element target =
		// DOM.getElementById(TARGET);
		// assertEquals(2, target.getChildCount());

	}

}
