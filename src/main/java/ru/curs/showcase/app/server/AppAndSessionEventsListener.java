package ru.curs.showcase.app.server;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.*;

import javax.management.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.*;

import ru.curs.celesta.Celesta;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.server.redirection.RedirectionUserdataProp;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.*;
import ru.curs.showcase.security.esia.*;
import ru.curs.showcase.security.logging.Event.TypeEvent;
import ru.curs.showcase.security.logging.*;

/**
 * Перехватчик старта приложения и сессии. Служит для инициализации приложения.
 * 
 * @author den
 * 
 */
public class AppAndSessionEventsListener implements ServletContextListener, HttpSessionListener {
	private static final String SHOWCASE_LOADING = "Showcase загружается...";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppAndSessionEventsListener.class);

	private AbstractRefreshableWebApplicationContext actx;

	/**
	 * Количество активных сессий.
	 */
	private static Object activeSessions = 0;

	/**
	 * Количество аутентифицированных сессий.
	 */
	private static Integer authenticatedSessions = 0;

	private MBeanServer mBeanServer;

	private ObjectName objectName;

	private static String contextPath;

	public static String getContextPath() {
		return contextPath;
	}

	public static synchronized Object getActiveSessions() {
		return activeSessions;
	}

	public static synchronized void setActiveSessions(final Object anActiveSessions) {
		activeSessions = anActiveSessions;
	}

	public static synchronized Integer getAuthenticatedSessions() {
		return authenticatedSessions;
	}

	public static synchronized void incrementingAuthenticatedSessions() {
		++authenticatedSessions;
	}

	public static synchronized void decrementingAuthenticatedSessions() {
		--authenticatedSessions;
		if (authenticatedSessions < 0)
			authenticatedSessions = 0;
	}

	@Override
	public final void contextInitialized(final ServletContextEvent arg0) {
		LOGGER.info(SHOWCASE_LOADING);

		AppInitializer.initialize();

		try {
			ProductionModeInitializer.initialize(arg0.getServletContext());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			AppInfoSingleton.getAppInfo().setShowcaseAppOnStartMessage(e.getMessage());
		}

		if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage().isEmpty()) {
			try {
				AppInfoSingleton.getAppInfo().getGeneralAppProperties().initialize();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				AppInfoSingleton.getAppInfo().setShowcaseAppOnStartMessage(e.getMessage());
			}

			if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage().isEmpty()) {

				File platformPoFile =
					new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + File.separator
							+ "common.sys" + File.separator + "resources" + File.separator
							+ "platform.po");

				File platformPoFileDefault =
					new File(AppInfoSingleton.getAppInfo().getResourcesDirRoot() + File.separator
							+ "platform.po");

				if (!platformPoFile.exists()) {
					if (!platformPoFileDefault.exists()) {
						LOGGER.error("ОШИБКА: Не удалось найти дефолтный файл platform.po "
								+ "локализации клиенсткой части Showcase");
						AppInfoSingleton.getAppInfo().setShowcaseAppOnStartMessage(
								"ОШИБКА: Не удалось найти дефолтный файл platform.po "
										+ "локализации клиенсткой части Showcase");
					}
				}

				if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage().isEmpty()) {
					// Установка анонимного входа
					Properties props = UserDataUtils.getGeneralProperties();
					boolean pr =
						Boolean.parseBoolean(props.getProperty(
								"showcase.authentication.anonymous", "false").trim());

					CustomAccessProvider cap =
						ApplicationContextProvider.getApplicationContext().getBean(
								"customAccessProvider", CustomAccessProvider.class);
					if (pr) {
						cap.setAccess("permitAll");
					}

					RedirectionUserdataProp.readAndSetRedirectproperties();

					contextPath = arg0.getServletContext().getContextPath();
					if (contextPath == null || "".equals(contextPath))
						contextPath = "/";
					mBeanServer = ManagementFactory.getPlatformMBeanServer();
					try {
						objectName =
							new ObjectName("Catalina:type=Manager,context=" + contextPath
									+ ",host=localhost");
					} catch (MalformedObjectNameException e1) {
						// e1.printStackTrace();
					}

					Timer updateTimer = new Timer(true);
					updateTimer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							try {
								activeSessions =
									mBeanServer.getAttribute(objectName, "activeSessions");
							} catch (AttributeNotFoundException | InstanceNotFoundException
									| MBeanException | ReflectionException e) {
								// e.printStackTrace();
							}
						}
					}, 0, 10 * 1000);

					try {
						// Регистрация класса MBean в MBean-сервере
						ActiveSessions activeSessionsMBean = new ActiveSessions();
						ObjectName activeSessionsName =
							new ObjectName("AppAndSessionEventsListener:name=activeSessionsMBean");
						mBeanServer.registerMBean(activeSessionsMBean, activeSessionsName);
					} catch (Exception e) {
						// e.printStackTrace();
					}

					WebApplicationContext ctx =
						WebApplicationContextUtils.getWebApplicationContext(arg0
								.getServletContext());
					actx = (AbstractRefreshableWebApplicationContext) ctx;
					actx.refresh();

					try {
						try {
							Properties celestaProps = UserDataUtils.getGeneralCelestaProperties();

							String javaLibPath = celestaProps.getProperty("javalib.path");
							for (String path : JythonIterpretatorFactory
									.getGeneralScriptDirFromWebInf("lib")) {
								if ("".equals(javaLibPath) || javaLibPath == null) {
									javaLibPath = path;
								} else {
									javaLibPath = javaLibPath + File.pathSeparator + path;
								}
							}
							if (javaLibPath != null) {

								javaLibPath = javaLibPath.replace("/", File.separator);

								celestaProps.setProperty("javalib.path", javaLibPath);
							} else {
								celestaProps.setProperty("javalib.path", "");
							}

							String pyLibPath = celestaProps.getProperty("pylib.path");
							for (String path : JythonIterpretatorFactory
									.getGeneralScriptDirFromWebInf("libJython")) {
								if ("".equals(pyLibPath) || pyLibPath == null) {
									pyLibPath = path;
								} else {
									pyLibPath = pyLibPath + File.pathSeparator + path;
								}
							}
							pyLibPath = pyLibPath.replace("/", File.separator);
							celestaProps.setProperty("pylib.path", pyLibPath);

							if (celestaProps != null) {

								Celesta c = Celesta.createInstance(celestaProps);
								AppInfoSingleton.getAppInfo().setCelestaInstance(c);

								// Celesta.initialize(celestaProps);
								AppInfoSingleton.getAppInfo().setIsCelestaInitialized(true);
							} else {
								if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
									LOGGER.warn("Celesta properties (in app.properties) is not set");
								}
								AppInfoSingleton
										.getAppInfo()
										.setCelestaInitializationException(
												new Exception(
														"Celesta properties (in app.properties) is not set"));
							}
						} catch (Exception ex) {
							if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
								LOGGER.error("Ошибка инициализации celesta", ex);
							}
							AppInfoSingleton.getAppInfo().setCelestaInitializationException(ex);
						}
					} finally {
						ProductionModeInitializer.initActiviti();

						try {
							ESIAManager.init();
						} catch (ESIAException e) {
							if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
								LOGGER.error("Ошибка инициализации ESIA");
							}
						}

					}
				}
			}
		}
	}

	@Override
	public final void contextDestroyed(final ServletContextEvent arg0) {
		JMXBeanRegistrator.unRegister();
		// AppInfoSingleton.getAppInfo().getCacheManager().shutdown();
		AppInfoSingleton.getAppInfo().getCacheManager().close();
		ConnectionFactory.unregisterDrivers();
		if (actx != null) {
			actx.close();
		}
	}

	@Override
	public final void sessionCreated(final HttpSessionEvent arg0) {
		try {
			Object anActiveSessions = mBeanServer.getAttribute(objectName, "activeSessions");
			setActiveSessions(anActiveSessions);
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException
				| ReflectionException e) {
			// e.printStackTrace();
		}

		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info("сессия Showcase создается... " + arg0.getSession().getId());
			LOGGER.info("Showcase.Sessions.Count: DateTime: " + new Date() + " Number: "
					+ getActiveSessions());
		}
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent arg0) {
		HttpSession destrHttpSession = arg0.getSession();

		try {
			Object anActiveSessions = mBeanServer.getAttribute(objectName, "activeSessions");
			setActiveSessions(anActiveSessions);
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException
				| ReflectionException e) {
			// e.printStackTrace();
		}

		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info("сессия Showcase удаляется..." + destrHttpSession.getId());
			LOGGER.info("Showcase.Sessions.Count: DateTime: " + new Date() + " Number: "
					+ getActiveSessions());
		}

		SecurityContext context =
			(SecurityContext) destrHttpSession
					.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if (context != null) {
			Authentication auth = context.getAuthentication();
			if (auth != null) {
				TypeEvent typeEvent = TypeEvent.SESSIONTIMEOUT;
				if (destrHttpSession.getAttribute(SecurityLoggingCommand.IS_CLICK_LOGOUT) != null) {
					typeEvent = TypeEvent.LOGOUT;
					decrementingAuthenticatedSessions();
				}
				if (typeEvent == TypeEvent.SESSIONTIMEOUT) {
					decrementingAuthenticatedSessions();
				}
				SecurityLoggingCommand logCommand =
					new SecurityLoggingCommand(new CompositeContext(), null, destrHttpSession,
							typeEvent);
				logCommand.execute();
			}
		}

		try {

			AppInfoSingleton.getAppInfo().getCelestaInstance()
					.logout(destrHttpSession.getId(), false);
			AppInfoSingleton.getAppInfo().getSessionSidsMap().remove(destrHttpSession.getId());

			// Celesta.getInstance().logout(destrHttpSession.getId(), false);
		} catch (Exception e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Ошибка разлогинивания сессии в celesta", e);
			}
		}
	}
}