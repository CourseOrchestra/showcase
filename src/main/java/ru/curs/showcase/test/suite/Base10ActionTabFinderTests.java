package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.event.*;

/**
 * Тесты для модуля поиска вкладки для действия.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ActionTabFinderTest.class, ActionTabFinderSLTest.class })
public class Base10ActionTabFinderTests {

}
