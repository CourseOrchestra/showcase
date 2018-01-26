package ru.curs.showcase.app.api.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Привязывает кнопку к действию. User: Inc Date: 09.03.2010 Time: 18:57:25
 */
public class ActionButtonAdapter implements ActionListener {

	/**
	 * Кнопка для действия.
	 */
	private ButtonBase button;

	public ActionButtonAdapter(final Action a) {
		this(a, ActionButtonStyle.IMAGE_TEXT);
	}

	public ActionButtonAdapter(final Action a, final ActionButtonStyle style) {
		switch (style) {
		case IMAGE:
			button = new PushButton(a.getImage(), createClickHandler(a));
			break;
		case IMAGE_TEXT:
			if (a.getImage() != null) {
				button = new PushButton("", createClickHandler(a));
				button.setHTML("<nobr><img src=\"" + a.getImage().getUrl() + "\"/>&nbsp;"
						+ a.getText() + "</nobr>");
			} else {
				button = new Button(a.getText(), createClickHandler(a));
			}
			break;
		case TEXT:
			button = new Button(a.getText(), createClickHandler(a));
			break;
		default:
			break;
		}
		a.addListener(this);
		enabledChangedLocal(a);
	}

	/**
	 * Возвращает кнопку.
	 * 
	 * @return - кнопка.
	 */
	public ButtonBase getButton() {
		return button;
	}

	private void enabledChangedLocal(final Action a) {
		button.setEnabled(a.isEnabled());
	}

	@Override
	public void enabledChanged(final Action a) {
		enabledChangedLocal(a);
	}

	private ClickHandler createClickHandler(final Action a) {
		return new ActionClickHandler(a);
	}
}
