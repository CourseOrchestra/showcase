package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.Event;
import ru.curs.showcase.app.api.html.HTMLEvent;
import ru.curs.showcase.test.AbstractTest;

/**
 * Базовые тесты, общие для HTML элементов инф. панели.
 * 
 * @author den
 * 
 */
public class HTMLBaseTest extends AbstractTest {

	@Test
	public void testHTMLEvent() {
		Event event = new HTMLEvent();
		ID id1 = new ID("id1");
		event.setId1(id1);

		HTMLEvent hEvent = (HTMLEvent) event;
		assertEquals(id1, hEvent.getLinkId());

		final String linkId = "linkId";
		hEvent.setLinkId(linkId);
		assertEquals(linkId, event.getId1().toString());
		assertNull(event.getId2());
	}
}
