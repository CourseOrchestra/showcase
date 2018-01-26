package ru.curs.showcase.app.api.common;

import com.google.gwt.event.dom.client.*;

/**
 * Обработчик события клика мышью на визуальном компоненте, связанном с
 * действием. Если действие является наследником AbstractAction, то у него
 * устанавливается NativeEvent перед вызовом логики действия.
 */
public class ActionClickHandler implements ClickHandler {
	/**
	 * a.
	 */
	private final Action a;

	public ActionClickHandler(final Action a1) {
		this.a = a1;
	}

	@Override
	public void onClick(final ClickEvent event) {
		if (a instanceof AbstractAction) {
			((AbstractAction) a).setNativeEvent(event.getNativeEvent());
		}
		a.execute();
	}
}
