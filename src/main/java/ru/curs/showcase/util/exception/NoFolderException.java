package ru.curs.showcase.util.exception;

public class NoFolderException extends RuntimeException {
	private static final String ERROR_MES = "Userdatas folder does not contain '%s' folder";

	private static final long serialVersionUID = -7664887404313831091L;

	/**
	 * Наименование директории
	 */

	private final String directory;

	public String getDirectory() {
		return directory;
	}

	public NoFolderException(final String directory) {
		super(String.format(ERROR_MES, directory));
		this.directory = directory;
	}

}
