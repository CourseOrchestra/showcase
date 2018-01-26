package ru.curs.showcase.util.xml;

import java.io.File;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.*;

/**
 * Источник схем из userdata.
 * 
 * @author den
 * 
 */
public final class UserDataXSDSource implements XSDSource {

	@Override
	public Schema getSchema(final String sourceName) throws SAXException {
		String xsdFullFileName =
			String.format("%s/%s/%s", UserDataUtils.getUserDataCatalog(),
					UserDataUtils.SCHEMASDIR, sourceName);
		File file = new File(xsdFullFileName);
		if (!file.exists()) {
			// file =
			// new File(String.format("%s/%s/%s",
			// AppInfoSingleton.getAppInfo().getUserdataRoot()
			// + "/" + UserDataUtils.GENERAL_RES_ROOT, UserDataUtils.SCHEMASDIR,
			// sourceName));

			File fileRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
			file =
				new File(String.format("%s/%s/%s", AppInfoSingleton.getAppInfo().getUserdataRoot()
						+ "/" + "common.sys", UserDataUtils.SCHEMASDIR, sourceName));
			if (!(file.exists())) {
				File[] files = fileRoot.listFiles();
				for (File f : files)
					if (f.getName().startsWith("common.") && !("common.sys".equals(f.getName()))) {
						File fileN =
							new File(String.format("%s/%s/%s", AppInfoSingleton.getAppInfo()
									.getUserdataRoot() + "/" + f.getName(),
									UserDataUtils.SCHEMASDIR, sourceName));
						if (fileN.exists()) {
							file = fileN;
							break;
						}
					}
			}

			if (!file.exists()) {
				throw new SettingsFileOpenException(xsdFullFileName, SettingsFileType.SCHEMA);
			}
		}
		// передавать InputStream и URL нельзя, т.к. в этом случае парсер не
		// находит вложенных схем!
		return XMLUtils.createSchemaForFile(file);
	}

	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.USER;
	}

}
