package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.html.*;

/**
 * Все тесты для XForm.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ XFormGatewayTest.class, XFormFactoryTest.class, XFormSLTest.class })
public class Core08XFormTests {

}
