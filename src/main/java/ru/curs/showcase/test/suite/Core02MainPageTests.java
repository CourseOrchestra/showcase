package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.html.FramesSLTest;

/**
 * Тесты для модулей главной страницы.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ FramesSLTest.class })
public class Core02MainPageTests {

}
