package ru.curs.showcase.util;

import java.io.*;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Действие по копированию файла.
 * 
 * @author den
 * 
 */
public class CopyFileAction implements FileAction {
	private static final String FILE_SKIPED_INFO = "Файл '%s' пропущен при копировании в '%s'";
	private static final String FILE_OVERWRITE_INFO = "Файл '%s' будет перезаписан";
	private static final String FILE_COPIED_INFO = "Файл '%s' скопирован в '%s'";

	private static final String DIR_CREATED_INFO = "Каталог '%s' не существовал и был создан";

	private static final String CREATE_DIR_ERROR = "Не удалось создать каталог '%s'";

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CopyFileAction.class);

	/**
	 * Размер буфера при копировании.
	 */
	private static final int COPY_BUF_SIZE = 1024;

	/**
	 * Каталог назначения для файла.
	 */
	private String toDir;

	/**
	 * Признак того, что файл назначения нужно перезаписывать при необходимости.
	 */
	private boolean overwrite = true;

	/**
	 * Признак того, что если каталог для файла назначение не существует, то его
	 * нужно создать.
	 */
	private boolean createDirIfNotExists = true;

	public String getToDir() {
		return toDir;
	}

	public void setToDir(final String aToDir) {
		toDir = aToDir;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(final boolean aOverwrite) {
		overwrite = aOverwrite;
	}

	public boolean isCreateDirIfNotExists() {
		return createDirIfNotExists;
	}

	public void setCreateDirIfNotExists(final boolean aCreateDirInNotExists) {
		createDirIfNotExists = aCreateDirInNotExists;
	}

	public CopyFileAction(final String aToDir) {
		toDir = aToDir;
	}

	@Override
	public void perform(final File sourceFile) throws IOException {
		if (sourceFile.isDirectory()) {
			return;
		}
		File destFile = new File(toDir, sourceFile.getName());
		if (!checkForDestFileExists(sourceFile, destFile)) {
			return;
		}
		checkForDestDir();
		copy(sourceFile, destFile);
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info(String.format(FILE_COPIED_INFO, sourceFile.getName(), toDir));
		}
	}

	private boolean checkForDestFileExists(final File sourceFile, final File destFile) {
		if (destFile.exists()) {
			if (overwrite) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.info(String.format(FILE_OVERWRITE_INFO, destFile.getName()));
				}
			} else {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.info(String.format(FILE_SKIPED_INFO, sourceFile.getName(), toDir));
				}
				return false;
			}
		}
		return true;
	}

	private void checkForDestDir() {
		File destDir = new File(toDir);
		if ((!destDir.exists()) && createDirIfNotExists) {
			if (!destDir.mkdirs()) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error(String.format(CREATE_DIR_ERROR, destDir.getName()));
				}
				return;
			}
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				LOGGER.info(String.format(DIR_CREATED_INFO, destDir.getName()));
			}
		}
	}

	private void copy(final File src, final File dst) throws IOException {
		try (InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dst);) {
			byte[] buf = new byte[COPY_BUF_SIZE];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		}
	}

	@Override
	public FileAction cloneForHandleChildDir(final String aChildDir) {
		CopyFileAction res = new CopyFileAction(toDir + File.separator + aChildDir);
		res.createDirIfNotExists = createDirIfNotExists;
		res.overwrite = overwrite;
		return res;
	}
}