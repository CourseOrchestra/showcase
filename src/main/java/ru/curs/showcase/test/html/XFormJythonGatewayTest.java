package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.xform.XFormJythonGateway;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Тест шлюза элемента XForm, источником данных для которого является Jython
 * скрипты.
 * 
 * @author bogatov
 * 
 */
public class XFormJythonGatewayTest extends AbstractTestWithDefaultUserData {

	private DataPanelElementInfo getDataPanelElementInfoForDownloadFile() {
		DataPanelElementInfo elInfo =
			new DataPanelElementInfo("xformsDownloadId", DataPanelElementType.XFORMS);
		elInfo.setPosition(1);
		elInfo.setProcName("xforms_proc_all");
		elInfo.setTemplateName("DownloadUpload_Jython_Template.xml");
		generateTestTabWithElement(elInfo);

		ID procID = new ID("050101");
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(procID);
		proc.setName("xform/XFormsDownloadByUserdata.py");
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		elInfo.getProcs().put(procID, proc);
		generateTestTabWithElement(elInfo);
		return elInfo;
	}

	private DataPanelElementInfo getDataPanelElementInfoForUploadFile() {
		DataPanelElementInfo elInfo =
			new DataPanelElementInfo("xformsUploadId", DataPanelElementType.XFORMS);
		elInfo.setPosition(1);
		elInfo.setProcName("xforms_proc_all");
		elInfo.setTemplateName("DownloadUpload_Jython_Template.xml");
		generateTestTabWithElement(elInfo);

		ID procID = new ID("050102");
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(procID);
		proc.setName("xform/XFormsUploadByUserdata.py");
		proc.setType(DataPanelElementProcType.UPLOAD);
		elInfo.getProcs().put(procID, proc);
		generateTestTabWithElement(elInfo);
		return elInfo;
	}

	@Test
	@Ignore
	// !!!
			public
			void testDownloadFile() {
		XFormContext context = new XFormContext();
		XMLSessionContextGenerator sessionContextGenerator =
			new XMLSessionContextGenerator(context);
		String sessionContext = sessionContextGenerator.generate();
		context.setSession(sessionContext);
		DataPanelElementInfo elInfo = getDataPanelElementInfoForDownloadFile();
		XFormJythonGateway gateway = new XFormJythonGateway();
		ID procID = new ID("050101");
		OutputStreamDataFile res = gateway.downloadFile(context, elInfo, procID);
		assertNotNull(res);
		assertNotNull(res.getName());
		assertNotNull(res.getData());
	}

	@Test
	@Ignore
	// !!!
			public
			void testUploadFile() {
		final String linkId = TEST_XML_FILE;
		DataFile<InputStream> file =
			new DataFile<InputStream>(FileUtils.loadClassPathResToStream(linkId), linkId);
		assertEquals(TextUtils.DEF_ENCODING, file.getEncoding());

		XFormContext context = new XFormContext();
		XMLSessionContextGenerator sessionContextGenerator =
			new XMLSessionContextGenerator(context);
		String sessionContext = sessionContextGenerator.generate();
		context.setSession(sessionContext);
		DataPanelElementInfo elInfo = getDataPanelElementInfoForUploadFile();
		XFormJythonGateway gateway = new XFormJythonGateway();
		ID procID = new ID("050102");

		gateway.uploadFile(context, elInfo, procID, file);
	}
}
