package ru.curs.showcase.runtime;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.ehcache.*;
import org.ehcache.config.Configuration;
import org.ehcache.config.xml.XmlConfiguration;
import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.xml.sax.SAXException;

import ru.curs.celesta.Celesta;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.server.GeneralAppProperties;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.ServerLogicError;

/**
 * Синглетон для хранения информации о сессиях приложения и глобальной
 * информации в приложении. Хранить данные пользовательских сессий на сервере
 * нежелательно - это усложняет приложение - но в данном случае это вынужденная
 * мера.
 * 
 * @author den
 * 
 */
public final class AppInfoSingleton {

	public static final String GRID_STATE_CACHE = "gridStateCache";
	public static final String LYRA_GRID_STATE_CACHE = "lyraGridStateCache";

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoSingleton.class);

	/** Список userdata. */
	private final Map<String, UserData> userdatas = new HashMap<String, UserData>();

	/**
	 * Синглетон.
	 */
	private static final AppInfoSingleton INSTANCE = new AppInfoSingleton();

	/**
	 * Карта пользовательских сессий.
	 */
	private final Map<String, SessionInfo> sessionInfoMap =
		Collections.synchronizedMap(new HashMap<String, SessionInfo>());

	private String sesid = "";

	private String oldSesid = "";

	/**
	 * Celesta instance для работы Showcase c источником данных Челеста.
	 */
	private Celesta celesta = null;

	public Celesta getCelestaInstance() {
		return celesta;
	}

	public void setCelestaInstance(final Celesta acelesta) {
		this.celesta = acelesta;
	}

	private List<String> additionalParametersList = new ArrayList<String>();

	private final SortedSet<String> executedProc =
		Collections.synchronizedSortedSet(new TreeSet<String>());
	/**
	 * Карта, используемая для устранения проблемы того, что имя пользователя
	 * остаётся старым при повторном входе при кросс-доменной аутентификации,
	 * вследствие неуничтожения сессии данного пользователя.
	 */
	private final Map<String, Authentication> sessionAuthenticationMapForCrossDomainEntrance =
		Collections.synchronizedMap(new HashMap<String, Authentication>());

	/**
	 * Идентификатор userdata в текущем запросе.
	 */
	private final ThreadLocal<String> curUserDataId = new ThreadLocal<String>();

	/**
	 * Версия контейнера сервлетов.
	 */
	private String servletContainerVersion;

	private SortedSet<LoggingEventDecorator> lastLogEvents;

	private String webAppPath;

	private String userdataRoot;

	/**
	 * Сообщение, выдаваемое в случае возникновения исключения при старте
	 * Showcase.
	 */
	private String showcaseAppOnStartMessage = "";

	private String userDataLogConfFile = "logback.xml";

	private boolean enableLogLevelInfo = true;
	private boolean enableLogLevelWarning = true;
	private boolean enableLogLevelError = true;

	/**
	 * Переменная, которая содержит в себе информацию о том, будет ли хоть в
	 * одной userdata приложения использоваться сторонняя компонента Activiti.
	 * По умолчанию она false. Если переменная true, то в памяти серверной части
	 * Showcase будет создаваться экземпляр движка Activiti, который будет
	 * передаваться или будет доступен в celesta скриптах. Свойства берется
	 * иззначения параметра activiti.enable главного файла общего файла
	 * app.propertes.
	 */
	private boolean enableActiviti = false;

	/**
	 * Переменная, которая сожержит в себе движок Activiti (сторонней
	 * компоненты) настроенный на текущую базу данных userdata.
	 */
	private ProcessEngine activitiProcessEngine = null;

	/**
	 * Переменная, которая содержит в себе значение: false - если в процессе
	 * запуска прилдожений не произошла ошибка с инициализацией celesta и
	 * calesta не инициализировалась. В противном случае переменная равна true.
	 */
	private Boolean isCelestaInitialized = false;

	/**
	 * Переменная, которая содержит в себе exception, который произошел при
	 * инициализации celesta в процессе запуска прилдожения Showcase. Если в
	 * процессе запуска произошла ошибка инициализации celesta ( переменная
	 * isCelestaInitialized = false), то необходимо при запуске на сервере
	 * Showcase jython скриптов celesta (когда пользоватлеь запрашивает инфу от
	 * челесты) на клиента Showcase возвращать ошибку, что челеста не была
	 * инициализирована и причину этого, хранящуюся в этом exception.
	 */
	private Exception celestainitializationException = null;

	// private final CacheManager cacheManager = new CacheManager();
	private final CacheManager cacheManager = generateCacheManager();

	/**
	 * Словарь (карта) соответствия событий Activiti и Celesta-скриптов.
	 */
	private Map<ActivitiEventType, List<String>> activitiEventScriptDictionary =
		new HashMap<ActivitiEventType, List<String>>();

	/**
	 * Карта, содержащая хост и строку запроса. Используется в файле index.jsp.
	 */
	private Map<String, String> hostUserdataMap = new HashMap<String, String>();

	private String solutionsDirRoot;

	private String resourcesDirRoot;

	private Map<String, Object> sessionAttributesMap = null;

	/**
	 * Переменная класса, содержащего в себе карту со всеми свойствами из файла
	 * generalapp.properties.
	 */

	private final GeneralAppProperties generalAppProperties = new GeneralAppProperties();

	public synchronized Collection<LoggingEventDecorator> getLastLogEvents() {
		return lastLogEvents;
	}

	public synchronized Collection<LoggingEventDecorator>
			getLastLogEvents(final ServletRequest request) {
		SortedMap<String, List<String>> params;
		try {
			params = ServletUtils.prepareURLParamsMap((HttpServletRequest) request);
		} catch (UnsupportedEncodingException e) {
			throw new ServerLogicError(e);
		}

		return getLastLogEvents(params);
	}

	public Collection<LoggingEventDecorator>
			getLastLogEvents(final Map<String, List<String>> params) {
		Collection<LoggingEventDecorator> result = new ArrayList<>();

		skip: for (LoggingEventDecorator event : lastLogEvents) {
			if (params != null) {
				for (Map.Entry<String, List<String>> entry : params.entrySet()) {
					if (!event.isSatisfied(entry.getKey(), entry.getValue().get(0))) {
						continue skip;
					}
				}
			}
			result.add(event);
		}
		return result;
	}

	public synchronized void addLogEvent(final LoggingEventDecorator event) {
		lastLogEvents.add(event);
	}

	private AppInfoSingleton() {
		super();
	}

	public Map<String, SessionInfo> getSessionInfoMap() {
		return sessionInfoMap;
	}

	public String getServletContainerVersion() {
		return servletContainerVersion;
	}

	public void setServletContainerVersion(final String aServletContainerVersion) {
		servletContainerVersion = aServletContainerVersion;
	}

	public static AppInfoSingleton getAppInfo() {
		return INSTANCE;
	}

	/**
	 * Добавляет сессию в список без параметров URL. Функция используется в
	 * тестовых целях.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 */
	public void addSession(final String sessionId) {
		getOrInitSessionInfoObject(sessionId);
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info("Число пользовательских сессий: " + getAppInfo().sessionInfoMap.size());
		}
	}

	/**
	 * Получает идентификатор Userdata из параметров URL.
	 * 
	 * @return - строку с идентификатором.
	 * @param aMap
	 *            - параметры URL.
	 */
	public String getUserdataIdFromURLParams(final Map<String, ArrayList<String>> aMap) {
		String userdataId = null;

		for (Map.Entry<String, ArrayList<String>> entry : aMap.entrySet()) {
			if (ExchangeConstants.URL_PARAM_PERSPECTIVE.equals(entry.getKey())) {
				if (aMap.get(entry.getKey()) != null) {
					userdataId = Arrays.toString(entry.getValue().toArray()).trim();
					userdataId = userdataId.replace("[", "").replace("]", "");
					break;
				}
			}
			if (ExchangeConstants.URL_PARAM_USERDATA.equals(entry.getKey())) {
				if (aMap.get(entry.getKey()) != null) {
					userdataId = Arrays.toString(entry.getValue().toArray()).trim();
					userdataId = userdataId.replace("[", "").replace("]", "");
					break;
				}
			}
		}

		return userdataId;
	}

	public Map<String, UserData> getUserdatas() {
		return userdatas;
	}

	/**
	 * Добавляет UserData в список.
	 * 
	 * @param userdataId
	 *            Идентификатор UserData
	 * 
	 * @param path
	 *            Путь к userdata
	 */
	public void addUserData(final String userdataId, final String path) {
		userdatas.put(userdataId, new UserData(path));
	}

	/**
	 * Возвращает UserData по его идентификатору.
	 * 
	 * @param userdataId
	 *            Идентификатор UserData
	 * 
	 * @return UserData
	 */
	public UserData getUserData(final String userdataId) {
		UserData us = null;
		if (userdataId != null) {
			us = userdatas.get(userdataId);
		}
		return us;
	}

	/**
	 * Устанавливает признак authViaAuthServer для сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param authViaAuthServer
	 *            - признак того, что аутентификация прошла через AuthServer.
	 */
	public void setAuthViaAuthServerForSession(final String sessionId,
			final boolean authViaAuthServer) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		si.setAuthViaAuthServer(authViaAuthServer);
	}

	/**
	 * Получает значение признака authViaAuthServer для сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - значение признака.
	 */
	public boolean getAuthViaAuthServerForSession(final String sessionId) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		return si.isAuthViaAuthServer();
	}

	/**
	 * Устанавливает временный уникальный пароль для пользователя, который
	 * аутентифицировался через AuthServer.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param pass
	 *            - пароль.
	 */
	public void setAuthServerCrossAppPasswordForSession(final String sessionId,
			final String pass) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		si.setAuthServerCrossAppPassword(pass);
	}

	/**
	 * Получает временный уникальный пароль для пользователя, который
	 * аутентифицировался через AuthServer.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - пароль.
	 */
	public String getAuthServerCrossAppPasswordForSession(final String sessionId) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		return si.getAuthServerCrossAppPassword();
	}

	/**
	 * Инициализирует пустой объект с информацией о сессии в карте сессий.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - объект с информацией о сессии.
	 */
	public SessionInfo getOrInitSessionInfoObject(final String sessionId) {
		SessionInfo res = sessionInfoMap.get(sessionId);
		if (res == null) {
			res = new SessionInfo();
			sessionInfoMap.put(sessionId, res);
		}
		return res;
	}

	/**
	 * Удаляет информацию о сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 */
	public void removeSessionInfo(final String sessionId) {
		sessionInfoMap.remove(sessionId);
	}

	/**
	 * Очищает карту сессий.
	 */
	public void clearSessions() {
		sessionInfoMap.clear();
	}

	public String getCurUserDataId() {
		String res = curUserDataId.get();
		return res;
	}

	public String getCurUserDataIdSafe() {
		String res = curUserDataId.get();
		return res != null ? res : "";
	}

	/**
	 * Устанавливает новое значение текущей userdata.
	 * 
	 * @param aMap
	 *            - параметры URL.
	 */
	public void setCurUserDataIdFromMap(final Map<String, ArrayList<String>> aMap) {
		String userDataId = getUserdataIdFromURLParams(aMap);
		if (userDataId == null) {
			userDataId = ExchangeConstants.DEFAULT_USERDATA;
		}

		if (!userdatas.containsKey(userDataId)) {
			throw new NoSuchUserDataException(userDataId);
		}

		curUserDataId.set(userDataId);
	}

	/**
	 * Метод для прямой установки currentUserDataId.
	 * 
	 * @param aUserDataId
	 *            - новое значение currentUserDataId.
	 */
	public void setCurUserDataId(final String aUserDataId) {
		curUserDataId.set(aUserDataId);
	}

	private CacheManager generateCacheManager() {
		final URL myUrl = this.getClass().getResource("/ehcache.xml");
		Configuration xmlConfig = null;
		try {
			xmlConfig = new XmlConfiguration(myUrl);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| SAXException | IOException e) {
			e.printStackTrace();
		}
		CacheManager myCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		myCacheManager.init();
		return myCacheManager;
	}

	public Object getGridCacheState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context) {
		Cache<Object, Object> cache =
			cacheManager.getCache(GRID_STATE_CACHE, Object.class, Object.class);
		String key = getSessionKeyForCaching(sessionId, dpei, context);
		// Element el = cache.get(key);
		// if (el != null) {
		// return el.getValue();
		// }
		if (cache.get(key) != null) {
			return cache.get(key);
		}
		return null;
	}

	public void storeGridCacheState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context, final Object state) {
		Cache<Object, Object> cache =
			cacheManager.getCache(GRID_STATE_CACHE, Object.class, Object.class);
		String key = getSessionKeyForCaching(sessionId, dpei, context);
		// Element cacheEl = new Element(key, state);
		// cache.put(cacheEl);
		cache.put(key, state);
	}

	public Object getLyraGridCacheState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context) {
		Cache<Object, Object> cache =
			cacheManager.getCache(LYRA_GRID_STATE_CACHE, Object.class, Object.class);
		String key = getSessionKeyForCaching(sessionId, dpei, context);
		// Element el = cache.get(key);
		// if (el != null) {
		// return el.getValue();
		// }
		if (cache.get(key) != null) {
			return cache.get(key);
		}
		return null;
	}

	public void storeLyraGridCacheState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context, final Object state) {
		Cache<Object, Object> cache =
			cacheManager.getCache(LYRA_GRID_STATE_CACHE, Object.class, Object.class);
		String key = getSessionKeyForCaching(sessionId, dpei, context);
		// Element cacheEl = new Element(key, state);
		// cache.put(cacheEl);
		cache.put(key, state);
	}

	private String getSessionKeyForCaching(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context) {
		return sessionId + AppInfoSingleton.getAppInfo().getCurUserDataId()
				+ dpei.getKeyForCaching(context);
	}

	public Cache<Object, Object> getGridStateCache() {
		CacheManager cm = getCacheManager();
		Cache<Object, Object> cache = cm.getCache(GRID_STATE_CACHE, Object.class, Object.class);
		return cache;
	}

	public Cache<Object, Object> getLyraGridStateCache() {
		CacheManager cm = getCacheManager();
		Cache<Object, Object> cache =
			cm.getCache(LYRA_GRID_STATE_CACHE, Object.class, Object.class);
		return cache;
	}

	public Cache<String, DataPanel> getDataPanelCache() {
		CacheManager cm = getCacheManager();
		Cache<String, DataPanel> cache =
			cm.getCache("dataPanelCache", String.class, DataPanel.class);
		return cache;
	}

	/**
	 * Специальный кэш, используемый для локализации с помощью Gettext,
	 * содержащий sessionId и язык.
	 */
	public Cache<String, String> getLocalizationCache() {
		CacheManager cm = getCacheManager();
		Cache<String, String> cache = cm.getCache("localizationCache", String.class, String.class);
		return cache;
	}

	/**
	 * Специальный кэш, используемый для локализации с помощью Gettext, в
	 * котором предполагается хранить sessionId и объект ResourceBundle.
	 */
	public Cache<Object, Object> getLocalizedBundleCache() {
		CacheManager cm = getCacheManager();
		Cache<Object, Object> cache =
			cm.getCache("localizedBundleCache", Object.class, Object.class);
		return cache;
	}

	public long numberofDataPanelCacheSizeBytes() {
		long n = 0L;
		for (Cache.Entry<String, DataPanel> s : AppInfoSingleton.getAppInfo()
				.getDataPanelCache()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(s.getValue());
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			n += baos.size();
		}
		return n;
	}

	public int numberOfDataPanelCacheEntries() {
		int n = 0;
		for (@SuppressWarnings("unused")
		Cache.Entry<String, DataPanel> s : AppInfoSingleton.getAppInfo().getDataPanelCache()) {
			++n;
		}
		return n;
	}

	public long numberofGridStateCacheSizeBytes() {
		long n = 0L;
		for (Cache.Entry<Object, Object> s : AppInfoSingleton.getAppInfo().getGridStateCache()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(s.getValue());
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			n += baos.size();
		}
		return n;
	}

	public int numberOfGridStateCacheEntries() {
		int n = 0;
		for (@SuppressWarnings("unused")
		Cache.Entry<Object, Object> s : AppInfoSingleton.getAppInfo().getGridStateCache()) {
			++n;
		}
		return n;
	}

	public UserData getCurUserData() {
		return userdatas.get(getCurUserDataId());
	}

	public String getWebAppPath() {
		return webAppPath;
	}

	public void setWebAppPath(final String aPath) {
		webAppPath = aPath;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public String getUserdataRoot() {
		return userdataRoot;
	}

	public void setUserdataRoot(final String aUserdataRoot) {
		userdataRoot = aUserdataRoot;
	}

	public String getShowcaseAppOnStartMessage() {
		return showcaseAppOnStartMessage;
	}

	public void setShowcaseAppOnStartMessage(final String aShowcaseAppOnStartMessage) {
		showcaseAppOnStartMessage = aShowcaseAppOnStartMessage;
	}

	public SortedSet<String> getExecutedProc() {
		return executedProc;
	}

	public void addExecutedProc(final String procName) {
		executedProc.add(procName);
	}

	public String getUserDataLogConfFile() {
		return userDataLogConfFile;
	}

	public void setUserDataLogConfFile(final String aUserDataLogConfFile) {
		userDataLogConfFile = aUserDataLogConfFile;
	}

	public void initWebConsole() {
		lastLogEvents = new LastLogEvents();
	}

	public Boolean getIsCelestaInitialized() {
		return isCelestaInitialized;
	}

	public void setIsCelestaInitialized(final Boolean aisCelestaInitialized) {
		this.isCelestaInitialized = aisCelestaInitialized;
	}

	public Exception getCelestaInitializationException() {
		return celestainitializationException;
	}

	public void
			setCelestaInitializationException(final Exception acelestainitializationException) {
		this.celestainitializationException = acelestainitializationException;
	}

	public boolean isEnableLogLevelInfo() {
		return enableLogLevelInfo;
	}

	public void setEnableLogLevelInfo(final boolean aEnableLogLevelInfo) {
		enableLogLevelInfo = aEnableLogLevelInfo;
	}

	public boolean isEnableLogLevelWarning() {
		return enableLogLevelWarning;
	}

	public void setEnableLogLevelWarning(final boolean aEnableLogLevelWarning) {
		enableLogLevelWarning = aEnableLogLevelWarning;
	}

	public boolean isEnableLogLevelError() {
		return enableLogLevelError;
	}

	public void setEnableLogLevelError(final boolean aEnableLogLevelError) {
		enableLogLevelError = aEnableLogLevelError;
	}

	public boolean isEnableActiviti() {
		return enableActiviti;
	}

	public void setEnableActiviti(final boolean aenableActiviti) {
		this.enableActiviti = aenableActiviti;
	}

	public ProcessEngine getActivitiProcessEngine() {
		return activitiProcessEngine;
	}

	public void setActivitiProcessEngine(final ProcessEngine aactivitiProcessEngine) {
		this.activitiProcessEngine = aactivitiProcessEngine;
	}

	public Map<ActivitiEventType, List<String>> getActivitiEventScriptDictionary() {
		return activitiEventScriptDictionary;
	}

	public void setActivitiEventScriptDictionary(
			final Map<ActivitiEventType, List<String>> anActivitiEventScriptDictionary) {
		this.activitiEventScriptDictionary = anActivitiEventScriptDictionary;
	}

	public Map<String, String> getHostUserdataMap() {
		return hostUserdataMap;
	}

	public void setHostUserdataMap(final Map<String, String> anHostUserdataMap) {
		this.hostUserdataMap = anHostUserdataMap;
	}

	public String getSolutionsDirRoot() {
		return solutionsDirRoot;
	}

	public void setSolutionsDirRoot(final String aSolutionsDirRoot) {
		this.solutionsDirRoot = aSolutionsDirRoot;
	}

	public String getResourcesDirRoot() {
		return resourcesDirRoot;
	}

	public void setResourcesDirRoot(final String aResourcesDirRoot) {
		this.resourcesDirRoot = aResourcesDirRoot;
	}

	public void setSessionAttributesMap(final Map<String, Object> aSessionAttributesMap) {
		this.sessionAttributesMap = aSessionAttributesMap;
	}

	public Map<String, Object> getSessionAttributesMap() {
		return sessionAttributesMap;
	}

	public GeneralAppProperties getGeneralAppProperties() {
		return generalAppProperties;
	}

	public String getSesid() {
		return sesid;
	}

	public void setSesid(final String aSesid) {
		this.sesid = aSesid;
	}

	public String getOldSesid() {
		return oldSesid;
	}

	public void setOldSesid(final String aOldSesid) {
		this.oldSesid = aOldSesid;
	}

	public List<String> getAdditionalParametersList() {
		return additionalParametersList;
	}

	public void setAdditionalParametersList(final List<String> anAdditionalParametersList) {
		this.additionalParametersList = anAdditionalParametersList;
	}

	public Map<String, Authentication> getSessionAuthenticationMapForCrossDomainEntrance() {
		return sessionAuthenticationMapForCrossDomainEntrance;
	}
}
