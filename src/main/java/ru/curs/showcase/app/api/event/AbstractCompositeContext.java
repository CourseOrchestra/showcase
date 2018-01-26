package ru.curs.showcase.app.api.event;

/**
 * Интерфейс составного контекста. В отличие от класса, содержит только методы
 * доступа к контекстам.
 * 
 * @author den
 * 
 */
public interface AbstractCompositeContext {

	String getSession();

	void setSession(String aSession);

	String getMain();

	void setMain(String aMain);

	String getAdditional();

	void setAdditional(String aAdditional);

	String getFilter();

	void setFilter(String aFilter);

}