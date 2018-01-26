package ru.curs.showcase.util;

import java.io.*;

import javax.xml.bind.annotation.*;

/**
 * Класс файла Excel. Включает содержимое файла и его имя.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExcelFile extends OutputStreamDataFile {
	/**
	 * Имя результирующего файла по умолчанию.
	 */
	private static final String DEF_FILENAME = "table";

	public ExcelFile(final ByteArrayOutputStream aData, final String fileName,
			final String fileExtension) throws UnsupportedEncodingException {

		super(aData,
				String.format("%s.%s", fileName != null ? fileName : DEF_FILENAME, fileExtension));

	}

	public ExcelFile() {
		super();
	}

	@Override
	public boolean isTextFile() {
		return ".xls".equalsIgnoreCase(TextUtils.extractFileExt(getName()));
	}
}
