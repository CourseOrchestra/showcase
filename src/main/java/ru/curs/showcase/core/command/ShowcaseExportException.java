package ru.curs.showcase.core.command;

import javax.xml.ws.WebFault;

/**
 * Исключение, передаваемое во внешние по отношению к Showcase приложения.
 * 
 * @author den
 * 
 */
@WebFault(name = "ShowcaseExportException")
public final class ShowcaseExportException extends Exception {

	private static final long serialVersionUID = -454477381667577122L;

	public ShowcaseExportException(final Throwable e) {
		super(e.getMessage());
	}

	public ShowcaseExportException(final String mes) {
		super(mes);
	}

}
