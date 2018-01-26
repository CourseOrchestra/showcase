package ru.curs.showcase.util.xml;

/**
 * Стандартный упрощенный обработчик для случая, когда нужно обрабатывать только
 * начальные тэги.
 * 
 * @author den
 * 
 */
public abstract class StartTagSAXHandler extends SAXTagHandler {

	@Override
	protected String[] getEndTrags() {
		return new String[0];
	}

	@Override
	public Object
			handleEndTag(final String aNamespaceURI, final String aLname, final String aQname) {
		return null;
	}

	@Override
	public void handleCharacters(final char[] aArg0, final int aArg1, final int aArg2) {
		// ничего не делаем
	}

}
