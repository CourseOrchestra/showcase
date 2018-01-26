package ru.curs.showcase.test;

import org.junit.Before;

/**
 * Абстрактный класс для тестов, устанавливающий дефолтную userdata перед каждым
 * тестом.
 * 
 * @author den
 * 
 */
public class AbstractTestWithDefaultUserData extends AbstractTest {
	/**
	 * Установка userdata по умолчанию для тестов, не вызывающих функции SL.
	 */
	@Override
	@Before
	public void beforeTest() {
		super.beforeTest();
		setDefaultUserData();
	}
}
