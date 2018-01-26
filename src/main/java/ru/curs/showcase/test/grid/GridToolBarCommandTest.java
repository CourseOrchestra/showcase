package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.toolbar.GridToolBar;
import ru.curs.showcase.core.grid.toolbar.GridToolBarCommand;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тест команды получения данных для построения панели инструментов грида.
 * 
 * @author bogatov
 * 
 */
public class GridToolBarCommandTest extends AbstractTestWithDefaultUserData {

	/**
	 * с использованием хранимой процедуры.
	 */
	@Test
	public void testDBSource() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("0101", DataPanelElementType.GRID);
		elInfo.setProcName("exttreegrid_geo_icons");
		generateTestTabWithElement(elInfo);
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("010101");
		proc.setName("gridToolBar");
		proc.setType(DataPanelElementProcType.TOOLBAR);
		elInfo.getProcs().put(proc.getId(), proc);

		GridToolBarCommand command = new GridToolBarCommand(context, elInfo);
		GridToolBar result = command.execute();
		assertNotNull(result);
		assertFalse(result.getItems().isEmpty());
	}
}
