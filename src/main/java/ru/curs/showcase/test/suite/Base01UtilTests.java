package ru.curs.showcase.test.suite;

import org.junit.extensions.cpsuite.*;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.IncludeJars;
import org.junit.runner.RunWith;

/**
 * Тесты для utils.
 * 
 * @author den
 * 
 */
@RunWith(ClasspathSuite.class)
@IncludeJars(false)
@ClassnameFilters({ "ru.curs.showcase.test.util.*" })
public class Base01UtilTests {

}
