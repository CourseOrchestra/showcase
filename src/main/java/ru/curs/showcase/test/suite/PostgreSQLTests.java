package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.PostgreSQLTest;

/**
 * Тесты работы с PostgreSQL.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ PostgreSQLTest.class })
public class PostgreSQLTests {

}
