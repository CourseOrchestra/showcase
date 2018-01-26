package ru.curs.showcase.app.api;

/**
 * Интерфейс, определяющий, может ли элемент иметь текущее состояние. Текущее
 * состояние задается через установку CURRENT_ID в один из идентификаторов
 * элемента.
 * 
 * @author den
 * 
 */
public interface CanBeCurrent {
	/**
	 * Признак того, что идентификатор ссылается на текущее значение.
	 */
	String CURRENT_ID = "current";
}
