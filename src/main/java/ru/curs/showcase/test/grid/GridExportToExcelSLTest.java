package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.GridExcelExportCommand;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.ExcelFile;

/**
 * Тесты для функции экспорта в Excel из грида.
 * 
 * @author den
 * 
 */
public class GridExportToExcelSLTest extends AbstractTest {

	/**
	 * Тест экспорта всех страниц используя ServiceLayer.
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testServiceForExportAll() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();
		GridContext gc = new GridContext(context);

		GridExcelExportCommand command =
			new GridExcelExportCommand(gc, element, GridToExcelExportType.ALL);
		ExcelFile file = command.execute();

		assertNotNull(gc.getSession()); // побочный эффект - нет clone
		assertNotNull(file.getData());
		assertEquals("table.xls", file.getName());
	}

	/**
	 * Тест экспорта текущей страницы используя ServiceLayer.
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testServiceForExportCurrent() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();
		GridContext gc = new GridContext(context);
		// gc.setPageNumber(1);
		// gc.setPageSize(2);

		GridExcelExportCommand command =
			new GridExcelExportCommand(gc, element, GridToExcelExportType.CURRENTPAGE);
		command.execute();

		assertNotNull(gc.getSession());
	}
}
