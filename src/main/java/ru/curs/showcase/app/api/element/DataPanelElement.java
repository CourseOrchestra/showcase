package ru.curs.showcase.app.api.element;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;

/**
 * Базовый класс для элементов информационной панели.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DataPanelElement implements SerializableElement {

	private static final long serialVersionUID = 8787932721898449225L;

	/**
	 * Идентификатор элемента в панели. По данному идентификатору можно
	 * сопоставить DataPanelElement и DataPanelElementInfo.
	 */
	private ID id;

	/**
	 * Действие по умолчанию. Возможное применение сокрытие зависимых элементов
	 * при перерисовке главного элемента при условии, что в нем не выделен
	 * активный элемент.
	 */
	private Action defaultAction;

	/**
	 * Менеджер событий в гриде.
	 */
	private EventManager<? extends Event> eventManager = initEventManager();

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	public DataPanelElement(final DataPanelElementInfo elInfo) {
		super();
		id = elInfo.getId();
	}

	public DataPanelElement() {
		super();
	}

	public ID getId() {
		return id;
	}

	public void setId(final ID aId) {
		id = aId;
	}

	public void setId(final String aId) {
		id = new ID(aId);
	}

	public final Action getDefaultAction() {
		return defaultAction;
	}

	public final void setDefaultAction(final Action aAction) {
		defaultAction = aAction;
	}

	protected final void setEventManager(final EventManager<? extends Event> aEventManager) {
		eventManager = aEventManager;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

	/**
	 * Инициализирует менеджер событий.
	 * 
	 * @return - менеджер событий.
	 */
	protected abstract EventManager<? extends Event> initEventManager();

	public EventManager<? extends Event> getEventManager() {
		return eventManager;
	}

	/**
	 * Возвращает действие для отрисовки зависимого элемента.
	 * 
	 * @return - действие.
	 */
	public Action getActionForDependentElements() {
		return defaultAction;
	}

	/**
	 * Функция для актуализации состояния действий для всех событий в элемента,
	 * а также события по умолчанию на основе переданного контекста.
	 * 
	 * @param callContext
	 *            - контекст.
	 */
	public void actualizeActions(final CompositeContext callContext) {
		for (Event event : eventManager.getEvents()) {
			Action action = event.getAction();
			action.actualizeBy(callContext);
		}

		if (defaultAction != null) {
			defaultAction.actualizeBy(callContext);
		}
	}

	/**
	 * Проверяет действия на корректность вызовом функции самопроверки.
	 * Возвращает первое некорректное действие.
	 */
	public Action checkActions() {
		for (Event event : eventManager.getEvents()) {
			Action action = event.getAction();
			if (!action.isCorrect()) {
				return action;
			}
		}

		if (defaultAction != null) {
			if (!defaultAction.isCorrect()) {
				return defaultAction;
			}
		}
		return null;
	}

}
