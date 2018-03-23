package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletContext;

import org.activiti.engine.*;
import org.slf4j.*;

import ru.curs.showcase.activiti.EventHandlerForActiviti;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;

/**
 * Инициализатор приложения в рабочем режиме - при запуске Tomcat. Не должен
 * выполняться при запуске модульных тестов. Содержит главную функцию
 * initialize.
 * 
 * @author den
 * 
 */
public final class ProductionModeInitializer {

	private static final String SHOWCASE_ROOTPATH_USERDATA_PARAM = "showcase.rootpath.userdata";

	private static final String FILE_COPY_ERROR = "Ошибка копирования файла при старте Tomcat: %s";

	private static final String COPY_USERDATA_DIRS_PARAM = "js:css:resources";

	private static final String COPY_COMMON_FILES_ON_STARTUP = "copy.common.files.on.startup";

	private static final String COPY_PERSPECTIVE_FILES_ON_STARTUP =
		"copy.perspective.files.on.startup";

	private static final String DELETE_SOLUTIONS_DIR_ON_TOMCAT_STSRTUP =
		"delete.solutions.dir.on.tomcat.startup";

	private static final String USER_DATA_DIR_NOT_FOUND_ERROR =
		"Каталог с пользовательскими данными с именем '%s' не найден. ";
	private static final String NOT_ALL_FILES_COPIED_ERROR =
		"В процессе запуска сервера не все файлы были корректно скопированы. "
				+ "Showcase может работать неверно.";

	private static final String GET_USERDATA_PATH_ERROR =
		"Невозможно получить путь к каталогу с пользовательскими данными";
	public static final String WIDTH_PROP = "width";

