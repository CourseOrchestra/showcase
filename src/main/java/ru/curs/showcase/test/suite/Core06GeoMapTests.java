package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.geomap.*;

/**
 * Тесты для карты.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
		GeoMapTest.class, GeoMapGatewayTest.class, GeoMapFactoryTest.class, GeoMapSLTest.class })
public class Core06GeoMapTests {

}
