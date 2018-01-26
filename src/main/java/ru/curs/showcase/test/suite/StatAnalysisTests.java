package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.JDependTest;

/**
 * Тесты, выполняющие статический анализ кода, вычисляющие метрики и проверяющие
 * качество кода.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ JDependTest.class })
public class StatAnalysisTests {

}
