package ru.curs.showcase.core.jython;

import java.io.InputStream;

/**
 * Входные атрибуты загрузки файла на сервер.
 * 
 * @author bogatov
 * 
 */
public class XFormUploadAttributes extends XFormDownloadAttributes {
	private String filename;
	private InputStream file;

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String aFilename) {
		this.filename = aFilename;
	}

	public InputStream getFile() {
		return file;
	}

	public void setFile(final InputStream aFile) {
		this.file = aFile;
	}

}
