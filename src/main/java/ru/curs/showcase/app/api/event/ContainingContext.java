package ru.curs.showcase.app.api.event;

/**
 * Элемент, содержащий мультиконтекст.
 * 
 * @author den
 * 
 */
public interface ContainingContext {
	/**
	 * Получение контекста.
	 */
	CompositeContext getContext();
}
