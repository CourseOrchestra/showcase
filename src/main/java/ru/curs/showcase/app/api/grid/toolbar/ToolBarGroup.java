package ru.curs.showcase.app.api.grid.toolbar;

import java.util.*;

import javax.xml.bind.annotation.*;


/**
 * Группа элементов панели инструментов грида.
 * @author bogatov
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ToolBarGroup extends BaseToolBarItem {
	private static final long serialVersionUID = 1L;
	private List<AbstractToolBarItem> items = new ArrayList<AbstractToolBarItem>();
	
	public ToolBarGroup() {
		super();
	}
	
	public void add(final AbstractToolBarItem item) {
		items.add(item);
	}
		
	public Collection<AbstractToolBarItem> getItems() {
		return items;
	}
}
