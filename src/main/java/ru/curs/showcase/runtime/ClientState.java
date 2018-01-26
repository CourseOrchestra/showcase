package ru.curs.showcase.runtime;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Состояние сессии.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ClientState {

	private ServerState serverState;

	private BrowserType browserType;

	private String browserVersion;

	public ServerState getServerState() {
		return serverState;
	}

	public ClientState() {
		super();
	}

	public ClientState(final ServerState aServerState, final String userAgent) {
		super();
		serverState = aServerState;
		browserType = BrowserType.detect(userAgent);
		browserVersion = BrowserType.detectVersion(userAgent);
	}

	public void setServerState(final ServerState aServerState) {
		serverState = aServerState;
	}

	public BrowserType getBrowserType() {
		return browserType;
	}

	public void setBrowserType(final BrowserType aBrowserType) {
		browserType = aBrowserType;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(final String aBrowserVersion) {
		browserVersion = aBrowserVersion;
	}
}
