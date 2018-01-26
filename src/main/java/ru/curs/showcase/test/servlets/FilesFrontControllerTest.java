package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.*;

import javax.servlet.ServletException;

import org.junit.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Тесты для FilesFrontController.
 * 
 * @author den
 * 
 */
public class FilesFrontControllerTest extends AbstractServletTest {

	private static final String SECURED_GRIDFILEDOWNLOAD = "/secured/gridfiledownload";
	private static final String RECORD_ID = "recordId";
	private static final String LINK_ID = "linkId";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String TXT = ".txt";
	private FilesFrontController controller;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		controller = new FilesFrontController();
	}

	// !!! @Test
	public void testXFormDownload() throws ServletException, IOException {
		request().setServletPath("/secured/download");
		request().addParameter(LINK_ID, "03");
		String data = loadTestData(XFormContext.class.getSimpleName() + TXT);
		request().addParameter(XFormContext.class.getName(), data);
		data =
			loadTestData(DataPanelElementInfo.class.getSimpleName()
					+ DataPanelElementType.XFORMS.toString() + TXT);
		request().addParameter(DataPanelElementInfo.class.getName(), data);

		controller.doPost(request(), response());

		checkOkResponse("application/octet-stream");
		InputStream ris = FilesFrontControllerTest.class.getResourceAsStream("result.xml");
		assertEquals(TextUtils.streamToString(ris), response().getContentAsString());
		assertEquals("attachment; filename*=\"navigator.xml\"",
				response().getHeader(CONTENT_DISPOSITION));
	}

	// !!! @Test
	public void testExportToExcel() throws ServletException, IOException {
		request().setServletPath("/secured/gridToExcel");
		request().addParameter(GridToExcelExportType.class.getName(),
				GridToExcelExportType.CURRENTPAGE.toString());
		String data = loadTestData(GridContext.class.getSimpleName() + TXT);
		request().addParameter(GridContext.class.getName(), data);
		data =
			loadTestData(DataPanelElementInfo.class.getSimpleName()
					+ DataPanelElementType.GRID.toString() + TXT);
		request().addParameter(DataPanelElementInfo.class.getName(), data);
		// data = loadTestData(ColumnSet.class.getSimpleName() + TXT);
		// request().addParameter(ColumnSet.class.getName(), data);

		controller.doPost(request(), response());

		checkOkResponse("application/vnd.ms-excel");
		assertTrue(response().getContentAsString().contains(
				"<?mso-application progid=\"Excel.Sheet\""));
		assertEquals("attachment; filename*=\"table.xls\"",
				response().getHeader(CONTENT_DISPOSITION));
	}

	// !!! @Test
	public void testGridDownload() throws ServletException, IOException {
		request().setServletPath(SECURED_GRIDFILEDOWNLOAD);
		request().addParameter(LINK_ID, "11");
		final String recId = "7451DF70-ACC3-48CC-8CC0-3092F8A237BE";
		request().addParameter(RECORD_ID, recId);
		String data = loadTestData(CompositeContext.class.getSimpleName() + TXT);
		request().addParameter(CompositeContext.class.getName(), data);
		data =
			loadTestData(DataPanelElementInfo.class.getSimpleName()
					+ DataPanelElementType.GRID.toString() + TXT);
		request().addParameter(DataPanelElementInfo.class.getName(), data);

		controller.doPost(request(), response());

		checkOkResponse("application/octet-stream");
		InputStream ris = FilesFrontControllerTest.class.getResourceAsStream("result.xml");
		assertEquals(TextUtils.streamToString(ris), response().getContentAsString());
		assertEquals("attachment; filename*=\"navigator_" + recId + ".xml\"", response()
				.getHeader(CONTENT_DISPOSITION));
	}

	@Test
	public void testWrongType() throws ServletException, IOException {
		request().setServletPath("/secured/fakecommand");
		try {
			controller.doPost(request(), response());
			fail();
		} catch (HTTPRequestUnknownParamException e) {
			assertEquals("Неизвестная команда: FAKECOMMAND", e.getLocalizedMessage());
		}
	}

	// !!! @Test
	public void testNoParam1() throws IOException, ServletException {
		request().setServletPath(SECURED_GRIDFILEDOWNLOAD);
		request().addParameter(LINK_ID, "11");
		request().addParameter(RECORD_ID, "1");
		String data = loadTestData(CompositeContext.class.getSimpleName() + TXT);
		request().addParameter(CompositeContext.class.getName(), data);

		try {
			controller.doPost(request(), response());
			fail();
		} catch (HTTPRequestRequiredParamAbsentException e) {
			assertEquals(HTTPRequestRequiredParamAbsentException.ERROR_MES
					+ DataPanelElementInfo.class.getName(), e.getLocalizedMessage());
		}
	}

	// !!! @Test
	public void testNoParam2() throws ServletException, IOException {
		request().setServletPath(SECURED_GRIDFILEDOWNLOAD);
		request().addParameter(LINK_ID, "11");
		String data = loadTestData(CompositeContext.class.getSimpleName() + TXT);
		request().addParameter(CompositeContext.class.getName(), data);
		data =
			loadTestData(DataPanelElementInfo.class.getSimpleName()
					+ DataPanelElementType.GRID.toString() + TXT);
		request().addParameter(DataPanelElementInfo.class.getName(), data);

		try {
			controller.doPost(request(), response());
			fail();
		} catch (HTTPRequestRequiredParamAbsentException e) {
			assertEquals(HTTPRequestRequiredParamAbsentException.ERROR_MES + RECORD_ID,
					e.getLocalizedMessage());
		}
	}

	// !!! @Test(expected = IncompatibleRemoteServiceException.class)
	public void testWrongParam() throws ServletException, IOException {
		request().setServletPath(SECURED_GRIDFILEDOWNLOAD);
		request().addParameter(LINK_ID, "11");
		request().addParameter(RECORD_ID, "1");
		String data = loadTestData(CompositeContext.class.getSimpleName() + TXT);
		request().addParameter(CompositeContext.class.getName(), data);
		request().addParameter(DataPanelElementInfo.class.getName(), "");

		controller.doPost(request(), response());
	}
}
