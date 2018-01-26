package ru.curs.showcase.util.exception;

public class FileNameValidationException extends RuntimeException {
	private static final String ERROR_MES =
		"Недопустимые имена файлов. В папке пользовательских данных содержатся следующие недопустимые файлы, с именами начинающимися с \"common.\": '%s'";

	private static final long serialVersionUID = -7554997404313831091L;

	/**
	 * Список файлов через запятую.
	 */
	private final String files;

	public String getCommonStartedFiles() {
		return files;
	}

	public FileNameValidationException(final String files) {
		super(String.format(ERROR_MES, files));
		this.files = files;
	}

}
