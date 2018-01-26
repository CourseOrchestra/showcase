package ru.curs.showcase.app.api;

import java.lang.annotation.*;

/**
 * Аннотация, указывающая, что отмеченное ей поле не нужно сериализовать.
 * 
 * @author den
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcludeFromSerialization {

}
