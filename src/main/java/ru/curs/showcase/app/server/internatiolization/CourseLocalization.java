package ru.curs.showcase.app.server.internatiolization;

import gnu.gettext.GettextResource;

import java.io.*;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.runtime.*;

/**
 * 
 * @author s.borodanev
 *
 *         Класс, предназначенный для вызова функциональных возможностей пакета
 *         GNU GetText. Каждый из методов данного класса перевызывает
 *         соответствующий метод указанного пакета. Указанный пакет выполняет
 *         функции локализации и интернационализации.
 */

public class CourseLocalization {

	/**
	 * Возвращает перевод переменной <VAR>msgid</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @return перевод переменной <VAR>msgid</VAR>, или сама переменная
	 *         <VAR>msgid</VAR>, если перевод не найден
	 */
	public static String gettext(ResourceBundle catalog, String msgid) {
		return GettextResource.gettext(catalog, msgid);
	}

	/**
	 * Возвращает форму множественного числа для номера <VAR>n</VAR> перевода
	 * переменной <VAR>msgid</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @param msgid_plural
	 *            - её форма множественного числа на английском языке
	 * @return перевод переменной <VAR>msgid</VAR>, в зависимости от переменной
	 *         <VAR>n</VAR>, или сама переменная <VAR>msgid</VAR>, или
	 *         переменная <VAR>msgid_plural</VAR>, если перевод не найден
	 */
	public static String
			ngettext(ResourceBundle catalog, String msgid, String msgid_plural, long n) {
		return GettextResource.ngettext(catalog, msgid, msgid_plural, n);
	}

	/**
	 * Возвращает перевод переменной <VAR>msgid</VAR>, в зависимости от
	 * контекстной переменной <VAR>msgctxt</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgctxt
	 *            - контекст для строки-ключа, используется ASCII-кодировка
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @return перевод переменной <VAR>msgid</VAR>, или сама переменная
	 *         <VAR>msgid</VAR>, если перевод не найден
	 */
	public static String pgettext(ResourceBundle catalog, String msgctxt, String msgid) {
		return GettextResource.pgettext(catalog, msgctxt, msgid);
	}

	/**
	 * * Возвращает форму множественного числа для номера <VAR>n</VAR> перевода
	 * переменной <VAR>msgid</VAR>, в зависимости от контекстной переменной
	 * <VAR>msgctxt</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgctxt
	 *            - контекст для строки-ключа, используется ASCII-кодировка
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @param msgid_plural
	 *            - её форма множественного числа на английском языке
	 * @return перевод переменной <VAR>msgid</VAR>, в зависимости от переменной
	 *         <VAR>n</VAR>, или сама переменная <VAR>msgid</VAR>, или
	 *         переменная <VAR>msgid_plural</VAR>, если перевод не найден
	 */
	public static String npgettext(ResourceBundle catalog, String msgctxt, String msgid,
			String msgid_plural, long n) {
		return GettextResource.npgettext(catalog, msgctxt, msgid, msgid_plural, n);
	}

	/**
	 * Метод установки ResourceBundle для дальнейшего использования в переводе
	 * серверной части Showcase с помощью Gettext.
	 * 
	 * @return ResourceBundle
	 */
	public static ResourceBundle getLocalizedResourseBundle() {
		// String lang = UserDataUtils.getLocaleForCurrentUserdata();

		// ProcessBuilder pb = new ProcessBuilder();
		// String classpath = pb.environment().get("CLASSPATH");
		// String localizePath = classpath.substring(classpath.lastIndexOf(";")
		// + 1);
		// File localizeDir = new File(localizePath);

		// String bundleFile = "";
		// for (String file : localizeDir.list()) {
		// if (file.equals(lang + ".class")) {
		// bundleFile = file;
		// break;
		// }
		// }

		File dir = UserDataUtils.getResourceDir(UserDataUtils.getUserDataId());

		String lang = "";
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String sesid = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();
			if (sesid == null) {
				// sesid = AppInfoSingleton.getAppInfo().getSesid();
				String[] arr =
					AppInfoSingleton.getAppInfo().getRemoteAddrSessionMap().values()
							.toArray(new String[0]);
				if (arr.length > 0)
					sesid = arr[arr.length - 1];
			}
			lang = AppInfoSingleton.getAppInfo().getLocalizationCache().get(sesid);
		}

		if (lang == null || "".equals(lang))
			lang = UserDataUtils.getLocaleForCurrentUserdata(UserDataUtils.getUserDataId());

		class MyLoader extends ClassLoader {

			public MyLoader(ClassLoader parent) {
				super(parent);
			}

			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				File classFile = null;
				if (dir.exists()) {
					for (File file : dir.listFiles()) {
						if (file.getName().substring(0, file.getName().lastIndexOf("."))
								.equals(name)) {
							classFile = file;
							break;
						}
					}
				}
				try {
					byte[] b = loadData(classFile);
					return defineClass(
							classFile.getName().substring(0, classFile.getName().lastIndexOf(".")),
							b, 0, b.length);
				} catch (Exception e) {
					return null;
				}

			}

			@Override
			protected void finalize() throws Throwable {
				super.finalize();
			}
		}

		ResourceBundle rb = null;
		try {
			Locale.setDefault(new Locale(lang));
			rb =
				ResourceBundle.getBundle("loc", new Locale(lang), new MyLoader(Thread
						.currentThread().getContextClassLoader()));
			Runtime.getRuntime().runFinalization();
		} catch (Throwable e) {
			rb = null;
		}
		return rb;
	}

	/**
	 * Метод установки ResourceBundle для дальнейшего использования в переводе
	 * серверной части Showcase с помощью Gettext с учётом передачи языка в этот
	 * метод.
	 * 
	 * @param lang
	 *            - передаваемый извне язык
	 * @return ResourceBundle
	 */
	public static ResourceBundle getLocalizedResourseBundle(String lang) {
		File dir = UserDataUtils.getResourceDir(UserDataUtils.getUserDataId());

		class MyLoader extends ClassLoader {

			public MyLoader(ClassLoader parent) {
				super(parent);
			}

			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				File classFile = null;
				if (dir.exists()) {
					for (File file : dir.listFiles()) {
						if (file.getName().substring(0, file.getName().lastIndexOf("."))
								.equals(name)) {
							classFile = file;
							break;
						}
					}
				}
				try {
					byte[] b = loadData(classFile);
					return defineClass(
							classFile.getName().substring(0, classFile.getName().lastIndexOf(".")),
							b, 0, b.length);
				} catch (Exception e) {
					return null;
				}

			}

			@Override
			protected void finalize() throws Throwable {
				super.finalize();
			}
		}

		ResourceBundle rb = null;
		try {
			Locale.setDefault(new Locale(lang));
			rb =
				ResourceBundle.getBundle("loc", new Locale(lang), new MyLoader(Thread
						.currentThread().getContextClassLoader()));
			Runtime.getRuntime().runFinalization();
		} catch (Throwable e) {
			rb = null;
		}
		return rb;
	}

	/**
	 * Вспомогательный метод, используемый при загрузке классов.
	 */
	private static byte[] loadData(File file) throws Exception {
		int maxBufferSize = 1 * 1024 * 1024;
		byte[] buffer = new byte[maxBufferSize];

		try (FileInputStream fileInputStream = new FileInputStream(file);) {
			int bytesAvailable = fileInputStream.available();
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
		}
		return buffer;
	}
}
