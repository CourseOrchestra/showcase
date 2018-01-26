package ru.curs.showcase.app.api.event;

import java.util.*;

import ru.curs.showcase.app.api.*;

/**
 * Ссылка на информационную панель. Может содержать ссылку на конкретную вкладку
 * панели. Также содержит информацию о контексте, необходимую для заполнения
 * данными элементов вкладок панели.
 * 
 * @author den
 * 
 */
public class DataPanelLink
		implements CanBeCurrent, SerializableElement, GWTClonable, SizeEstimate {

	private static final long serialVersionUID = 5921173204380210732L;

	/**
	 * Ссылка на информационную панель.
	 */
	private ID dataPanelId;

	/**
	 * Ссылка на вкладку панели, которая должна быть активизирована.
	 */
	private ID tabId;

	/**
	 * Признак того, что нужно открывать либо первую страницу в случае смены
	 * панели, либо оставаться на текущей.
	 */
	private Boolean firstOrCurrentTab = false;

	/**
	 * Признак того, что нужно кэшировать данную информационную панель.
	 */
	private Boolean dataPanelCaching = false;

	/**
	 * Коллекция элементов информационной панели, для которых нужно
	 * переопределить контекст или которые нужно перерисовать.
	 */
	private List<DataPanelElementLink> elementLinks = new ArrayList<DataPanelElementLink>();

	/**
	 * Проверяет, является ли панель текущей.
	 * 
	 * @return результат проверки.
	 */
	public Boolean isCurrentPanel() {
		return ID.createCurrentID().equals(dataPanelId);
	}

	/**
	 * Проверяет, является ли вкладка текущей.
	 * 
	 * @return результат проверки.
	 */
	public Boolean isCurrentTab() {
		return ID.createCurrentID().equals(tabId);
	}

	/**
	 * Возвращает ссылку на элемент панели, объявленный в действии, по его ID.
	 * 
	 * @param id
	 *            - идентификатор.
	 * @return - ссылка на элемент.
	 */
	public DataPanelElementLink getElementLinkById(final ID id) {
		if (id == null) {
			return null;
		}
		for (DataPanelElementLink link : elementLinks) {
			if (id.equals(link.getId())) {
				return link;
			}
		}
		return null;
	}

	public DataPanelElementLink getElementLinkById(final String id) {
		return getElementLinkById(new ID(id));
	}

	public final List<DataPanelElementLink> getElementLinks() {
		return elementLinks;
	}

	public final void setElementLinks(final List<DataPanelElementLink> aElementLinks) {
		this.elementLinks = aElementLinks;
	}

	public final ID getDataPanelId() {
		return dataPanelId;
	}

	public final void setDataPanelId(final ID aDataPanelId) {
		this.dataPanelId = aDataPanelId;
	}

	public final void setDataPanelId(final String aDataPanelId) {
		this.dataPanelId = new ID(aDataPanelId);
	}

	public final ID getTabId() {
		return tabId;
	}

	public final void setTabId(final ID aTabId) {
		this.tabId = aTabId;
	}

	public final void setTabId(final String aTabId) {
		this.tabId = new ID(aTabId);
	}

	public Boolean getFirstOrCurrentTab() {
		return firstOrCurrentTab;
	}

	public void setFirstOrCurrentTab(final Boolean aFirstOrCurrentTab) {
		firstOrCurrentTab = aFirstOrCurrentTab;
	}

	public Boolean getDataPanelCaching() {
		return dataPanelCaching;
	}

	public void setDataPanelCaching(final Boolean aDataPanelCaching) {
		dataPanelCaching = aDataPanelCaching;
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public DataPanelLink gwtClone() {
		DataPanelLink res = new DataPanelLink();
		res.dataPanelId = dataPanelId;
		res.tabId = tabId;
		res.firstOrCurrentTab = firstOrCurrentTab;
		res.dataPanelCaching = dataPanelCaching;
		for (DataPanelElementLink link : elementLinks) {
			res.getElementLinks().add(link.gwtClone());
		}
		return res;
	}

	/**
	 * Функция, создающая ссылку на "текущую" панель.
	 * 
	 * @return - ссылка на панель.
	 */
	public static DataPanelLink createCurrent() {
		DataPanelLink result = new DataPanelLink();
		result.setDataPanelId(CURRENT_ID);
		result.setTabId(CURRENT_ID);
		return result;
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		result += dataPanelId.length();
		result += tabId.length();
		for (DataPanelElementLink link : elementLinks) {
			result += link.sizeEstimate();
		}
		return result;
	}
}
