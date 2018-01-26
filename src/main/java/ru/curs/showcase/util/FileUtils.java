/**
 * 
 */
package ru.curs.showcase.util;

import java.io.*;
import java.util.Properties;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.exception.*;

/**
 * Класс, содержащий общие функции для работы с файлами.
 * 
 * @author anlug
 * 
 */
public final class FileUtils {

	/**
	 * Имя файла с настройками путей приложения. Пути рекомендуется задавать
	 * абсолютно, т.к. относительный путь отсчитывается либо от папки с eclipse,
	 * либо от папки с Tomcat и не является постоянным. При задании пути нужно
	 * использовать двойной обратный слэш в качестве разделителя.
	 */
	public static final String GENERAL_PROPERTIES = "general.properties";

	private static final String SHOWCASE_ROOTPATH_USERDATA_PARAM = "rootpath.userdata";

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * Универсальная функция загрузки внутренних ресурсов Web-приложения по
	 * относительному пути, используя Java ClassLoader (например, файлов
	 * конфигурации). Загрузка идет из папки classes.
	 * 
	 * @param fileName
	 *            - путь к загружаемому файлу
	 * @return поток с файлом.
	 */
	public static InputStream loadClassPathResToStream(final String fileName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		InputStream result = classLoader.getResourceAsStream(fileName);
		return result;
	}

	private FileUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Процедура копирования файла. В случае отсутствия файла ошибка не
	 * выдается.
	 * 
	 * @param source
	 *            - полный путь к файлу, который будет скопирован (с указанием
	 *            имени файла).
	 * @param destination
	 *            - полный путь места, куда будет скопирован файл (без указания
	 *            имени файла)
	 * @return - возвращает результат копирования файла: true - в случае успеха,
	 *         false - в случае неудачи.
	 */
	public static boolean copyFile(final String source, final String destination) {
		try {
			File sourceFile = new File(source);
			if (!sourceFile.exists()) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error(String.format("Файл или каталог '%s' не существует", source));
				}
				return false;
			}
			BatchFileProcessor fprocessor =
				new BatchFileProcessor(sourceFile.getParent(), new RegexFilenameFilter(
						sourceFile.getName(), true));
			fprocessor.process(new CopyFileAction(destination));
			return true;
		} catch (IOException ex) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(ex.getMessage());
			}
			return false;
		}
	}

	/**
	 * Удаляет каталог с подкаталогами и файлами.
	 * 
	 * @param dir
	 *            - удаляемый каталог.
	 * @throws IOException
	 */
	public static void deleteDir(final String dir) throws IOException {
		BatchFileProcessor fprocessor = new BatchFileProcessor(dir, true);
		fprocessor.process(new DeleteFileAction());
	}

	public static String getTestUserdataRoot(final String file) {
		Properties prop = new Properties();
		InputStream is = loadClassPathResToStream(file);
		try {
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				prop.load(reader);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(file, SettingsFileType.GENERAL_PROPERTIES);
		}
		return prop.getProperty(SHOWCASE_ROOTPATH_USERDATA_PARAM);
	}

	public static String getOutsideWarRoot(final String file) {
		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				prop.load(reader);
			}
			is.close();
		} catch (IOException e) {
			LOGGER.info("В процессе инициализации папки пользовательских данных зафиксировано отсутствие файла "
					+ file);
		}
		return prop.getProperty(SHOWCASE_ROOTPATH_USERDATA_PARAM);
	}
}
