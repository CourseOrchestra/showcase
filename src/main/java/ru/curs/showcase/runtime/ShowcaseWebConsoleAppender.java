package ru.curs.showcase.runtime;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Наш обработчик событий для работы веб-консоли.
 * 
 * @author den
 * 
 */
public class ShowcaseWebConsoleAppender extends AppenderBase<ILoggingEvent> {

	@Override
	protected void append(final ILoggingEvent event) {
		CommandContext commandContext = new CommandContext();
		commandContext.fromMDC();
		LogBackLoggingEventDecorator eventDecorator = new LogBackLoggingEventDecorator(event, commandContext);
		AppInfoSingleton.getAppInfo().addLogEvent(eventDecorator);
	}

}