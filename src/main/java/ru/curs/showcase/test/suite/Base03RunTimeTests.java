package ru.curs.showcase.test.suite;

import org.junit.extensions.cpsuite.*;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.IncludeJars;
import org.junit.runner.RunWith;

/**
 * Тесты модулей времени выполнения.
 * 
 * @author den
 * 
 */
@RunWith(ClasspathSuite.class)
@IncludeJars(false)
@ClassnameFilters({ "ru.curs.showcase.test.runtime.*" })
public class Base03RunTimeTests {

}
