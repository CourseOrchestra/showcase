package ru.curs.showcase.util.alfresco;

/**
 * Класс для результата логина в Alfresco.
 * 
 */
public class AlfrescoLoginResult extends AlfrescoBaseResult {

	private String ticket = null;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(final String aTicket) {
		ticket = aTicket;
	}

}
