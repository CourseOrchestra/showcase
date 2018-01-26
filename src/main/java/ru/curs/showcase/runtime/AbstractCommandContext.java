package ru.curs.showcase.runtime;

/**
 * Абстрактный интерфейс для контекста команды.
 * 
 * @author den
 * 
 */
public interface AbstractCommandContext {

	String getRequestId();

	String getCommandName();

	String getUserName();

	String getUserdata();

	void setCommandName(final String aCommandName);

	void setRequestId(final String aRequestId);

	void setUserName(final String aUserName);

	void setUserdata(final String aUserdata);

}