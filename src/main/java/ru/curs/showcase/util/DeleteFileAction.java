package ru.curs.showcase.util;

import java.io.*;

/**
 * Действие для удаления файлов.
 * 
 * @author den
 * 
 */
public class DeleteFileAction implements FileAction {

	private static final String DELETE_ERROR = "Ошибка при удалении файла '%s'";

	@Override
	public void perform(final File aFile) throws IOException {
		if (aFile.exists()) {
			boolean res = aFile.delete();
			if (stopOnError && (!res)) {
				throw new IOException(String.format(DELETE_ERROR, aFile.getName()));
			}
		}
	}

	/**
	 * Признак того, что в случае ошибки удаления нужно останавливать процесс.
	 */
	private boolean stopOnError = true;

	@Override
	public FileAction cloneForHandleChildDir(final String aChildDir) {
		DeleteFileAction res = new DeleteFileAction();
		res.stopOnError = stopOnError;
		return res;
	}

	public boolean isStopOnError() {
		return stopOnError;
	}

	public void setStopOnError(final boolean aStopOnError) {
		stopOnError = aStopOnError;
	}

}
