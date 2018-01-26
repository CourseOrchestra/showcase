package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.Action;

/**
 * Класс информационной панели. Панель содержит набор вкладок, каждая из которых
 * содержит набор элементов.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPanel implements SerializableElement {

	private static final long serialVersionUID = 1182249077845843177L;

	/**
	 * Идентификатор панели. В случае хранения панели в файле - имя файла без
	 * расширения.
	 */
	private ID id;

	/**
	 * Набор вкладок панели.
	 */
	private List<DataPanelTab> tabs = new ArrayList<DataPanelTab>();

	public DataPanel(final String aId) {
		super();
		id = new ID(aId);
	}

	public DataPanel() {
		super();
	}

	/**
	 * Возвращает активную вкладку для переданного объекта действия. Если в
	 * действии не задана вкладка панели - открывается первая вкладка.
	 * 
	 * @param action
	 *            - действие.
	 * @return - вкладка.
	 */
	public DataPanelTab getActiveTabForAction(final Action action) {
		if (action.getDataPanelLink() != null) {
			return getTabById(action.getDataPanelLink().getTabId());
		} else {
			return tabs.iterator().next();
		}
	}

	/**
	 * Возвращает вкладку по id.
	 * 
	 * @param aTabId
	 *            - id.
	 * @return вкладку.
	 */
	public DataPanelTab getTabById(final ID aTabId) {
		for (DataPanelTab current : tabs) {
			if (current.getId().equals(aTabId)) {
				return current;
			}
		}
		return null;
	}

	public DataPanelTab getTabById(final String aTabId) {
		return getTabById(new ID(aTabId));
	}

	public final List<DataPanelTab> getTabs() {
		return tabs;
	}

	public final void setTabs(final List<DataPanelTab> aTabs) {
		this.tabs = aTabs;
	}

	/**
	 * Добавляет и инициализирует вкладку к панели.
	 * 
	 * @param tabId
	 *            - id вкладки.
	 * @param tabNamr
	 *            - наименование вкладки.
	 * @return - вкладка.
	 */
	public DataPanelTab add(final String tabId, final String tabNamr) {
		DataPanelTab res = new DataPanelTab();
		res.setId(tabId);
		res.setName(tabNamr);
		res.setDataPanel(this);
		res.setPosition(tabs.size());
		tabs.add(res);
		return res;
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
}
