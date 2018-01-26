package ru.curs.showcase.app.server;

import java.util.Date;

/**
 * 
 * Интерфейс для реализации классом, представляющим собой MBean.
 *
 * @author s.borodanev
 */

public interface ActiveSessionsMBean {

	public void setDate(Date aDate);

	public void setActiveSessions(int anActiveSessions);

	public Date getDate();

	public int getActiveSessions();

	public void extraMethod();

}
