package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.event.*;

/**
 * Тесты для действий и событий.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ActionAndContextTest.class, ActionAndContextSLTest.class, EventsTest.class })
public class Base02EventAndActionTests {

}
