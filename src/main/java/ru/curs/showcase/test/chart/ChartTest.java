package ru.curs.showcase.test.chart;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.api.event.Event;
import ru.curs.showcase.test.AbstractTest;

/**
 * Класс для модульных тестов в узком смысле этого слова графика и связанных с
 * ним классов.
 * 
 * @author den
 * 
 */
public class ChartTest extends AbstractTest {

	@Test
	public void testChartCreate() {
		Chart chart = new Chart();

		assertTrue(chart.getHeader().isEmpty());
		assertTrue(chart.getFooter().isEmpty());
	}

	@Test
	public void testChartEvent() {
		Event event = new ChartEvent();
		ChartEvent cEvent = (ChartEvent) event;
		ID id1 = new ID("id1");
		event.setId1(id1);

		assertEquals(id1, cEvent.getSeriesId());
		assertNull(cEvent.getX());

		ID id2 = new ID("2");
		event.setId2(id2);
		assertEquals(Integer.valueOf(id2.toString()), cEvent.getX());

		cEvent.setX(1);
		final String seriesId = "seriesId";
		cEvent.setSeriesId(seriesId);
		assertEquals("1", event.getId2().toString());
		assertEquals(seriesId, event.getId1().toString());
	}

}
