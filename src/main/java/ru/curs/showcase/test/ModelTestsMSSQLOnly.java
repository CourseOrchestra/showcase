package ru.curs.showcase.test;

import org.junit.extensions.cpsuite.*;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.SuiteTypes;
import org.junit.runner.RunWith;

/**
 * Все тесты модели.
 * 
 * @author den
 * 
 */
@RunWith(ClasspathSuite.class)
@SuiteTypes(org.junit.extensions.cpsuite.SuiteType.RUN_WITH_CLASSES)
@ClassnameFilters({ "!.*ModelTests.*", "!.*Postgre.*" })
public class ModelTestsMSSQLOnly {

}
