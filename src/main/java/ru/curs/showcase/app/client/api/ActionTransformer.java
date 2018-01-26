package ru.curs.showcase.app.client.api;

import java.util.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;

import com.google.gwt.core.client.GWT;

/**
 * Класс API, служащий для корректного получения текущего контекста на основе
 * предыдущих контекстов в клиентской части. В данный класс вынесены все
 * правила, по которым происходит смена контекста.
 * 
 * @author den
 * 
 */
public class ActionTransformer {

	/**
	 * Текущий Action. По нему отрабатываются клики внутри вкладок.
	 */
	private Action currentAction;

	/**
	 * Идентификатор элемента, из которого было вызвано действие.
	 */
	private ID currentElementId;

	/**
	 * Последний Action, пришедший из навигатора. По нему отрабатываются клики в
	 * навигаторе и клики по вкладками панели.
	 */
	private Action navigatorAction;

	public Action getCurrentAction() {
		return currentAction;
	}

	/**
	 * Метод установки нового текущего контекста, обновляющий поля Action так,
	 * чтобы корректно установить current значения.
	 * 
	 * @param newAction
	 *            - новый Action.
	 */
	public void
			setCurrentActionFromElement(final Action newAction, final DataPanelElement element) {
		Action clone = newAction.gwtClone();
		clone.actualizeBy(navigatorAction);
		leaveOldFilterForInsideTabAction(clone);
		setupUserdata(clone);
		currentAction = clone;
		currentElementId = element.getId();
	}

	private void setupUserdata(final Action action) {
		Map<String, List<String>> params = null;
		if (GWT.isClient()) {
			params = com.google.gwt.user.client.Window.Location.getParameterMap();
		}
		action.setSessionContext(params);
	}

	public Action getNavigatorAction() {
		return navigatorAction;
	}

	/**
	 * Метод установки нового контекста навигатора взамен старого при клике по
	 * вкладке, обновляющий поля Action так, чтобы корректно установить current
	 * значения.
	 * 
	 * @param newAction
	 *            - новый Action.
	 */
	public void setNavigatorActionFromTab(final Action newAction) {
		navigatorAction.setDataPanelActionType(DataPanelActionType.REFRESH_TAB);
		determineNavigatorKeepUserSettingsWhenTabSwitching(newAction);
		if (!newAction.getDataPanelLink().getTabId()
				.equals(navigatorAction.getDataPanelLink().getTabId())) {
			navigatorAction.getDataPanelLink().setTabId(newAction.getDataPanelLink().getTabId());
			navigatorAction.getDataPanelLink().setFirstOrCurrentTab(false);
			if (currentAction != null) {
				currentAction.filterBy(null);
			}
		}
	}

	/**
	 * Метод установки нового контекста навигатора взамен старого при клике в
	 * навигаторе, обновляющий поля Action так, чтобы корректно установить
	 * current значения.
	 * 
	 * @param newAction
	 *            - новый Action.
	 */
	public void setNavigatorAction(final Action newAction) {
		Action clone = newAction.gwtClone();
		if (navigatorAction != null) {
			clone.actualizeBy(navigatorAction);
		}
		setupUserdata(clone);
		navigatorAction = clone;
		currentAction = null;
	}

	private void determineNavigatorKeepUserSettingsWhenTabSwitching(final Action newAction) {
		if (!navigatorAction.getDataPanelLink().getTabId()
				.equals(newAction.getDataPanelLink().getTabId())) {
			navigatorAction.setKeepUserSettingsForAll(false);
		}
	}

	private void leaveOldFilterForInsideTabAction(final Action clone) {
		if ((clone.getDataPanelActionType() == DataPanelActionType.REFRESH_TAB)
				|| (clone.getDataPanelActionType() == DataPanelActionType.RELOAD_ELEMENTS)) {
			if (!clone.isFiltered()) {
				if ((currentAction != null) && (currentAction.getContext() != null)) {
					clone.filterBy(currentAction.getContext().getFilter());
				}
			}
		}
	}

	public ID getCurrentElementId() {
		return currentElementId;
	}
}