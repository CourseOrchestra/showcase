package ru.curs.showcase.app.server;

import java.lang.management.ManagementFactory;

import javax.management.*;

import org.ehcache.CacheManager;
import org.slf4j.*;

import ru.curs.showcase.runtime.*;

/**
 * Регистрация локальных JMX bean.
 * 
 * @author den
 * 
 */
public final class JMXBeanRegistrator {

	private static final String REGISTER_ERROR = "Ошибка при регистрации MBean ";

	private static final Logger LOGGER = LoggerFactory.getLogger(JMXBeanRegistrator.class);

	private static final String UNREGISTER_ERROR = "Ошибка при отмене регистрации MBean ";

	/**
	 * Сервер JMX Bean.
	 */
	private static MBeanServer mbs = null;

	private static boolean needDisable = false;

	private static MBeanServer getMBeanServer() {
		if (mbs == null) {
			mbs = ManagementFactory.getPlatformMBeanServer();
		}
		return mbs;
	}

	/**
	 * Функция регистрации JMX bean.
	 */
	public static void register() {
		boolean jmxEnable = Boolean.valueOf(UserDataUtils.getGeneralOptionalProp("jmx.enable"));
		needDisable = needDisable || jmxEnable;
		if (jmxEnable) {
			registerEncacheMBean();
			registerShowcaseMBean();
		}
	}

	/**
	 * Для модульных тестов нужна проверка на то, что bean уже зарегистрирован.
	 * Если это так, то повторно не регистрируем - для простоты.
	 */
	private static void registerEncacheMBean() {
		CacheManager manager = AppInfoSingleton.getAppInfo().getCacheManager();
		try {
			if (getMBeanServer().isRegistered(getEhcasheMBeanName(manager))) {
				return;
			}
		} catch (MalformedObjectNameException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(REGISTER_ERROR + e.getLocalizedMessage());
			}
		}
		try {
			getMBeanServer().registerMBean(manager, getEhcasheMBeanName(manager));
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | MalformedObjectNameException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(REGISTER_ERROR + e.getLocalizedMessage());
			}
		}
		// ManagementService.registerMBeans(manager, getMBeanServer(), true,
		// false, false, true);
	}

	private static void registerShowcaseMBean() {
		JMXMonitorBean monBean = new JMXMonitorBeanImpl();
		ObjectName beanName = null;
		try {
			beanName = getShowcaseMBeanName();
			if (getMBeanServer().isRegistered(beanName)) {
				getMBeanServer().unregisterMBean(beanName);
			}
			getMBeanServer().registerMBean(monBean, beanName);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | InstanceNotFoundException
				| MalformedObjectNameException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(REGISTER_ERROR + e.getLocalizedMessage());
			}
		}
	}

	public static ObjectName getShowcaseMBeanName() throws MalformedObjectNameException {
		return new ObjectName("Showcase:name=Showcase.Monitor");
	}

	private static ObjectName getEhcasheMBeanName(final CacheManager manager)
			throws MalformedObjectNameException {
		return new ObjectName("net.sf.ehcache:type=CacheManager,name=" + manager.toString());
	}

	public static void unRegister() {
		if (needDisable) {
			try {
				getMBeanServer().unregisterMBean(getShowcaseMBeanName());
			} catch (InstanceNotFoundException | MBeanRegistrationException
					| MalformedObjectNameException e) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error(UNREGISTER_ERROR + e.getLocalizedMessage());
				}
			}
		}
	}

	private JMXBeanRegistrator() {
		throw new UnsupportedOperationException();
	}
}
