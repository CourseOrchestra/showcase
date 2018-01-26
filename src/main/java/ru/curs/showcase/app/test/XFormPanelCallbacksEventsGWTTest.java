package ru.curs.showcase.app.test;

import ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Класс для тестирования XFormPanelCallbacksEvents.
 */
public class XFormPanelCallbacksEventsGWTTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	@Override
	public void gwtSetUp() {

		XFormTestsCommon.clearDOM();

		XFormPanelCallbacksEvents.setTestXFormPanel(null);

	}

	/**
	 * Тест ф-ции xFormPanelClickSave.
	 */
	public void testXFormPanelClickSave() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// assertNull(AppCurrContext.getInstance().getCurrentAction().getKeepUserSettings());
		// XFormPanelCallbacksEvents.setTestXFormPanel(xfp);
		//
		// final String xformId = "1";
		// final String linkId = "1";
		// final String data = XFormTestsCommon.XFORM_DATA;
		//
		// XFormPanelCallbacksEvents.xFormPanelClickSave(xformId, linkId, data);
		//
		// assertEquals(DataPanelActionType.DO_NOTHING,
		// AppCurrContext.getInstance()
		// .getCurrentAction().getDataPanelActionType());
		// assertTrue(AppCurrContext.getInstance().getCurrentAction().getKeepUserSettings());

	}

	/**
	 * Тест ф-ции xFormPanelClickFilter.
	 */
	public void testXFormPanelClickFilter() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// assertNull(AppCurrContext.getInstance().getCurrentAction().getKeepUserSettings());
		// XFormPanelCallbacksEvents.setTestXFormPanel(xfp);
		//
		// final String xformId = "1";
		// final String linkId = "1";
		// final String data = XFormTestsCommon.XFORM_DATA;
		//
		// XFormPanelCallbacksEvents.xFormPanelClickFilter(xformId, linkId,
		// data);
		//
		// assertEquals(DataPanelActionType.DO_NOTHING,
		// AppCurrContext.getInstance()
		// .getCurrentAction().getDataPanelActionType());
		// assertTrue(AppCurrContext.getInstance().getCurrentAction().getKeepUserSettings());

	}

	/**
	 * Тест ф-ции xFormPanelClickUpdate.
	 */
	public void testXFormPanelClickUpdate() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// assertNull(AppCurrContext.getInstance().getCurrentAction().getKeepUserSettings());
		// XFormPanelCallbacksEvents.setTestXFormPanel(xfp);
		//
		// final String xformId = "1";
		// final String linkId = "1";
		// final String data = XFormTestsCommon.XFORM_DATA;
		//
		// XFormPanelCallbacksEvents.xFormPanelClickUpdate(xformId, linkId,
		// data);
		//
		// assertEquals(DataPanelActionType.DO_NOTHING,
		// AppCurrContext.getInstance()
		// .getCurrentAction().getDataPanelActionType());
		// assertTrue(AppCurrContext.getInstance().getCurrentAction().getKeepUserSettings());

	}

	/**
	 * Тест ф-ции showSelector.
	 */
	public void testShowSelector() {
		assertTrue(true);
	}

	/**
	 * Тест ф-ции downloadFile.
	 */
	public void testDownloadFile() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// XFormPanelCallbacksEvents.setTestXFormPanel(xfp);
		//
		// final String xformId = "1";
		// final String linkId = "1";
		// final String data = XFormTestsCommon.XFORM_DATA;
		//
		// XFormPanelCallbacksEvents.downloadFile(xformId, linkId, data);
		//
		// assertEquals(DataPanelActionType.DO_NOTHING,
		// AppCurrContext.getInstance()
		// .getCurrentAction().getDataPanelActionType());
		// assertNull(AppCurrContext.getInstance().getCurrentAction().getKeepUserSettings());

	}

	/**
	 * Тест ф-ции uploadFile.
	 */
	public void testUploadFile() {
		assertTrue(true);
	}

	/**
	 * Тест ф-ции getCurrentPanel.
	 */
	public void testGetCurrentPanel() {

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// XFormPanelCallbacksEvents.setTestXFormPanel(xfp);
		//
		// assertEquals(XFormTestsCommon.LEN_MAININSTANCE,
		// XFormPanelCallbacksEvents.getCurrentPanel("1").fillAndGetMainInstance().trim().length());

	}
}
