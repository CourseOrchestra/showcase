package ru.curs.showcase.util;

import java.io.*;

/**
 * Класс для пакетных операций с файлами. Используется совместно с классами,
 * реализующими интерфейс {@link ru.curs.showcase.util.FileAction FileAction}:
 * 
 * {@link ru.curs.showcase.util.CopyFileAction CopyFileAction}. а также
 * интерфейс {@link java.io.FilenameFilter FilenameFilter}:
 * {@link ru.curs.showcase.util.RegexFilenameFilter RegexFilenameFilter}. Пример
 * использования: <code>
 * BatchFileProcessor fprocessor = new BatchFileProcessor(sourceDir, new
 * RegexFilenameFilter( "^[.].*", false)); try { fprocessor.process(new
 * CopyFileAction(destDir, true)); } catch (IOException e) {
 * LOGGER.error("Ошибка копирования файла:" + e.getMessage()); }
 * </code>
 * 
 * @author den
 * 
 */
public class BatchFileProcessor {
	/**
	 * Исходный каталог с файлами.
	 */
	private final File sourceDir;
	/**
	 * Фильтр для выборки файлов из каталога.
	 */
	private final FilenameFilter filter;

	/**
	 * Признак того, что нужно обработать также и исходный каталог.
	 */
	private boolean includeSourceDir = false;

	public BatchFileProcessor(final String aSourceDir, final FilenameFilter aFilter) {
		sourceDir = new File(aSourceDir);
		filter = aFilter;
	}

	public BatchFileProcessor(final String aSourceDir, final boolean aIncludeSourceDir) {
		sourceDir = new File(aSourceDir);
		filter = null;
		includeSourceDir = aIncludeSourceDir;
	}

	/**
	 * Выполняет действие над группой файлов.
	 * 
	 * @param action
	 *            - действие.
	 * @throws IOException
	 */
	public void process(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile()) {
				action.perform(f);
			} else if (f.isDirectory()) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(getParentDir() + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
				action.perform(f);
			}
		}
	}

	public void processWithoutLoginContent(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile()) {
				action.perform(f);
			} else if (f.isDirectory() && !("login_content".equals(f.getName()))) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(getParentDir() + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
				action.perform(f);
			}
		}
	}

	public void processWithoutWebInf(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile()) {
				// action.perform(f);
			} else if (f.isDirectory()
					&& ("js".equals(f.getName()) || "css".equals(f.getName()) || "resources"
							.equals(f.getName()))) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(getParentDir() + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
				action.perform(f);
			}
		}
	}

	public void processForPlugins(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile()) {
				// action.perform(f);
			} else if (f.isDirectory()
					&& ("plugins".equals(f.getName()) || "libraries".equals(f.getName()))) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(getParentDir() + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
				action.perform(f);
			}
		}
	}

	public void processForCSS(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile()) {
				// action.perform(f);
			} else if (f.isDirectory() && ("css".equals(f.getName()))) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(getParentDir() + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
				action.perform(f);
			}
		}
	}

	public void processForWebInf(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile() && "user.properties".equals(f.getName())) {
				action.perform(f);
			} else if (f.isDirectory()
					&& !("lib".equals(f.getName()) || "libJython".equals(f.getName()))) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(getParentDir() + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
				action.perform(f);
			}
		}
	}

	public void processForLoginJsp(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile()
					&& ("login.jsp".equals(f.getName()) || "sestimeout.jsp".equals(f.getName())
							|| "about.jsp".equals(f.getName()) || "err500.jsp".equals(f.getName()) || "err404.jsp"
								.equals(f.getName()))) {
				action.perform(f);
			} else if (f.isDirectory()) {
			}
		}
	}

	public void processForSecurityXml(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile() && ("security.xml".equals(f.getName()))) {
				action.perform(f);
			} else if (f.isDirectory()) {
			}
		}
	}

	private String getParentDir() {
		if (includeSourceDir) {
			return sourceDir.getParent();
		}
		return sourceDir.getPath();
	}

	private File[] getFilesList() {
		if (includeSourceDir) {
			File[] flist = { sourceDir };
			return flist;
		} else {
			if (filter != null) {
				return sourceDir.listFiles(filter);
			} else {
				return sourceDir.listFiles();
			}
		}
	}
}
