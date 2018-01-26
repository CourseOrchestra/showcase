package ru.curs.showcase.util.xml;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementProc;
import ru.curs.showcase.util.*;

/**
 * Класс для трансформации пользовательских XML c проверкой схемы.
 * 
 * @author den
 * 
 */
public final class UserXMLTransformer {

	/**
	 * Исходный поток.
	 */
	private OutputStreamDataFile subject;

	/**
	 * Исходный файл в виде строки.
	 */
	private String strSubject;

	/**
	 * Процедура с описанием трансформации и схемы.
	 */
	private DataPanelElementProc proc;

	/**
	 * Результат преобразования (если оно произошло).
	 */
	private InputStream result = null;

	/**
	 * Используемая трансформация.
	 */
	private final DataFile<InputStream> transform;

	private final XSDSource xsdSource;

	/**
	 * Проверяет исходник и трансформирует его если в процедуре трансформации
	 * описаны соответствующие схема и файл трансформации.
	 * 
	 * @throws IOException
	 */
	public void checkAndTransform() throws IOException {
		if ((subject == null) || (proc == null)) {
			return;
		}

		if (proc.getSchemaName() != null) {
			Document doc = checkForXMLandCreateDoc();
			XMLValidator validator = new XMLValidator(xsdSource);
			validator.validate(new XMLSource(doc, subject.getName(), proc.getSchemaName()));
		}
		if (proc.getTransformName() != null) {
			InputStream is = StreamConvertor.outputToInputStream(subject.getData());
			String res = XMLUtils.xsltTransform(is, transform);
			result = TextUtils.stringToStream(res);
		}
	}

	private Document checkForXMLandCreateDoc() throws IOException {
		DocumentBuilder db = XMLUtils.createBuilder();
		Document doc = null;
		InputStream is = StreamConvertor.outputToInputStream(subject.getData());
		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			throw new NotXMLException(e, subject.getName());
		}
		return doc;
	}

	public OutputStreamDataFile getSubject() {
		return subject;
	}

	public UserXMLTransformer(final OutputStreamDataFile aSubject,
			final DataPanelElementProc aProc, final DataFile<InputStream> aTransform,
			final XSDSource aXSDSource) {
		super();
		subject = aSubject;
		proc = aProc;
		transform = aTransform;
		xsdSource = aXSDSource;
	}

	public UserXMLTransformer(final String aSubject, final DataPanelElementProc aProc,
			final DataFile<InputStream> aTransform, final XSDSource aXSDSource) throws IOException {
		super();
		strSubject = aSubject;
		if (aSubject != null) {
			ByteArrayOutputStream os =
				StreamConvertor.inputToOutputStream(TextUtils.stringToStream(aSubject));
			subject = new OutputStreamDataFile(os, "данные формы");
		}
		transform = aTransform;
		proc = aProc;
		xsdSource = aXSDSource;
	}

	public void setSubject(final OutputStreamDataFile aSubject) {
		subject = aSubject;
	}

	public DataPanelElementProc getProc() {
		return proc;
	}

	public void setProc(final DataPanelElementProc aProc) {
		proc = aProc;
	}

	/**
	 * Возвращает результат преобразования файла или конвертирует в нужный
	 * формат входной файл в случае, если преобразование не задано.
	 * 
	 * @return - выходной файл.
	 */
	public DataFile<InputStream> getInputStreamResult() {
		if (result != null) {
			return new DataFile<InputStream>(result, subject.getName());
		} else {
			return new DataFile<InputStream>(
					StreamConvertor.outputToInputStream(subject.getData()), subject.getName());
		}
	}

	/**
	 * Возвращает результат преобразования файла или конвертирует в нужный
	 * формат входной файл в случае, если преобразование не задано.
	 * 
	 * @return - выходной файл.
	 */
	public OutputStreamDataFile getOutputStreamResult() throws IOException {
		if (result != null) {
			return new OutputStreamDataFile(StreamConvertor.inputToOutputStream(result),
					subject.getName());
		} else {
			return subject;
		}
	}

	/**
	 * Возвращает результат преобразования файла или конвертирует в нужный
	 * формат входной файл в случае, если преобразование не задано.
	 * 
	 * @return - выходной файл.
	 */
	public String getStringResult() throws IOException {
		if (result != null) {
			return TextUtils.streamToString(result);
		} else {
			return strSubject;
		}
	}
}