	private static final String COMMON_SYS = "common.sys";

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductionModeInitializer.class);

	private ProductionModeInitializer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основной метод инициализатора.
	 * 
	 * @param aServletContext
	 *            - ServletContextEvent.
	 */
	public static void initialize(final ServletContext aServletContext) {
		initClassPath(aServletContext);
		initUserDatas(aServletContext, false);
		readCSSs();
		JMXBeanRegistrator.register();
		// initActiviti();
	}

	public static void initActiviti() {
		if (AppInfoSingleton.getAppInfo().isEnableActiviti()) {
			// log4jConfPath = "/../log4j.properties"
			// PropertyConfigurator.configure(log4jConfPath) # эти две строки
			// нужны для внутреннего логгинга Activiti

			// создаём движок с коннектом к встроенной БД

			// ProcessEngineConfiguration conf =
			// ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();

			String databaseType =
				UserDataUtils.getGeneralOptionalProp("activiti.database.databaseType");

			String jdbcUrl = UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcUrl");

			String jdbcDriver =
				UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcDriver");

			String jdbcUsername =
				UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcUsername");

			String jdbcPassword =
				UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcPassword");

			String history = UserDataUtils.getGeneralOptionalProp("activiti.history.level");

			String databaseTablePrefix =
				UserDataUtils.getGeneralOptionalProp("activiti.database.table.prefix");

			String tablePrefixIsSchema =
				UserDataUtils.getGeneralOptionalProp("activiti.table.prefix.is.schema");

			String jobExecutorActivate =
				UserDataUtils.getGeneralOptionalProp("activiti.jobexecutor.activate");

			ProcessEngineConfiguration conf =
				ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
			conf.setDatabaseType(databaseType);
			// conf.setJdbcUrl("jdbc:sqlserver://172.16.1.26\\SQL2008;databasename=activitydimatest");
			conf.setJdbcUrl(jdbcUrl);
			conf.setJdbcDriver(jdbcDriver);
			conf.setJdbcUsername(jdbcUsername);
			conf.setJdbcPassword(jdbcPassword);
			conf.setHistory(history);
			conf.setJobExecutorActivate(Boolean.parseBoolean(jobExecutorActivate));

			if (databaseTablePrefix != null && !("".equals(databaseTablePrefix))) {
				conf.setDatabaseTablePrefix(databaseTablePrefix);

				if (tablePrefixIsSchema != null && !("".equals(tablePrefixIsSchema))) {
					Boolean b = false;
					try {
						b = Boolean.parseBoolean(tablePrefixIsSchema);
					} catch (Exception e) {
						LOGGER.error("The value of property could not be convert to Boolean.");
					}
					conf.setTablePrefixIsSchema(b);
				}
			}

			ProcessEngine processEngine = conf.buildProcessEngine();
			AppInfoSingleton.getAppInfo().setActivitiProcessEngine(processEngine);
			RuntimeService rs = processEngine.getRuntimeService();
			rs.addEventListener(new EventHandlerForActiviti());
		}

	}

	public static void initUserDatas(final ServletContext aServletContext,
			final boolean fromControlMemoryJsp) {

		AppInfoSingleton.getAppInfo().setSolutionsDirRoot(
				aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR));

		AppInfoSingleton.getAppInfo().setResourcesDirRoot(
				aServletContext.getRealPath("/" + UserDataUtils.RESOURCES_DIR));

		AppInitializer.checkUserDataDir(
				aServletContext.getInitParameter(SHOWCASE_ROOTPATH_USERDATA_PARAM), "context.xml");

		if (AppInfoSingleton.getAppInfo().getUserdatas().size() == 0)
			getUserdataFromOutsideWar(aServletContext);

		AppInitializer.finishUserdataSetupAndCheckLoggingOverride();
		UserDataUtils.checkUserdatas();
		copyUserDatas(aServletContext, fromControlMemoryJsp);
		AppInfoSingleton.getAppInfo().setServletContainerVersion(aServletContext.getServerInfo());
	}

	private static void initClassPath(final ServletContext aServletContext) {
		// String path = aServletContext.getRealPath("index.jsp");
		String path = aServletContext.getRealPath("") + "/index.jsp";
		path = path.replaceAll("\\\\", "/");
		path = path.substring(0, path.lastIndexOf('/'));
		AppInfoSingleton.getAppInfo().setWebAppPath(path);
	}

	private static void readCSSs() {
		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			readCSS(userdataId);
		}

	}

	private static void readCSS(final String userdataId) {

		// Раньше здесь для "советского"грида считывалось только значение
		// ColumnGapWidth из файла стилей грида. Сейчас это не актуально. Код
		// считывания убрал, но механизм оставил -- мало ли нужно еще что-то
		// будет считывать из стилей

		// CSSPropReader reader = new CSSPropReader();
	}

	private static void copyUserDatas(final ServletContext aServletContext,
			final boolean fromControlMemoryJsp) {
		File solutionsDir =
			new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR));
		if (UserDataUtils.getGeneralOptionalProp(DELETE_SOLUTIONS_DIR_ON_TOMCAT_STSRTUP) == null
				|| !"false".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp(
						DELETE_SOLUTIONS_DIR_ON_TOMCAT_STSRTUP).trim())) {
			deleteDirectory(solutionsDir);
		}
		copyGeneralResources(aServletContext, fromControlMemoryJsp);
		copyDefaultUserData(aServletContext, fromControlMemoryJsp);
		copyOtherUserDatas(aServletContext, fromControlMemoryJsp);
		copyClientExtLib(aServletContext);
		copyLoginJsp(aServletContext);
	}

	private static void copyOtherUserDatas(final ServletContext aServletContext,
			final boolean fromControlMemoryJsp) {
		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			if (!(ExchangeConstants.DEFAULT_USERDATA.equals(userdataId))) {
				copyUserData(aServletContext, userdataId, fromControlMemoryJsp);
			}
		}
	}

	private static void copyDefaultUserData(final ServletContext aServletContext,
			final boolean fromControlMemoryJsp) {
		copyUserData(aServletContext, ExchangeConstants.DEFAULT_USERDATA, fromControlMemoryJsp);
	}

	private static void copyGeneralResources(final ServletContext aServletContext,
			final boolean fromControlMemoryJsp) {
		// File generalResRoot =
		// new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
		// + UserDataUtils.GENERAL_RES_ROOT);
		File generalResRoot = null;
		Boolean isAllFilesCopied = true;
		File userdataRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
		File[] list = userdataRoot.listFiles();

		generalResRoot =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + COMMON_SYS);
		if (generalResRoot.exists()) {
			for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
				File generalDir =
					new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR + "/"
							+ "general"));
				File userDataDir =
					new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR + "/"
							+ userdataId));
				userDataDir.mkdir();

				isAllFilesCopied =
					isAllFilesCopied
							&& copyGeneralDir(aServletContext, generalResRoot, userDataDir,
									generalDir, fromControlMemoryJsp);
			}
		}

		// for (File f : list) {
		for (int c = list.length - 1; c >= 0; c--) {
			File f = list[c];
			if (f.getName().startsWith("common") && !(f.getName().equals(COMMON_SYS))) {
				generalResRoot =
					new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + f.getName());
			} // else if (f.getName().equals(COMMON_SYS)) {
				// generalResRoot =
				// new File(AppInfoSingleton.getAppInfo().getUserdataRoot() +
				// "/" + COMMON_SYS);
			// }
			else {
				continue;
			}
			if (generalResRoot.exists()) {
				for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
					File generalDir =
						new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR
								+ "/" + "general"));
					File userDataDir =
						new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR
								+ "/" + userdataId));
					userDataDir.mkdir();

					isAllFilesCopied =
						isAllFilesCopied
								&& copyGeneralDir(aServletContext, generalResRoot, userDataDir,
										generalDir, fromControlMemoryJsp);
				}
			}
		}

		if (!isAllFilesCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static void copyClientExtLib(final ServletContext aServletContext) {
		Boolean isAllFilesCopied = true;
		File generalResRoot =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
					+ UserDataUtils.GENERAL_RES_ROOT + "/js");

		String[] files = null;
		if (generalResRoot.exists()) {
			files = generalResRoot.list();
		}

		BatchFileProcessor fprocessor = null;
		if (files != null) {
			for (String s : files) {
				if ("clientextlib".equals(s)) {
					fprocessor =
						new BatchFileProcessor(generalResRoot.getAbsolutePath() + "/clientextlib",
								new RegexFilenameFilter("^[.].*", false));
				}
			}
		}

		if (fprocessor != null) {
			try {
				fprocessor.process(new CopyFileAction(aServletContext.getRealPath("/" + "js")));
			} catch (IOException e) {
				isAllFilesCopied = false;
				LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
			}
		}

		if (!isAllFilesCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static void copyLoginJsp(final ServletContext aServletContext) {
		Boolean isFileCopied = true;
		File generalResRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
		File solutions = new File(AppInfoSingleton.getAppInfo().getSolutionsDirRoot());

		BatchFileProcessor fprocessor =
			new BatchFileProcessor(generalResRoot.getAbsolutePath(), new RegexFilenameFilter(
					"^[.].*", false));

		if (fprocessor != null) {
			try {
				fprocessor.processForLoginJsp(new CopyFileAction(solutions.getParent()));
				fprocessor.processForSecurityXml(new CopyFileAction(solutions.getParent()
						+ File.separator + "WEB-INF"));
			} catch (IOException e) {
				isFileCopied = false;
				LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
			}
		}

		if (!isFileCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static void copyUserData(final ServletContext aServletContext,
			final String userdataId, final boolean fromControlMemoryJsp) {
		String userDataCatalog = "";
		UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
		if (us == null) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(GET_USERDATA_PATH_ERROR);
			}
			return;
		}
		userDataCatalog = us.getPath();

		String dirsForCopyStr = COPY_USERDATA_DIRS_PARAM;
		String[] dirsForCopy = dirsForCopyStr.split(":");
		Boolean isAllFilesCopied = true;
		for (int i = 0; i < dirsForCopy.length; i++) {
			File dir = new File(userDataCatalog + "/" + dirsForCopy[i]);
			if (!dir.exists()) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
					LOGGER.warn(String.format(USER_DATA_DIR_NOT_FOUND_ERROR, dirsForCopy[i]));
				}
				continue;
			}
			isAllFilesCopied =
				isAllFilesCopied
						&& copyUserDataDir(aServletContext, userDataCatalog, dirsForCopy[i],
								userdataId, fromControlMemoryJsp);
		}

		if (!isAllFilesCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static Boolean copyGeneralDir(final ServletContext aServletContext,
			final File generalResRoot, final File userDataDir, final File generalDir,
			final boolean fromControlMemoryJsp) {
		Boolean isAllFilesCopied = true;

		File[] files = generalResRoot.listFiles();

		BatchFileProcessor fprocessor =
			new BatchFileProcessor(generalResRoot.getAbsolutePath(), new RegexFilenameFilter(
					"^[.].*", false));

		BatchFileProcessor fprocessorForWebInf =
			new BatchFileProcessor(generalResRoot.getAbsolutePath() + "/WEB-INF",
					new RegexFilenameFilter("^[.].*", false));

		try {
			for (File f : files) {
				if ("WEB-INF".equals(f.getName())) {
					fprocessorForWebInf.processForWebInf(new CopyFileAction(aServletContext
							.getRealPath("/" + "WEB-INF")));
				}
				// else if ("plugins".equals(f.getName()) ||
				// "libraries".equals(f.getName())) {
				// fprocessor.processForPlugins(new
				// CopyFileAction(generalDir.getAbsolutePath()));
				// }
				else if ((UserDataUtils.getGeneralOptionalProp(COPY_COMMON_FILES_ON_STARTUP) == null || !"false"
						.equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp(
								COPY_COMMON_FILES_ON_STARTUP).trim()))
						|| fromControlMemoryJsp) {
					fprocessor.processForPlugins(new CopyFileAction(generalDir.getAbsolutePath()));
					fprocessor.processWithoutWebInf(new CopyFileAction(userDataDir
							.getAbsolutePath()));
				}
			}
		} catch (IOException e) {
			isAllFilesCopied = false;
			LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
		}

		return isAllFilesCopied;
	}

	private static Boolean copyUserDataDir(final ServletContext aServletContext,
			final String userDataCatalog, final String dirName, final String userdataId,
			final boolean fromControlMemoryJsp) {
		Boolean isAllFilesCopied = true;
		BatchFileProcessor fprocessor =
			new BatchFileProcessor(userDataCatalog + "/" + dirName, new RegexFilenameFilter(
					"^[.].*", false));
		BatchFileProcessor fprocessorForLoginContent =
			new BatchFileProcessor(userDataCatalog + "/resources/login_content",
					new RegexFilenameFilter("^[.].*", false));
		try {
			if ((UserDataUtils.getOptionalProp(COPY_PERSPECTIVE_FILES_ON_STARTUP, userdataId) == null || !"false"
					.equalsIgnoreCase(UserDataUtils.getOptionalProp(
							COPY_PERSPECTIVE_FILES_ON_STARTUP, userdataId).trim()))
					|| fromControlMemoryJsp) {
				fprocessor.processWithoutLoginContent(new CopyFileAction(aServletContext
						.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR + "/" + userdataId + "/"
								+ dirName)));
				fprocessorForLoginContent.process(new CopyFileAction(aServletContext
						.getRealPath("/resources/login_content")));
			}
		} catch (IOException e) {
			isAllFilesCopied = false;
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
			}
		}
		return isAllFilesCopied;
	}

	private static boolean deleteDirectory(final File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && files[i].listFiles().length > 0) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return path.delete();
	}

	public static void reCopyCSS(final ServletContext aServletContext) {
		File generalResRoot = null;
		File userdataRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
		File[] list = userdataRoot.listFiles();

		generalResRoot =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + COMMON_SYS);
		if (generalResRoot.exists()) {
			for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
				File generalDir =
					new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR + "/"
							+ "general"));
				File userDataDir =
					new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR + "/"
							+ userdataId));

				reCopyGeneralCSS(aServletContext, generalResRoot, userDataDir, generalDir);
			}
		}

		for (int c = list.length - 1; c >= 0; c--) {
			File f = list[c];
			if (f.getName().startsWith("common") && !(f.getName().equals(COMMON_SYS))) {
				generalResRoot =
					new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + f.getName());
			} else {
				continue;
			}
			if (generalResRoot.exists()) {
				for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
					File generalDir =
						new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR
								+ "/" + "general"));
					File userDataDir =
						new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR
								+ "/" + userdataId));

					reCopyGeneralCSS(aServletContext, generalResRoot, userDataDir, generalDir);
				}
			}
		}

		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			String userDataCatalog = "";
			UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
			if (us == null) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error(GET_USERDATA_PATH_ERROR);
				}
				return;
			}
			userDataCatalog = us.getPath();

			File dir = new File(userDataCatalog + "/css");

			if (!dir.exists()) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
					LOGGER.warn(String.format(USER_DATA_DIR_NOT_FOUND_ERROR, "css"));
				}
			}
			reCopyUserdataCSS(aServletContext, userDataCatalog, userdataId);

		}

	}

	private static void reCopyGeneralCSS(final ServletContext aServletContext,
			final File generalResRoot, final File userDataDir, final File generalDir) {
		File[] files = generalResRoot.listFiles();

		BatchFileProcessor fprocessor =
			new BatchFileProcessor(generalResRoot.getAbsolutePath(), new RegexFilenameFilter(
					"^[.].*", false));

		try {
			for (File f : files) {
				if ("css".equals(f.getName())) {
					fprocessor.processForCSS(new CopyFileAction(userDataDir.getAbsolutePath()));
				}
			}
		} catch (IOException e) {
			LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
		}
	}

	private static void reCopyUserdataCSS(final ServletContext aServletContext,
			final String userDataCatalog, final String userdataId) {
		BatchFileProcessor fprocessor =
			new BatchFileProcessor(userDataCatalog + "/css", new RegexFilenameFilter("^[.].*",
					false));
		try {
			fprocessor.process(new CopyFileAction(aServletContext.getRealPath("/"
					+ UserDataUtils.SOLUTIONS_DIR + "/" + userdataId + "/css")));
		} catch (IOException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
			}
		}
	}

	private static void getUserdataFromOutsideWar(ServletContext aServletContext) {
		try {
			int ind = aServletContext.getContextPath().indexOf("/");
			String realPath = aServletContext.getContextPath().substring(ind + 1);
			int ind1 = aServletContext.getRealPath("/").indexOf(File.separator + realPath);
			String realPath1 = aServletContext.getRealPath("/").substring(0, ind1);
			int ind2 = realPath1.lastIndexOf(File.separator);
			String realPath2 = aServletContext.getRealPath("/").substring(0, ind2);
			String file =
				realPath2 + File.separator + "userdatas" + File.separator
						+ FileUtils.GENERAL_PROPERTIES;

			AppInitializer.checkUserDataDir(FileUtils.getOutsideWarRoot(file), file);
		} catch (Exception ex) {
		}
	}
}
