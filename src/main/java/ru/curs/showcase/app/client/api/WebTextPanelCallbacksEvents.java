package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.*;

/**
 * 
 */

/**
 * @author anlug Класс реализующий функции обратного вызова из html текста
 *         (WebText).
 * 
 */
public final class WebTextPanelCallbacksEvents {

	private WebTextPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие одинарного клика на графике (на Chart).
	 * 
	 * @param webTextId
	 *            - Id элемента WebText.
	 * @param linkId
	 *            - идентификатор ссылки на которой был совершен клик
	 */
	public static void webTextPanelClick(final String webTextId, final String linkId,
			final String overridenValue, final String replaseWhat, final String id) {

		WebText wt = ((WebTextPanel) ActionExecuter.getElementPanelById(webTextId)).getWebText();

		ActionFieldType actionFieldType = ActionFieldType.ADD_CONTEXT;
		if (replaseWhat != null) {
			actionFieldType = ActionFieldType.valueOf(replaseWhat.toUpperCase());
		}

		List<HTMLEvent> events = wt.getEventManager().getEventForLink(linkId);
		for (HTMLEvent hev : events) {
			Action ac = hev.getAction().gwtClone();
			if (overridenValue != null) {
				switch (actionFieldType) {
				case ADD_CONTEXT:
					ac.setAdditionalContext(overridenValue);
					break;
				case MAIN_CONTEXT:
					ac.setMainContext(overridenValue);
					break;
				case FILTER_CONTEXT:
					ac.filterBy(overridenValue);
					break;
				case ELEMENT_ID:
					if (ac.getDataPanelActionType() != DataPanelActionType.DO_NOTHING) {
						String elID = id;
						DataPanelElementLink link = null;
						if (elID != null) {
							link = ac.getDataPanelLink().getElementLinkById(elID);
						} else {
							if (ac.getDataPanelLink().getElementLinks().size() > 0) {
								link = ac.getDataPanelLink().getElementLinks().get(0);
							}
						}
						if (link != null) {
							link.setId(overridenValue);
						} else {
							MessageBox.showSimpleMessage("Ошибка",
									"Элемент действия для замены ID неверно определен (ID = "
											+ elID + ")");
						}
					}
					break;
				default:
					break;
				}
			}
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, wt);
			ActionExecuter.execAction();
		}

	}
}
