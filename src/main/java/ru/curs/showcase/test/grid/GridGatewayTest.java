package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тесты для шлюза грида.
 * 
 * @author den
 * 
 */
public class GridGatewayTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основной тест на получение данных из БД.
	 * 
	 */
	@Test
	public void testGetData() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getTestGridInfo();

		GridGateway gateway = new GridDBGateway();
		gateway.getRawDataAndSettings(context, element);
	}

	/**
	 * Проверка получения только данных для грида.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetDataOnly() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo2();

		Connection conn = ConnectionFactory.getInstance().acquire();
		try (GridDBGateway gateway = new GridDBGateway(conn)) {
			GridContext gc = new GridContext();
			// gc.setPageNumber(2);
			// gc.setPageSize(2);
			gc.assignNullValues(context);
			gateway.getRawData(gc, element);
		}
	}

	/**
	 * Проверка получения только данных для грида.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetDataOnlyV2() throws SQLException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		GridGateway gateway = new GridDBGateway();
		GridContext gc = new GridContext(context);
		RecordSetElementRawData res = gateway.getRawData(gc, elInfo);

		assertNotNull(res);
		assertEquals(gc, res.getCallContext());
		assertEquals(elInfo, res.getElementInfo());
		assertNull(res.getSettings());
		assertNotNull(res.getXmlDS());
		assertFalse(res.getStatement().getConnection().isClosed());
		assertNotNull(res.getStatement());

		res.close();
	}

	/**
	 * Проверка получения настроек грида.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetSettingsOnly() throws SQLException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		ElementSettingsGateway gateway = new ElementSettingsDBGateway();
		RecordSetElementRawData res = gateway.getRawData(context, elInfo);

		assertNotNull(res);
		assertEquals(context, res.getCallContext());
		assertEquals(elInfo, res.getElementInfo());
		assertNull(res.getSettings());
		res.prepareSettings();
		assertNotNull(res.getSettings());
		assertNotNull(res.getStatement());
		assertFalse(res.getStatement().getConnection().isClosed());
		assertNull(res.nextResultSet());

		res.close();
	}
}
