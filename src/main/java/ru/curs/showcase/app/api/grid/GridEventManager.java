package ru.curs.showcase.app.api.grid;

import java.util.List;

import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.*;

/**
 * Менеджер событий для грида.
 * 
 */
public class GridEventManager extends EventManager<GridEvent> {

	private static final long serialVersionUID = -4188170257316586043L;

	/**
	 * Функция возвращает нужный обработчик события по переданным ей координатам
	 * ячейки и типу взаимодействия.
	 * 
	 * @param rowId
	 *            - идентификатор строки.
	 * @param colId
	 *            - идентификатор строки.
	 * @param interactionType
	 *            - тип взаимодействия.
	 * @return - событие или NULL.
	 */
	public List<GridEvent> getEventForCell(final String rowId, final String colId,
			final InteractionType interactionType) {
		return getEventByIds(rowId, colId, interactionType);
	}

	/**
	 * Возвращает действие для отрисовки зависимых элементов, содержащее
	 * фильтрацию по всем выделенным в гриде записям.
	 * 
	 * @param selection
	 *            - информация о выделенных записях.
	 * @return - действие.
	 */
	public Action getSelectionActionForDependentElements(final List<String> selectedRecordIds) {
		Action result = prepareFilterAction(selectedRecordIds);
		fillFilterActionBySelection(selectedRecordIds, result);
		finishFilterAction(result);
		return result;
	}

	private void finishFilterAction(final Action result) {
		for (DataPanelElementLink newLink : result.getDataPanelLink().getElementLinks()) {
			newLink.getContext().finishFilter();
			result.markFiltered(newLink.getContext().getFilter());
		}
	}

	private void fillFilterActionBySelection(final List<String> selectedRecordIds,
			final Action result) {
		for (String recId : selectedRecordIds) {
			List<GridEvent> events = getEventForCell(recId, null, InteractionType.SELECTION);
			if (!events.isEmpty()) {
				for (DataPanelElementLink curLink : events.get(0).getAction().getDataPanelLink()
						.getElementLinks()) {
					DataPanelElementLink resLink =
						result.getDataPanelLink().getElementLinkById(curLink.getId());
					resLink.getContext().addFilterLine(curLink.getContext());
				}
			}
		}
	}

	private Action prepareFilterAction(final List<String> selectedRecordIds) {

		// Action result = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		// for (Event event : getEvents()) {
		// if (event.getInteractionType() == InteractionType.SELECTION) {
		// for (DataPanelElementLink curLink :
		// event.getAction().getDataPanelLink()
		// .getElementLinks()) {
		// DataPanelElementLink newLink = curLink.gwtClone();
		// newLink.getContext().setAdditional(null);
		// newLink.getContext().setFilter("");
		// result.getDataPanelLink().getElementLinks().add(newLink);
		// }
		// }
		// }
		// return result;

		Action result = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		if ((selectedRecordIds != null) && (!selectedRecordIds.isEmpty())) {
			String recId = selectedRecordIds.get(0);
			List<GridEvent> events = getEventForCell(recId, null, InteractionType.SELECTION);
			for (Event event : events) {
				for (DataPanelElementLink curLink : event.getAction().getDataPanelLink()
						.getElementLinks()) {
					DataPanelElementLink newLink = curLink.gwtClone();
					newLink.getContext().setAdditional(null);
					newLink.getContext().setFilter("");
					result.getDataPanelLink().getElementLinks().add(newLink);
				}
			}
		}
		return result;

	}
}