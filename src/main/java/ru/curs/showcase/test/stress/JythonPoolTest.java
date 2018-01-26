package ru.curs.showcase.test.stress;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.xform.XFormSaveCommand;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.TextUtils;

/**
 * Тесты для пула jython интерпретаторов.
 * 
 * @author den
 * 
 */
public class JythonPoolTest extends AbstractTest {

	private static final int USERDATA_COUNT = 3;

	private static final int INVOCATION_COUNT = 1000;

	private static final int THREAD_POOL_SIZE = 10;

	private static final AtomicInteger COUNTER = new AtomicInteger(0);

	private static final Set<String> STORAGE = Collections.synchronizedSet(new HashSet<String>());

	private static final String[] USERDATAS = {
			ExchangeConstants.DEFAULT_USERDATA, TEST1_USERDATA, TEST2_USERDATA };

	private static final Random RAND_GENERATOR = new Random();

	public static Set<String> getStorage() {
		return STORAGE;
	}

	@Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATION_COUNT)
	public void jythonXFormSaveProcShouldNotCacheData() {
		final String data = "<tag>" + String.valueOf(COUNTER.incrementAndGet()) + "</tag>";
		final String userDataId = USERDATAS[RAND_GENERATOR.nextInt(USERDATA_COUNT)];
		XFormContext context =
			new XFormContext(new CompositeContext(generateTestURLParams(userDataId)), data);
		context.setMain(UUID.randomUUID().toString());

		DataPanelElementInfo elementInfo =
			generateTestXFormSaveProcElement("xform/XFormStressTestSaveProc.py");

		XFormSaveCommand command = new XFormSaveCommand(context, elementInfo);
		command.execute();
	}

	@Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATION_COUNT)
	public void jythonInterpretatorShouldCleanGlobalVars() throws IOException {
		InputStream is = JythonPoolTest.class.getResourceAsStream("goodTestFormData.xml");
		final String data1 =
			String.format(TextUtils.streamToString(is), UUID.randomUUID().toString());
		is = JythonPoolTest.class.getResourceAsStream("badTestFormData.xml");
		final String data2 = TextUtils.streamToString(is);
		String data;
		if (RAND_GENERATOR.nextBoolean()) {
			data = data1;
		} else {
			data = data2;
		}

		XFormContext context =
			new XFormContext(new CompositeContext(generateTestURLParams(USERDATAS[0])), data);
		context.setMain(UUID.randomUUID().toString());

		DataPanelElementInfo elementInfo =
			generateTestXFormSaveProcElement("xform/formCardSaveTest.py");

		XFormSaveCommand command = new XFormSaveCommand(context, elementInfo);
		command.execute();
	}

	private DataPanelElementInfo generateTestXFormSaveProcElement(final String file) {
		DataPanelElementInfo elementInfo = getTestXForms1Info();
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("saveproc");
		proc.setName(file);
		proc.setType(DataPanelElementProcType.SAVE);
		elementInfo.getProcs().clear();
		elementInfo.getProcs().put(proc.getId(), proc);
		return elementInfo;
	}

}
