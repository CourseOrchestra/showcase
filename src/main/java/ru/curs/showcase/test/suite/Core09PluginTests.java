package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.plugin.*;

/**
 * Все тесты для UI плагинов.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ PluginTest.class, PluginFactoryTest.class, PluginSLTest.class })
public class Core09PluginTests {

}
