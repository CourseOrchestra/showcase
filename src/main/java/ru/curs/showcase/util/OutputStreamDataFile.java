package ru.curs.showcase.util;

import java.io.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SizeEstimate;

/**
 * Файл, в котором данные хранятся в ByteArrayOutputStream.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class OutputStreamDataFile extends DataFile<ByteArrayOutputStream> implements SizeEstimate {

	public OutputStreamDataFile() {
		super();
	}

	public OutputStreamDataFile(final ByteArrayOutputStream aData, final String aFileName) {
		super(aData, aFileName);
	}

	public final String getTextData() throws UnsupportedEncodingException {
		if (isTextFile()) {
			return getData().toString(getEncoding());
		}
		return null;
	}

	public final void setTextData(final String aData) {
		// парный метод, не используется
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		if (getName() != null) {
			result += getName().getBytes().length;
		}
		if (getData() != null) {
			result += getData().size();
		}
		return result;
	}
}
