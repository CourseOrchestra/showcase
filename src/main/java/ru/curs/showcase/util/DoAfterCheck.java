package ru.curs.showcase.util;

import java.lang.annotation.*;

/**
 * Аннотация для AspectJ, говорящая, что после выполнения метода проверки
 * (возвращающего boolean) нужно выполнить еще одну проверку. Класс и имя
 * статического метода задаются как параметры аннотации.
 * 
 * @author den
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DoAfterCheck {

	String className();

	String methodName();

}
