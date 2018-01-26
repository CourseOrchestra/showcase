package ru.curs.showcase.core;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * Интерфейс для поиска и установки вкладки инф. панели у действия при неполных
 * указанных данных.
 * 
 * @author den
 * 
 */
public abstract class ActionTabFinder extends GeneralXMLHelper {
	/**
	 * Часть сообщения об ошибке, передаваемая в случае неверного номера
	 * вкладки.
	 */
	private static final String ERROR_MES = "Вкладка %s в инф. панели %s не существует";
	/**
	 * Признак того, что нужно искать первую вкладку при первом открытии панели
	 * и оставаться на текущей при последующих.
	 */
	private static final String FIRST_OR_CURRENT_VALUE = "firstOrCurrent";

	/**
	 * Функция поиска вкладки для действия.
	 * 
	 * @param tabValue
	 *            - указатель на вкладку.
	 * @return - реальный идентификатор вкладки.
	 * @param link
	 *            - ссылка на панель.
	 */
	public String findTabForAction(final CompositeContext context, final DataPanelLink link,
			final String tabValue) {
		if (tabValue == null) {
			return getFirstTabId(context, link);
		}

		if (tabValue.equalsIgnoreCase(FIRST_OR_CURRENT_VALUE)) {
			link.setFirstOrCurrentTab(true);
			return getFirstTabId(context, link);
		} else {
			if (!tabValue.equalsIgnoreCase(CanBeCurrent.CURRENT_ID)) {
				if (!ID.createCurrentID().equals(link.getDataPanelId())) {
					checkForExists(context, link, tabValue);
				}
			}
			return tabValue;
		}
	}

	private void checkForExists(final CompositeContext context, final DataPanelLink link,
			final String tabValue) {
		if (!tabExists(context, link, tabValue)) {
			throw new IncorrectElementException(String.format(ERROR_MES, tabValue,
					link.getDataPanelId()));
		}
	}

	public abstract String getFirstTabId(final CompositeContext context, final DataPanelLink link);

	public abstract boolean tabExists(final CompositeContext context, final DataPanelLink link,
			final String tabValue);
}
