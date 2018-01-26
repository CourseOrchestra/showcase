package ru.curs.showcase.app.api.grid.toolbar;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Панель инструментов грида.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement
@XmlSeeAlso({ ToolBarItem.class, ToolBarGroup.class })
@XmlAccessorType(XmlAccessType.FIELD)
public class GridToolBar implements SerializableElement {
	private static final long serialVersionUID = 1L;
	private List<AbstractToolBarItem> items = new ArrayList<AbstractToolBarItem>();

	private String style = null;
	private String className = null;

	public GridToolBar() {
		super();
	}

	public void add(final AbstractToolBarItem item) {
		items.add(item);
	}

	/**
	 * Возвращает элемены панели инструментов.
	 * 
	 * @return элементы типа {@link ToolBarItem} или {@link ToolBarGroup} или
	 *         {@link ToolBarSeparator}
	 */
	public Collection<AbstractToolBarItem> getItems() {
		return items;
	}

	public void setItems(final List<AbstractToolBarItem> aItems) {
		items = aItems;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(final String aStyle) {
		style = aStyle;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(final String aClassName) {
		className = aClassName;
	}

}
