package ru.curs.showcase.util.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.w3c.dom.Document;

/**
 * Источник для XML документов. Может содержать данные в InputStream, в DOM
 * Document или в виде, пригодном для работы с SAX парсером.
 * 
 * @author den
 * 
 */
public class XMLSource {
	/**
	 * Имя проверяемого файла (если поток был получен из файловой системы).
	 */
	private String subjectName;

	/**
	 * Имя схемы для проверки.
	 */
	private String schemaName;
	/**
	 * Поток с XML данными.
	 */
	private InputStream inputStream;
	/**
	 * XML документ.
	 */
	private Document document;
	/**
	 * Экземпляр парсера для работы с InputStream.
	 */
	private SAXParser saxParser;

	/**
	 * Возвращает правильный преобразователь, анализируя свое содержимое.
	 * 
	 * @return - преобразователь.
	 */
	public XMLExtractor getExtractor() {
		if (document != null) {
			return new DocumentXMLExtractor();
		} else if (saxParser != null) {
			return new SAXExtractor();
		} else if (inputStream != null) {
			return new InputStreamXMLExtractor();
		}
		return null;
	}

	public XMLSource(final InputStream aInputStream, final SAXParser aSaxParser,
			final String aSchemaName) {
		super();
		inputStream = aInputStream;
		saxParser = aSaxParser;
		schemaName = aSchemaName;
	}

	public XMLSource(final Document aDocument, final String aSubjectName, final String aSchemaName) {
		super();
		document = aDocument;
		subjectName = aSubjectName;
		schemaName = aSchemaName;
	}

	public XMLSource(final Document aDocument, final String aSchemaName) {
		super();
		document = aDocument;
		schemaName = aSchemaName;
	}

	public XMLSource(final InputStream aInputStream, final String aSchemaName) {
		super();
		inputStream = aInputStream;
		schemaName = aSchemaName;
	}

	public XMLSource(final InputStream aInputStream, final String aSubjectName,
			final String aSchemaName) {
		super();
		inputStream = aInputStream;
		subjectName = aSubjectName;
		schemaName = aSchemaName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(final InputStream aInputStream) {
		inputStream = aInputStream;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(final Document aDocument) {
		document = aDocument;
	}

	public SAXParser getSaxParser() {
		return saxParser;
	}

	public void setSaxParser(final SAXParser aSaxParser) {
		saxParser = aSaxParser;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(final String aSubjectName) {
		subjectName = aSubjectName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(final String aSchemaName) {
		schemaName = aSchemaName;
	}
}
