package ru.curs.showcase.util;

import java.lang.annotation.*;

/**
 * Аннотация, содержащая описание класса, используемое в логе и при выводе
 * сообщений пользователю, в т.ч. ошибок.
 * 
 * @author den
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {

	/**
	 * Название процесса, выполняемого данным классом.
	 */
	String process();
}
