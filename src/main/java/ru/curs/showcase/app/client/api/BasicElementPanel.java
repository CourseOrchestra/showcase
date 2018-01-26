/**
 * 
 */
package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author anlug
 * 
 *         Интерфейс панели,на которой может находится grid, web-text или chart,
 *         geomap.
 * 
 */
public interface BasicElementPanel {

	/**
	 * 
	 * Функция перерисовки элемента панели (графика, грида, вебтекста).
	 * 
	 * @param context1
	 *            контекст с которым надо перерисовать объект.
	 * @param refreshContextOnly
	 *            - признак того, что у элемента нужно вызвать функцию
	 *            обновления контекста для всех его событий.
	 */
	void reDrawPanel(final CompositeContext context1);

	/**
	 * 
	 * Функция обновления элемента панели (графика, грида, вебтекста).
	 * 
	 */
	void refreshPanel();

	/**
	 * 
	 * Функция скрывает, но не удаляет панель с графиком, гридом или web-text на
	 * закладке.
	 */
	void hidePanel();

	/**
	 * 
	 * Функция показывает (делает видимой) панель с графиком, гридом или
	 * web-text на закладке.
	 */
	void showPanel();

	/**
	 * 
	 * Функция возвращает информацию о текущем связанный с данной панелью
	 * элемент DataPanelElement.
	 * 
	 * @return - DataPanelElementInfo
	 */
	DataPanelElementInfo getElementInfo();

	/**
	 * @return Ф-ция, возвращающая панель элемента типа VerticalPanel (т.е. gwt
	 *         Widget).
	 */
	VerticalPanel getPanel();

	/**
	 * 
	 * Функция возвращает текущий связанный с данной панелью элемент
	 * DataPanelElement.
	 * 
	 * @return - DataPanelMainElement
	 */
	DataPanelElement getElement();

	CompositeContext getContext();

	/**
	 * Возвращает детализированный контекст элемента, включающий в себя
	 * настройки элемента, сделанные пользователем.
	 */
	CompositeContext getDetailedContext();

	void setNeedResetLocalContext(final boolean aNeedResetLocalContext);

	void setPartialUpdate(final boolean aPartialUpdate);

	void setCurrentLevelUpdate(final boolean aCurrentLevelUpdate);

	void setChildLevelUpdate(final boolean aChildLevelUpdate);
}
