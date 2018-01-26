package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс вкладки информационной панели. В случае табличной раскладки панели
 * фактически включает в себя блок &lt;table&gt;.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPanelTab extends NamedElement {

	private static final long serialVersionUID = -560927756003899524L;
	/**
	 * Позиция вкладки на панели. Нумерация начинается с 0.
	 */
	private Integer position;
	/**
	 * Набор элементов на вкладке панели.
	 */
	private List<DataPanelElementInfo> elements = new ArrayList<DataPanelElementInfo>();

	/**
	 * Родительская панель.
	 */
	@ExcludeFromSerialization
	@XmlTransient
	private DataPanel dataPanel;

	private DataPanelTabLayout layout = DataPanelTabLayout.VERTICAL;

	private HTMLAttrs htmlAttrs = new HTMLAttrs();

	private List<DataPanelTR> trs = new ArrayList<DataPanelTR>();

	public final List<DataPanelElementInfo> getElements() {
		return elements;
	}

	public DataPanelTab(final Integer aPosition, final DataPanel aDataPanel) {
		super();
		position = aPosition;
		dataPanel = aDataPanel;
	}

	public DataPanelTab() {
		super();
	}

	public final void setElements(final List<DataPanelElementInfo> aElements) {
		this.elements = aElements;
	}

	public final Integer getPosition() {
		return position;
	}

	public final void setPosition(final Integer aPosition) {
		position = aPosition;
	}

	public final DataPanel getDataPanel() {
		return dataPanel;
	}

	public final void setDataPanel(final DataPanel aDataPanel) {
		dataPanel = aDataPanel;
	}

	/**
	 * Возвращает элемент по его id.
	 * 
	 * @param id
	 *            - id.
	 * @return - элемент.
	 */
	public DataPanelElementInfo getElementInfoById(final ID id) {
		if (id == null) {
			return null;
		}
		for (DataPanelElementInfo current : elements) {
			if (id.equals(current.getId())) {
				return current;
			}
		}
		for (DataPanelTR tr : trs) {
			for (DataPanelTD td : tr.getTds()) {
				if (id.equals(td.getElement().getId())) {
					return td.getElement();
				}
			}
		}
		return null;
	}

	public DataPanelElementInfo getElementInfoById(final String id) {
		return getElementInfoById(new ID(id));
	}

	/**
	 * Функция, которая возвращает действие, соответствующее выбору вкладки
	 * панели.
	 * 
	 * @return - действие.
	 */
	public Action getAction() {
		Action res = new Action();
		res.setDataPanelLink(new DataPanelLink());
		res.getDataPanelLink().setDataPanelId(CanBeCurrent.CURRENT_ID);
		res.getDataPanelLink().setTabId(getId());
		res.setContext(CompositeContext.createCurrent());
		res.determineState();
		return res;
	}

	/**
	 * Корректно добавляет элемент на вкладку.
	 * 
	 * @param aElInfo
	 *            - информация об элементе.
	 */
	public void add(final DataPanelElementInfo aElInfo) {
		getElements().add(aElInfo);
		aElInfo.setTab(this);
	}

	public DataPanelElementInfo addElement(final String aId, final DataPanelElementType aType) {
		DataPanelElementInfo aElInfo = new DataPanelElementInfo(aId, aType);
		add(aElInfo);
		return aElInfo;
	}

	public HTMLAttrs getHtmlAttrs() {
		return htmlAttrs;
	}

	public void setHtmlAttrs(final HTMLAttrs aHtmlAttrs) {
		htmlAttrs = aHtmlAttrs;
	}

	public DataPanelTabLayout getLayout() {
		return layout;
	}

	public void setLayout(final DataPanelTabLayout aLayout) {
		layout = aLayout;
	}

	public DataPanelTR addTR() {
		DataPanelTR tr = new DataPanelTR(this);
		trs.add(tr);
		return tr;
	}

	public List<DataPanelTR> getTrs() {
		return trs;
	}

	public void setTrs(final List<DataPanelTR> aTrs) {
		trs = aTrs;
	}
}
