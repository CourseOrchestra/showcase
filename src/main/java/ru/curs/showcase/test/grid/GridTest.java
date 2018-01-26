package ru.curs.showcase.test.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.GridEvent;
import ru.curs.showcase.test.AbstractTest;

/**
 * Модульные тесты (без обращения к БД или файловой системе) грида и его
 * внутренних компонентов.
 * 
 * @author den
 * 
 */
public class GridTest extends AbstractTest {

	@Test
	public void gridEventPropsShouldMatchEventsProps() {
		Event event = new GridEvent();
		ID id1 = new ID("id1");
		ID id2 = new ID("2");
		event.setId1(id1);
		event.setId2(id2);

		GridEvent gEvent = (GridEvent) event;
		assertEquals(id1, gEvent.getRecordId());
		assertEquals(id2, gEvent.getColId());

		final String recId = "recId";
		gEvent.setRecordId(recId);
		final String colId = "colId";
		gEvent.setColId(colId);
		assertEquals(recId, event.getId1().toString());
		assertEquals(colId, event.getId2().toString());
	}

	@SuppressWarnings("unused")
	private GridEvent generateRowClickEvent(final String id) {
		GridEvent event = new GridEvent();
		event.setRecordId(id);
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setAction(generateTestActionForRefreshElements());
		return event;
	}

	@SuppressWarnings("unused")
	private GridEvent generateCellClickEvent(final String row, final String cell) {
		GridEvent event = new GridEvent();
		event.setRecordId(row);
		event.setColId(cell);
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setAction(generateTestActionForRefreshElements());
		return event;
	}

	@SuppressWarnings("unused")
	private GridEvent generateRowDblClickEvent(final String id) {
		GridEvent event = new GridEvent();
		event.setRecordId(id);
		event.setInteractionType(InteractionType.DOUBLE_CLICK);
		event.setAction(generateTestActionForRefreshElements());
		return event;
	}

	private Action generateTestActionForRefreshElements() {
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		DataPanelLink dLink = new DataPanelLink();
		action.setContext(CompositeContext.createCurrent());
		dLink.setDataPanelId(ID.CURRENT_ID);
		dLink.setTabId(ID.CURRENT_ID);
		action.setDataPanelLink(dLink);
		return action;
	}
}
