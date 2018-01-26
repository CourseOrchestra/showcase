package ru.curs.showcase.app.api.selector;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Дополнительная информация.
 */
public class SelectorAdditionalData implements SerializableElement {
	private static final long serialVersionUID = -3561342065265034932L;

	private SerializableElement context;
	private SerializableElement elInfo;

	public SerializableElement getContext() {
		return context;
	}

	public void setContext(final SerializableElement aContext) {
		context = aContext;
	}

	public SerializableElement getElementInfo() {
		return elInfo;
	}

	public void setElementInfo(final SerializableElement aElInfo) {
		elInfo = aElInfo;
	}

}
