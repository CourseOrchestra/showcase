package ru.curs.showcase.test.selector;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.core.selector.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тесты для команды получения данных селектора.
 * 
 * @author bogatov
 * 
 */
public class SelectorGetCommandTest extends AbstractTestWithDefaultUserData {

	private DataPanelElementInfo getTestGridJythonInfo() {
		DataPanelElementInfo elInfo =
			new DataPanelElementInfo("xformsSelectotId", DataPanelElementType.XFORMS);
		elInfo.setPosition(1);
		elInfo.setProcName("xforms_proc_all");
		elInfo.setTemplateName("SelectorJython_Template.xml");
		generateTestTabWithElement(elInfo);
		return elInfo;
	}

	private DataRequest getDataRequest() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridJythonInfo();

		DataRequest dataRequest = new DataRequest();
		dataRequest.setFirstRecord(0);
		dataRequest.setRecordCount(2);
		dataRequest.setCurValue("");
		dataRequest.setParams("");
		SelectorAdditionalData addData = new SelectorAdditionalData();
		addData.setContext(context);
		addData.setElementInfo(elInfo);
		dataRequest.setAddData(addData);
		return dataRequest;
	}

	/**
	 * Тест на получение данных, источником которых является Jython скрипт.
	 * 
	 */
	@Test
	public void testJythonSource() {
		DataRequest dataRequest = getDataRequest();
		dataRequest.setProcName("testSelectorListAndCount.py");

		SelectorGetCommand command = new SelectorGetCommand(dataRequest);
		ResultSelectorData result = command.execute();
		assertNotNull(result);
		assertTrue(result.getCount() > 0);
		assertTrue(!result.getDataRecordList().isEmpty());
	}

	/**
	 * Тест на получение данных, источником которых является СУБД.
	 * 
	 */
	@Test
	public void testDbSource() {
		DataRequest dataRequest = getDataRequest();
		dataRequest.setProcName("[dbo].[regions_list_and_count]");

		SelectorGetCommand command = new SelectorGetCommand(dataRequest);
		ResultSelectorData result = command.execute();
		assertNotNull(result);
		assertTrue(result.getCount() > 0);
		assertTrue(!result.getDataRecordList().isEmpty());
	}
}
