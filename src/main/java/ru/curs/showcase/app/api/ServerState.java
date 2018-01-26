package ru.curs.showcase.app.api;

import javax.xml.bind.annotation.*;

/**
 * Класс текущего состояния приложения.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ServerState implements SerializableElement {

	private static final long serialVersionUID = -6409374109173355019L;

	/**
	 * Версия приложения.
	 */
	private String appVersion;

	/**
	 * Информация о текущем пользователе (sid, телефон, имя, e-mail).
	 */
	private UserInfo userInfo;

	/**
	 * Признак того, что это собственный пользователь приложения. Альтернатива -
	 * пользователь из AuthServer.
	 */
	private Boolean isNativeUser;

	/**
	 * Признак того, что это пользователь ESIA.
	 */
	private Boolean isESIAUser = false;

	/**
	 * Переменная типа String, содержащая в себе значение-ссылку, осуществляющую
	 * выход из ЕСИА.
	 */
	private String esiaLogoutURL = null;

	/**
	 * Версия контейнера сервлетов.
	 */
	private String servletContainerVersion;

	/**
	 * Версия Java, которая используется на сервере.
	 */
	private String javaVersion;

	/**
	 * Время генерации данного объекта на сервере.
	 */
	private String serverTime;

	/**
	 * Версия SQL сервера.
	 */
	private String sqlVersion;

	private String gwtVersion;

	private String dojoVersion;

	private Boolean caseSensivityIDs;

	/**
	 * SesionContext на момент старта приложения (загрузки главной страницы).
	 */
	private String startingAppSessionContext;

	/**
	 * Признак того, что разрешена запись в веб-консоль и лог сообщений из
	 * клиента. Отключить данную опцию имеет смысл из соображений
	 * производительности.
	 */
	private Boolean enableClientLog;

	/**
	 * Id сессии, который используется в сервлете ShowcaseLogoutServlet, для
	 * правильного разлогинивания при истечении таймаута сессии.
	 */
	private String sesId = "";

	/**
	 * Признак того, что гриды нужно грузить при загрузке приложения.
	 */
	private boolean preloadGrids = false;

	/**
	 * Настройки ширины dragger-а навигатора.
	 */
	private Integer pageSplitterWidth;

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(final String aAppVersion) {
		appVersion = aAppVersion;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(final UserInfo aUserInfo) {
		userInfo = aUserInfo;
	}

	public Boolean getIsNativeUser() {
		return isNativeUser;
	}

	public void setIsNativeUser(final Boolean aIsNativeUser) {
		isNativeUser = aIsNativeUser;
	}

	public String getServletContainerVersion() {
		return servletContainerVersion;
	}

	public void setServletContainerVersion(final String aServletContainerVersion) {
		servletContainerVersion = aServletContainerVersion;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(final String aJavaVersion) {
		javaVersion = aJavaVersion;
	}

	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(final String aServerTime) {
		serverTime = aServerTime;
	}

	public String getSqlVersion() {
		return sqlVersion;
	}

	public void setSqlVersion(final String aSqlVersion) {
		sqlVersion = aSqlVersion;
	}

	public String getGwtVersion() {
		return gwtVersion;
	}

	public void setGwtVersion(final String aGwtVersion) {
		gwtVersion = aGwtVersion;
	}

	public String getDojoVersion() {
		return dojoVersion;
	}

	public void setDojoVersion(final String aDojoVersion) {
		dojoVersion = aDojoVersion;
	}

	public Boolean getCaseSensivityIDs() {
		return caseSensivityIDs;
	}

	public void setCaseSensivityIDs(final Boolean aCaseSensivityIDs) {
		caseSensivityIDs = aCaseSensivityIDs;
	}

	public Boolean getEnableClientLog() {
		return enableClientLog;
	}

	public void setEnableClientLog(final Boolean aEnableClientLog) {
		enableClientLog = aEnableClientLog;
	}

	public String getStartingAppSessionContext() {
		return startingAppSessionContext;
	}

	public void setStartingAppSessionContext(final String astartingAppSessionContext) {
		this.startingAppSessionContext = astartingAppSessionContext;
	}

	public String getSesId() {
		return sesId;
	}

	public void setSesId(final String aSesId) {
		this.sesId = aSesId;
	}

	public boolean isPreloadGrids() {
		return preloadGrids;
	}

	public void setPreloadGrids(final boolean aPreloadGrids) {
		preloadGrids = aPreloadGrids;
	}

	public Boolean getIsESIAUser() {
		return isESIAUser;
	}

	public void setIsESIAUser(final Boolean aIsESIAUser) {
		isESIAUser = aIsESIAUser;
	}

	public String getEsiaLogoutURL() {
		return esiaLogoutURL;
	}

	public void setEsiaLogoutURL(final String aEsiaLogoutURL) {
		esiaLogoutURL = aEsiaLogoutURL;
	}

	public Integer getPageSplitterWidth() {
		return pageSplitterWidth;
	}

	public void setPageSplitterWidth(final Integer aPageSplitterWidth) {
		pageSplitterWidth = aPageSplitterWidth;
	}
}
