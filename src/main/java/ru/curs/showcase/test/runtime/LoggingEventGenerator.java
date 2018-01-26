package ru.curs.showcase.test.runtime;

import java.util.Date;

import ru.curs.showcase.runtime.LogBackLoggingEventDecorator;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * Генератор тестовых событий.
 * 
 * @author den
 * 
 */
public final class LoggingEventGenerator {

	private static LoggingEvent generateTestLoggingEvent(final long timestamp) {
		LoggingEvent event = new LoggingEvent();
		event.setMessage("message");
		event.setLevel(Level.ERROR);
		event.setTimeStamp(timestamp);
		return event;
	}

	public static LogBackLoggingEventDecorator generateEvent(final long timestamp) {
		return new LogBackLoggingEventDecorator(
				LoggingEventGenerator.generateTestLoggingEvent(timestamp));
	}

	public static LogBackLoggingEventDecorator generateEvent() {
		Date date = new Date();
		return new LogBackLoggingEventDecorator(
				LoggingEventGenerator.generateTestLoggingEvent(date.getTime()));
	}

	private LoggingEventGenerator() {
		throw new UnsupportedOperationException();
	}

}
