package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.core.command.WriteToLogFromClientCommand;
import ru.curs.showcase.core.event.ExecServerActivityCommand;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.core.jython.JythonQuery;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Все, связанное с тестированием логгеров. В том числе, код специфичный для
 * конкретного логгера.
 * 
 * @author den
 * 
 */
public class LoggerTest extends AbstractTestWithDefaultUserData {

	private static final String LEVEL_LOG_EVENT_PROP = "level";

	@Test
	public void loggerReconfigurationShouldWorkWithWhenUserDataConfNotExists() {
		try {
			AppInfoSingleton.getAppInfo().setUserDataLogConfFile("someFakeFileName.xml");
			AppInitializer.setupUserdataLogging();
		} finally {
			AppInfoSingleton.getAppInfo().setUserDataLogConfFile("logback.xml");
		}
	}

	private void testBaseLastLogEventQueue(final Collection<LoggingEventDecorator> lleq)
			throws InterruptedException {
		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		final int eventCount = 405;
		for (int i = 0; i < eventCount; i++) {
			Thread.sleep(1);
			lleq.add(LoggingEventGenerator.generateEvent());
		}

		assertEquals(LastLogEvents.getMaxRecords(), lleq.size());
	}

	@Test
	public void testLastLogEventQueue() throws InterruptedException {
		testBaseLastLogEventQueue(new LastLogEvents());
	}

	@Test
	public void testLastLogEventQueueDuplicates() {
		LastLogEvents lle = new LastLogEvents();
		LoggingEventDecorator event = LoggingEventGenerator.generateEvent();
		long timestamp = event.getTimeStamp();
		lle.add(event);
		event = LoggingEventGenerator.generateEvent(timestamp);
		lle.add(event);
		event = LoggingEventGenerator.generateEvent(timestamp);
		lle.add(event);

		final int itemsCount = 3;
		assertEquals(itemsCount, lle.size());
	}

	@Test
	public void testLastLogEventQueueInRuntime() throws InterruptedException {
		testBaseLastLogEventQueue(AppInfoSingleton.getAppInfo().getLastLogEvents());
	}

	@Test
	public void testLoggingEventDecorator() {
		LoggingEventDecorator decorator = LoggingEventGenerator.generateEvent();

		decorator.setUserdata(TEST1_USERDATA);
		assertTrue(decorator.isSatisfied(ExchangeConstants.URL_PARAM_USERDATA, "test1"));
		assertFalse(decorator.isSatisfied(ExchangeConstants.URL_PARAM_USERDATA, "default"));

		decorator.setUserName("master");
		assertTrue(decorator.isSatisfied("userName", "master"));
		assertFalse(decorator.isSatisfied("userName", "master1"));

		decorator.setCommandName(XFormDownloadCommand.class.getSimpleName());
		assertTrue(decorator
				.isSatisfied("commandName", XFormDownloadCommand.class.getSimpleName()));
		assertFalse(decorator.isSatisfied("commandName", XFormUploadCommand.class.getSimpleName()));

		decorator.setRequestId("1");
		assertTrue(decorator.isSatisfied("requestId", "1"));
		assertFalse(decorator.isSatisfied("requestId", "2"));

		assertTrue(decorator.isSatisfied(LEVEL_LOG_EVENT_PROP, "error"));
		assertTrue(decorator.isSatisfied(LEVEL_LOG_EVENT_PROP, "ERROR"));
		assertFalse(decorator.isSatisfied(LEVEL_LOG_EVENT_PROP, "warn"));

		assertTrue(decorator.isSatisfied("requestid", "1111"));
	}

	@Test
	public void testGetLastLogEventsWithFilter() {
		AppInfoSingleton.getAppInfo().getLastLogEvents().clear();
		LoggingEventDecorator decorator = LoggingEventGenerator.generateEvent();
		decorator.setUserdata(TEST1_USERDATA);
		AppInfoSingleton.getAppInfo().addLogEvent(decorator);

		decorator = LoggingEventGenerator.generateEvent();
		decorator.setUserdata("default");
		AppInfoSingleton.getAppInfo().addLogEvent(decorator);

		Map<String, List<String>> params = new TreeMap<>();
		params.put(ExchangeConstants.URL_PARAM_USERDATA, Arrays.asList(TEST1_USERDATA));
		Collection<LoggingEventDecorator> selected =
			AppInfoSingleton.getAppInfo().getLastLogEvents(params);

		assertEquals(1, selected.size());
		assertEquals(TEST1_USERDATA, selected.iterator().next().getUserdata());
	}

	@Test
	public void testGetLastLogEventsWithNullFilter() {
		AppInfoSingleton.getAppInfo().getLastLogEvents().clear();
		LoggingEventDecorator decorator = LoggingEventGenerator.generateEvent();
		decorator.setUserdata(TEST1_USERDATA);
		AppInfoSingleton.getAppInfo().addLogEvent(decorator);

		Map<String, List<String>> params = null;
		Collection<LoggingEventDecorator> selected =
			AppInfoSingleton.getAppInfo().getLastLogEvents(params);

		assertEquals(1, selected.size());
		assertEquals(TEST1_USERDATA, selected.iterator().next().getUserdata());
	}

	@Test
	@Ignore
	// !!!
	public void testJythonMessages() {
		Action action = generateActionWithServerAactivity("TestWriteToLog.py");
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();
		int jythonEvents = 0;
		String expected1 = MAIN_CONDITION + " из jython";
		String expected2 = "из jython 2";
		for (LoggingEventDecorator event : AppInfoSingleton.getAppInfo().getLastLogEvents()) {
			if (JythonQuery.JYTHON_MARKER.equals(event.getProcess())) {
				if (expected1.equals(event.getMessage()) || expected2.equals(event.getMessage())) {
					assertEquals("INFO", event.getLevel());
					assertEquals(ExchangeConstants.DEFAULT_USERDATA, event.getUserdata());
					assertEquals(ExecServerActivityCommand.class.getSimpleName(),
							event.getCommandName());
					jythonEvents++;
				}
			}
		}
		assertEquals(jythonEvents, 2);
	}

	@Test
	public void directionObjectShouldGenerateRightSL4JMarker() {
		assertNotNull(HandlingDirection.OUTPUT.getMarker());
		assertNotNull(HandlingDirection.INPUT.getMarker());
		assertEquals("OUTPUT", HandlingDirection.OUTPUT.getMarker().getName());
		assertEquals("INPUT", HandlingDirection.INPUT.getMarker().getName());
	}

	@Test
	public void writeToLogShouldAddMessageToWebConsole() {
		AppInfoSingleton.getAppInfo().getLastLogEvents().clear();
		DataServiceImpl servlet = new DataServiceImpl();
		servlet.writeToLog(new CompositeContext(), "message01", MessageType.WARNING);

		Map<String, List<String>> params = null;
		Collection<LoggingEventDecorator> selected =
			AppInfoSingleton.getAppInfo().getLastLogEvents(params);

		assertEquals(2, selected.size());
		Iterator<LoggingEventDecorator> iterator = selected.iterator();
		LoggingEventDecorator event = null;
		while (iterator.hasNext()) {
			event = iterator.next();
			if ("WARN".equals(event.getLevel())) {
				assertEquals("message01", event.getMessage());
				assertEquals(WriteToLogFromClientCommand.CLIENT_LABEL, event.getProcess());
				assertNull(event.getDirection());
				assertEquals(WriteToLogFromClientCommand.class.getSimpleName(),
						event.getCommandName());
				return;
			}
		}
		fail();
	}
}
