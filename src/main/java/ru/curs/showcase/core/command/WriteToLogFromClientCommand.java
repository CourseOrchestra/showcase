package ru.curs.showcase.core.command;

import org.slf4j.*;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Команда для записи в лог (и веб-консоль) сообщений из клиентской части.
 * 
 * @author den
 * 
 */
public class WriteToLogFromClientCommand extends ServiceLayerCommand<Void> {

	public static final String CLIENT_LABEL = "CLIENT";

	private String message;
	private MessageType messageType;

	public WriteToLogFromClientCommand(final CompositeContext aContext, final String aMessage,
			final MessageType aMessageType) {
		super(aContext);
		message = aMessage;
		messageType = aMessageType;
	}

	@Override
	protected void mainProc() throws Exception {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		Marker marker = MarkerFactory.getDetachedMarker(CLIENT_LABEL);
		switch (messageType) {
		case ERROR:
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				logger.error(marker, message);
			}
			break;
		case WARNING:
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
				logger.warn(marker, message);
			}
			break;
		case INFO:
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				logger.info(marker, message);
			}
			break;
		default:
			break;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String aMessage) {
		message = aMessage;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(final MessageType aMessageType) {
		messageType = aMessageType;
	}

}
