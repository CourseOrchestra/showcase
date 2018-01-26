package ru.curs.showcase.app.api;

/**
 * Класс, содержащий динамические настройки для ID.
 * 
 * @author den
 * 
 */
public class IDSettings implements SerializableElement {

	private static final long serialVersionUID = 8467286510734723596L;

	public IDSettings() {
		super();
		reset();
	}

	private static final IDSettings INSTANCE = new IDSettings();

	private Boolean caseSensivity;

	public static IDSettings getInstance() {
		return INSTANCE;
	}

	public Boolean getCaseSensivity() {
		return caseSensivity;
	}

	public final void reset() {
		caseSensivity = false;
	}

	public void setCaseSensivity(final Boolean aCaseSensivity) {
		caseSensivity = aCaseSensivity;
	}

}
