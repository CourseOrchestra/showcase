package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.*;

/**
 * Тест на правильные настройки среды. Не должен содержать проверки, выполняемые
 * Ant.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ClassPathCheck.class })
public class Base00EnvironmentCheck extends AbstractTest {

}
