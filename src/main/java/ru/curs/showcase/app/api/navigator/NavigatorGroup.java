package ru.curs.showcase.app.api.navigator;

import java.util.*;

import ru.curs.showcase.app.api.*;

/**
 * Класс группы элементов навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorGroup extends NamedElement {

	private static final long serialVersionUID = 2639632044936098205L;

	/**
	 * Корневые элементы в группе.
	 */
	private List<NavigatorElement> elements = new ArrayList<NavigatorElement>();

	/**
	 * Идентификатор изображения, связанного с группой. В случае отсутствия
	 * изображения - передается пустая строка.
	 */
	private String imageId;

	public final List<NavigatorElement> getElements() {
		return elements;
	}

	public final void setElements(final List<NavigatorElement> aElements) {
		this.elements = aElements;
	}

	public final String getImageId() {
		return imageId;
	}

	public final void setImageId(final String aImageId) {
		this.imageId = aImageId;
	}

	/**
	 * Возвращает элемент 1-го уровня по его id.
	 * 
	 * @param id
	 *            - id.
	 * @return - элемент.
	 */
	public NavigatorElement getElementById(final ID id) {
		if (id == null) {
			return null;
		}
		for (NavigatorElement current : elements) {
			if (id.equals(current.getId())) {
				return current;
			}
		}
		return null;
	}

	public NavigatorElement getElementById(final String id) {
		return getElementById(new ID(id));
	}
}
