package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.*;

/**
 * Все тесты для навигатора.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ NavigatorGatewayTest.class, NavigatorFactoryTest.class, NavigatorSLTest.class })
public class Core01NavigatorTests {

}
