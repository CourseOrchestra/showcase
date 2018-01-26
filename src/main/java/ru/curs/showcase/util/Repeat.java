package ru.curs.showcase.util;

import java.lang.annotation.*;

/**
 * Замена для пока не сделанной аннотации Repeat у JUnit.
 * 
 * @author den
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repeat {

	int count();

}
