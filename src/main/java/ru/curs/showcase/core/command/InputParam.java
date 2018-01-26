package ru.curs.showcase.core.command;

import java.lang.annotation.*;

/**
 * Признак входного параметра для команды.
 * 
 * @author den
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InputParam {

}
