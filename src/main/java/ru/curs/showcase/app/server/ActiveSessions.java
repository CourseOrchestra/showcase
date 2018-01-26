package ru.curs.showcase.app.server;

import java.util.*;

/**
 * Класс, представляющий собой MBean.
 * 
 * @author s.borodanev
 */

public class ActiveSessions implements ActiveSessionsMBean {

	private Date date = null;
	private int activeSessions = 0;

	public ActiveSessions() {
		Timer updateTimer = new Timer(true);
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				date = new Date();
				activeSessions = (int) AppAndSessionEventsListener.getActiveSessions();
			}
		}, 0, 10 * 1000);
	}

	@Override
	public void setDate(final Date aDate) {
		this.date = aDate;
	}

	@Override
	public void setActiveSessions(final int anActiveSessions) {
		this.activeSessions = anActiveSessions;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public int getActiveSessions() {
		return activeSessions;
	}

	@Override
	public void extraMethod() {
		// TODO Auto-generated method stub
	}

}
