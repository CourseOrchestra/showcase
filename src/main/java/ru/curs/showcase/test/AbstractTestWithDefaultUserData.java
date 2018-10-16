package ru.curs.showcase.test;

import org.junit.jupiter.api.*;

//import org.junit.Before;

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
	@BeforeEach
	public void beforeTest() {
		super.beforeTest();
		setDefaultUserData();
	}
}
