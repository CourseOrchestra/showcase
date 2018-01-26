package ru.curs.showcase.runtime;


/**
 * Интерфейс декоратора события лога. Реализация для конкретного логгера также
 * должна имплементировать AbstractCommandContext.
 * 
 * @author den
 * 
 */
public interface LoggingEventDecorator extends AbstractCommandContext {

	long getTimeStamp();

	String getMessage();

	String getLevel();

	String getTime();

	String getDirection();

	String getProcess();

	String getParams();

	boolean isSatisfied(final String fieldName, final String fieldValue);

	String EXCEPTION_SOURCE = "Источник ошибки:";

}