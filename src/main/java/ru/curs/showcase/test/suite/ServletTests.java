package ru.curs.showcase.test.suite;

import org.junit.extensions.cpsuite.*;
import org.junit.extensions.cpsuite.ClasspathSuite.BaseTypeFilter;
import org.junit.extensions.cpsuite.ClasspathSuite.IncludeJars;
import org.junit.runner.RunWith;

import ru.curs.showcase.test.servlets.AbstractServletTest;

/**
 * Сборка тестов сервлетов.
 * 
 * @author den
 * 
 */
@RunWith(ClasspathSuite.class)
@IncludeJars(false)
@BaseTypeFilter(AbstractServletTest.class)
public class ServletTests {

}
