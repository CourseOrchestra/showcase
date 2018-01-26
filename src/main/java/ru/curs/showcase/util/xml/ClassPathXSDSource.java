package ru.curs.showcase.util.xml;

import java.io.File;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.*;

/**
 * Источник схем в classpath.
 * 
 * @author den
 * 
 */
public class ClassPathXSDSource implements XSDSource {

	@Override
	public Schema getSchema(final String aFileName) throws SAXException {
		String xsdFullFileName = String.format("%s/%s", UserDataUtils.SCHEMASDIR, aFileName);
		File file =
			new File(AppInfoSingleton.getAppInfo().getWebAppPath() + "/WEB-INF/classes/"
					+ xsdFullFileName);
		if (!file.exists()) {
			throw new SettingsFileOpenException(xsdFullFileName, SettingsFileType.SCHEMA);
		}
		return XMLUtils.createSchemaForFile(file);
	}

	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.SOLUTION;
	}
}
