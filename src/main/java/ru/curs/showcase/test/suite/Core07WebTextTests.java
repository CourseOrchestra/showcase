package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.html.*;

/**
 * Все тесты для вебтекста.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ WebTextGatewayAndFactoryTest.class, WebTextSLTest.class })
public class Core07WebTextTests {

}
