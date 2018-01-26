package ru.curs.showcase.app.api;

import javax.xml.bind.annotation.*;

/**
 * Универсальный идентификатор, основанный на строке. Переопределен метод
 * equals.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ID implements SerializableElement, CanBeCurrent, Comparable<ID>, CharSequence {

	private static final long serialVersionUID = 2453957534494801548L;

	private String string;

	public String getString() {
		return string;
	}

	public void setString(final String aString) {
		string = aString;
	}

	@Override
	public int length() {
		if (string != null) {
			return string.length();
		}
		return 0;
	}

	@Override
	public char charAt(final int aIndex) {
		if (string != null) {
			return string.charAt(aIndex);
		}
		return 0;
	}

	@Override
	public CharSequence subSequence(final int aStart, final int aEnd) {
		if (string != null) {
			return string.subSequence(aStart, aEnd);
		}
		return null;
	}

	@Override
	public int compareTo(final ID aO) {
		if (string != null) {
			return cleverCompareTo(string, aO.getString());
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : cleverHashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof ID) {
			ID other = (ID) obj;
			if (string == null) {
				if (other.string != null) {
					return false;
				}
			} else {
				return cleverEquals(string, other.string);
			}
		} else if (obj instanceof String) {
			return cleverEquals((String) obj, string);
		} else {
			return false;
		}

		return true;
	}

	private boolean cleverEquals(final String s1, final String s2) {
		if (IDSettings.getInstance().getCaseSensivity()) {
			return s1.equals(s2);
		} else {
			return s1.equalsIgnoreCase(s2);
		}
	}

	private int cleverCompareTo(final String s1, final String s2) {
		if (IDSettings.getInstance().getCaseSensivity()) {
			return s1.compareTo(s2);
		} else {
			return s1.compareToIgnoreCase(s2);
		}
	}

	private int cleverHashCode() {
		if (IDSettings.getInstance().getCaseSensivity()) {
			return string.hashCode();
		} else {
			return string.toLowerCase().hashCode();
		}
	}

	public ID() {
		super();
	}

	public ID(final String aString) {
		super();
		string = aString;
	}

	public static ID createCurrentID() {
		return new ID(CURRENT_ID);
	}

	public boolean isEmpty() {
		if (string != null) {
			return string.isEmpty();
		}
		return true;
	}

	@Override
	public String toString() {
		return getString();
	}
}
