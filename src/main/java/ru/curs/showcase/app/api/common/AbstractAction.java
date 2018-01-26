package ru.curs.showcase.app.api.common;

import java.util.*;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Image;

/**
 * Базовый класс для действий в системе (паттерн Action).
 */
public abstract class AbstractAction implements Action {

	/**
	 * text.
	 */
	private final String text;
	/**
	 * enabled.
	 */
	private boolean enabled = true;

	/**
	 * image.
	 */
	private Image image;

	/**
	 * listeners.
	 */
	private List<ActionListener> listeners;

	/**
	 * nativeEvent.
	 */
	private NativeEvent nativeEvent;

	protected AbstractAction(final String text1) {
		this.text = text1;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Image getImage() {
		return image;
	}

	/**
	 * Устанавливает иконку команды.
	 * 
	 * @param image1
	 *            Новая иконка.
	 */
	public void setImage(final Image image1) {
		this.image = image1;
	}

	/**
	 * Устанавливает доступность команды.
	 * 
	 * @param enabled1
	 *            Будет ли команда доступна (активирована)
	 */
	public void setEnabled(final boolean enabled1) {
		if (this.enabled == enabled1) {
			return;
		}
		this.enabled = enabled1;
		if (listeners != null) {
			for (ActionListener l : listeners) {
				l.enabledChanged(this);
			}
		}
	}

	@Override
	public void addListener(final ActionListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ActionListener>();
		}
		listeners.add(listener);
	}

	@Override
	public void execute() {
		if (!enabled) {
			return;
		}
		perform();
	}

	/**
	 * Метод, вызываемый при выполнении действия.
	 */
	protected abstract void perform();

	/**
	 * Возвращает событие.
	 * 
	 * @return nativeEvent
	 */
	protected NativeEvent getNativeEvent() {
		return nativeEvent;
	}

	void setNativeEvent(final NativeEvent nativeEvent1) {
		this.nativeEvent = nativeEvent1;
	}
}
