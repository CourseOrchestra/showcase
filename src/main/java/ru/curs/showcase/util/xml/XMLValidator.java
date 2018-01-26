package ru.curs.showcase.util.xml;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Валидатор схем, позволяющий настроить месторасположение схем и тип исходных
 * данных.
 * 
 * @author den
 * 
 */
public class XMLValidator {
	/**
	 * Источник для схем.
	 */
	private XSDSource xsdSource;

	/**
	 * Функция проверки XML (Template Method). Важно: нельзя ловить
	 * SettingsFileOpenException!
	 * 
	 * @param source
	 *            - источник XML данных.
	 */
	public void validate(final XMLSource source) {
		try {
			Validator validator = createValidator(source);
			Source prepared = prepareSource(source);
			synchronized (AppInfoSingleton.getAppInfo()) {
				validator.validate(prepared);
			}
		} catch (SAXException e) {
			handleException(source, e);
		} catch (IOException e) {
			handleException(source, e);
		}
	}

	private void handleException(final XMLSource source, final Exception e) {
		ExceptionType exType = xsdSource.getExceptionType();
		if (source.getSubjectName() != null) {
			throw new XSDValidateException(exType, e, source.getSubjectName(),
					source.getSchemaName());
		} else {
			throw new XSDValidateException(exType, e, source.getSchemaName());
		}
	}

	private Source prepareSource(final XMLSource aSource) throws SAXException {
		return aSource.getExtractor().extract(aSource); // TODO некрасиво
	}

	private Validator createValidator(final XMLSource aXMLSource) throws SAXException {
		Schema schemaXSD = xsdSource.getSchema(aXMLSource.getSchemaName());
		return schemaXSD.newValidator();
	}

	public XMLValidator(final XSDSource aXsdSource) {
		super();
		xsdSource = aXsdSource;
	}

	public XSDSource getXsdSource() {
		return xsdSource;
	}

	public void setXsdSource(final XSDSource aXsdSource) {
		xsdSource = aXsdSource;
	}
}
