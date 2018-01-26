package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.chart.*;

/**
 * Тесты для графиков.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ChartTest.class, ChartGatewayTest.class, ChartFactoryTest.class, ChartSLTest.class })
public class Core05ChartTests {

}
