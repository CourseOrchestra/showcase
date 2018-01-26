package ru.curs.showcase.app.api.common;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Command;

/**
 * Обработчик события клика мышью на команде. User: Inc Date: 18.11.2009 Time:
 * 0:31:16
 */
public class CommandClickHandler implements ClickHandler {
	/**
	 * cmd.
	 */
	private final Command cmd;

	public CommandClickHandler(final Command cmd1) {
		this.cmd = cmd1;
	}

	@Override
	public void onClick(final ClickEvent event) {
		cmd.execute();
	}
}
