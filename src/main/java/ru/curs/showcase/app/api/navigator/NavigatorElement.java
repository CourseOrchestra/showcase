package ru.curs.showcase.app.api.navigator;

import java.util.*;

import ru.curs.showcase.app.api.NamedElement;
import ru.curs.showcase.app.api.event.Action;

/**
 * Класс элемента навигатора. Каждый элемент может инициировать действие
 * (например, открытие информационной панели на определенной вкладке), а также
 * содержать в себе другие элементы.
 * 
 * @author den
 * 
 */
public class NavigatorElement extends NamedElement {

	private static final long serialVersionUID = 8722430867190906156L;

	/**
	 * Дочерние элементы.
	 */
	private List<NavigatorElement> elements = new ArrayList<NavigatorElement>();

	/**
	 * Действие, которое необходимо выполнить при активации элемента.
	 */
	private Action action;

	public final List<NavigatorElement> getElements() {
		return elements;
	}

	public final void setElements(final List<NavigatorElement> aElements) {
		this.elements = aElements;
	}

	public final Action getAction() {
		return action;
	}

	public final void setAction(final Action aAction) {
		this.action = aAction;
	}
}
