package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.*;

/**
 * Тесты для инф. панели.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ DataPanelElementInfoTest.class, DataPanelFactoryTest.class, DataPanelSLTest.class })
public class Base04DataPanelTests {

}
