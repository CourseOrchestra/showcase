package ru.curs.showcase.app.api.common;

import com.google.gwt.user.client.ui.Image;

/**
 * Пустое действие, служащее разделителем между действиями в меню. User: Inc
 * Date: 09.03.2010 Time: 17:27:01
 */
class ActionSeparator implements Action {
	@Override
	public String getText() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public void addListener(final ActionListener listener) {
	}

	@Override
	public void execute() {
	}
}
