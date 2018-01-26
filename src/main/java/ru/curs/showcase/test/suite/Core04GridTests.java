package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.grid.*;

/**
 * Все тесты для грида.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
		GridTest.class, GridGatewayTest.class, GridFactoryTest.class,
		GridExportToExcelSLTest.class, LiveGridComponentTest.class, TreeGridComponentTest.class,
		PageGridComponentTest.class })
public class Core04GridTests {

}
