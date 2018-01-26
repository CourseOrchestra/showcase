package ru.curs.showcase.util.xml;

import java.io.*;
import java.sql.*;

import javax.xml.bind.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import net.sf.saxon.lib.NamespaceConstant;

import org.slf4j.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

import com.google.gson.JsonElement;

/**
 * Реализует обработку XML (в частности, выполнение XSLT-преобразования).
 * 
 */
public final class XMLUtils {

	public static Schema createSchemaForFile(final File file) throws SAXException {
		SchemaFactory schemaFactory = createSchemaFactory();
		return schemaFactory.newSchema(file);
	}

	public static final String XML_VERSION_1_0_ENCODING_UTF_8 =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	public static final String XSL_MARKER = "XSL";

	protected static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);

	/**
	 * Преобразует объект в XML документ.
	 * 
	 * @param object
	 *            - объект.
	 * @return - документ.
	 */
	public static Document objectToXML(final Object object) {
		DocumentBuilder builder = createBuilder();
		Document doc = builder.newDocument();
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, TextUtils.DEF_ENCODING);
			m.marshal(object, doc);
			return doc;
		} catch (JAXBException e) {
			throw new ServerLogicError(e);
		}
	}

	/**
	 * Преобразует XML документ в объект.
	 * 
	 * @param node
	 *            - элемент XML.
	 * @param objectClass
	 *            - класс требуемого объекта.
	 * @return - требуемый объект.
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Object xmlToObject(final Node node, final Class objectClass) {
		try {
			JAXBContext jc = JAXBContext.newInstance(objectClass);
			Unmarshaller um = jc.createUnmarshaller();
			Object res = um.unmarshal(node);
			return res;
		} catch (JAXBException e) {
			throw new ServerLogicError(e);
		}
	}

	private static final String XSLT_ERROR = "Ошибка при выполнении XSLT-преобразования: ";

	/**
	 * Идентификатор схемы версии 1.1.
	 */
	public static final String XML_SCHEMA_V1_1 = "http://www.w3.org/XML/XMLSchema/v1.1";

	public static final String JAXP_SCHEMA_LANGUAGE =
		"http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	public static final String JAXP_SCHEMA_SOURCE =
		"http://java.sun.com/xml/jaxp/properties/schemaSource";

	private XMLUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Получает "входящий" SQLXML по org.w3c.dom.Document.
	 * 
	 * @param doc
	 *            org.w3c.dom.Document
	 * @param con
	 *            Connection в котором будет создан SQLXML
	 * 
	 * @return SQLXML
	 * 
	 * @throws SQLException
	 *             В случае ошибки
	 * @throws TransformerException
	 *             В случае ошибки
	 * @throws IOException
	 */
	public static SQLXML domToSQLXML(final org.w3c.dom.Document doc, final Connection con)
			throws SQLException, TransformerException, IOException {
		SQLXML sqlxml = con.createSQLXML();
		Result result = sqlxml.setResult(null);
		Transformer tr = XSLTransformerPoolFactory.getInstance().acquire();
		try {
			tr.transform(new DOMSource(doc), result);
		} finally {
			XSLTransformerPoolFactory.getInstance().release(tr);
		}
		return sqlxml;
	}

	/**
	 * Стандартная процедура создания XML Document builder.
	 * 
	 * @return - DocumentBuilder.
	 */
	public static DocumentBuilder createBuilder() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		return builder;
	}

	public static DocumentBuilder createBuilderWithoutNamespace() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		return builder;
	}

	/**
	 * Стандартная функция для создания SAX XML Parser.
	 * 
	 * @return парсер.
	 */
	public static SAXParser createSAXParser() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		SAXParser parser;
		try {
			factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			parser = factory.newSAXParser();
		} catch (SAXException | ParserConfigurationException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		return parser;
	}

	public static void
			setupStdTransformerParams(final Transformer tr, final boolean excludeXmlDecl) {
		if (excludeXmlDecl) {
			tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		}
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.ENCODING, TextUtils.DEF_ENCODING);
		// tr.setOutputProperty(OutputKeys.METHOD, "xml"); //Andrey
		// закоментировал это свойство, так как при трансформации с out method =
		// xml пустые тэги div были самозакрывающиеся, что не поддерживается в
		// спецификации html
	}

	/**
	 * Функция сохранения строки в XML файл. Функция сначала преобразует строку
	 * в XML документ в целях проверки.
	 * 
	 * @param content
	 *            - содержимое строки.
	 * @param filename
	 *            - имя файла для сохранения.
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public static void stringToXMLFile(final String content, final String filename)
			throws SAXException, IOException, TransformerException {
		Document doc = stringToDocument(content);
		Transformer tr = XSLTransformerPoolFactory.getInstance().acquire();
		try {
			setupStdTransformerParams(tr, true);
			tr.transform(new DOMSource(doc), new StreamResult(new File(filename)));
		} finally {
			XSLTransformerPoolFactory.getInstance().release(tr);
		}
	}

	public static Document stringToDocument(final String content) throws SAXException, IOException {
		if ((content == null) || content.isEmpty()) {
			return null;
		}
		InputSource is = new InputSource(new StringReader(content));
		DocumentBuilder db = createBuilder();
		Document doc = db.parse(is);
		return doc;
	}

	public static Document stringToDocumentWithoutNamespace(final String content)
			throws SAXException, IOException {
		if ((content == null) || content.isEmpty()) {
			return null;
		}
		InputSource is = new InputSource(new StringReader(content));
		DocumentBuilder db = createBuilderWithoutNamespace();
		Document doc = db.parse(is);
		return doc;
	}

	public static String xsltTransform(final SQLXML sqlxml, final String xsltFileName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = XSLTransformerPoolFactory.getInstance().acquire(xsltFileName);
			try {
				setupStdTransformerParams(tr, true);
				tr.transform(sqlxml.getSource(DOMSource.class), new StreamResult(baos));
			} finally {
				XSLTransformerPoolFactory.getInstance().release(tr, xsltFileName);
			}
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (IOException | TransformerException | SQLException e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage(), e);
		}
	}

	public static String xsltTransform(final org.w3c.dom.Document doc, final String xsltFileName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = XSLTransformerPoolFactory.getInstance().acquire(xsltFileName);
			try {
				setupStdTransformerParams(tr, true);
				tr.transform(new DOMSource(doc), new StreamResult(baos));
			} finally {
				XSLTransformerPoolFactory.getInstance().release(tr, xsltFileName);
			}
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (IOException | TransformerException e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage(), e);
		}
	}

	public static String xsltTransform(final org.w3c.dom.Document doc,
			final DataFile<InputStream> transform) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			logXSLInput(doc, transform.getName());
			Transformer tr = null;
			if (transform.getData() != null) {
				tr =
					XSLTransformerPoolFactory.getTransformerFactory().newTransformer(
							new StreamSource(transform.getData()));
			} else {
				tr = XSLTransformerPoolFactory.getInstance().acquire();
			}
			try {
				setupStdTransformerParams(tr, true);
				tr.transform(new DOMSource(doc), new StreamResult(baos));
			} finally {
				if (transform.getData() == null) {
					XSLTransformerPoolFactory.getInstance().release(tr);
				}
			}
			final String result = baos.toString(TextUtils.DEF_ENCODING);
			logXSLOutput(transform.getName(), result);
			return result;
		} catch (IOException | TransformerException e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage(), e);
		}
	}

	public static String documentToString(final Document doc) {
		if (doc == null) {
			return null;
		}
		return xsltTransform(doc, (String) null);
	}

	public static String xsltTransform(final InputStream is, final String xsltFileName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = XSLTransformerPoolFactory.getInstance().acquire(xsltFileName);
			try {
				setupStdTransformerParams(tr, true);
				tr.transform(new StreamSource(is), new StreamResult(baos));
			} finally {
				XSLTransformerPoolFactory.getInstance().release(tr, xsltFileName);
			}
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (IOException | TransformerException e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage(), e);
		}
	}

	public static String
			xsltTransform(final InputStream is, final DataFile<InputStream> transform) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream source = is;
			source = (InputStream) logXSLInput(source, transform.getName());

			Transformer tr = null;
			if (transform.getData() != null) {
				tr =
					XSLTransformerPoolFactory.getTransformerFactory().newTransformer(
							new StreamSource(transform.getData()));
			} else {
				tr = XSLTransformerPoolFactory.getInstance().acquire();
			}
			try {
				setupStdTransformerParams(tr, true);
				tr.transform(new StreamSource(source), new StreamResult(baos));
			} finally {
				if (transform.getData() == null) {
					XSLTransformerPoolFactory.getInstance().release(tr);
				}
			}

			String result = baos.toString(TextUtils.DEF_ENCODING);
			logXSLOutput(transform.getName(), result);
			return result;
		} catch (IOException | TransformerException e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage(), e);
		}
	}

	private static Object logXSLInput(final Object source, final String xsltFileName) {
		String value = null;
		Object sourceCopy = source;

		if ((!(LOGGER.isInfoEnabled() && AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()))
				|| (xsltFileName == null)) {
			return sourceCopy;
		}
		if (source instanceof InputStream) {
			StreamConvertor convertor;
			try {
				convertor = new StreamConvertor((InputStream) source);
			} catch (IOException e) {
				throw new ServerObjectCreateCloseException(e);
			}
			value = streamToString(convertor.getCopy());
			sourceCopy = convertor.getCopy();
		} else if (source instanceof Document) {
			value = documentToString((Document) source);
		}

		Marker marker = MarkerFactory.getDetachedMarker(XSL_MARKER);
		marker.add(HandlingDirection.INPUT.getMarker());
		marker.add(MarkerFactory.getMarker(String.format("xslTransform=%s", xsltFileName)));
		LOGGER.info(marker, value);
		return sourceCopy;
	}

	private static void logXSLOutput(final String xsltFileName, final String result) {
		if ((xsltFileName == null) || (!AppInfoSingleton.getAppInfo().isEnableLogLevelInfo())) {
			return;
		}
		Marker marker = MarkerFactory.getDetachedMarker(XSL_MARKER);
		marker.add(HandlingDirection.OUTPUT.getMarker());
		marker.add(MarkerFactory.getMarker(String.format("xslTransform=%s", xsltFileName)));
		LOGGER.info(marker, result);
	}

	/**
	 * В отличие от аналогичной функции в TextUtils для преобразования
	 * использует стандартную XSTL трансформацию. Отсюда как минимум 2
	 * следствия: 1) на вход должен подаваться XML файл. 2) из файла будет
	 * убрана преамбула при ее наличии.
	 */
	public static String streamToString(final InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = XSLTransformerPoolFactory.getInstance().acquire();
			try {
				setupStdTransformerParams(tr, true);
				tr.transform(new StreamSource(is), new StreamResult(baos));
			} finally {
				XSLTransformerPoolFactory.getInstance().release(tr);
			}
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (IOException | TransformerException e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage(), e);
		}
	}

	/**
	 * Выполняет XSLT-преобразование для грида.
	 * 
	 * @param doc
	 *            org.w3c.dom.Document для преобразования
	 * 
	 * @return OutputStream с преобразованным XML
	 * 
	 */
	public static ByteArrayOutputStream xsltTransformForGrid(final org.w3c.dom.Document doc) {
		try {
			Transformer tr = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			tr = XSLTransformerPoolFactory.getInstance().acquire(UserDataUtils.GRIDDATAXSL);
			try {
				setupStdTransformerParams(tr, false);
				tr.transform(new DOMSource(doc), new StreamResult(baos));
			} finally {
				XSLTransformerPoolFactory.getInstance().release(tr, UserDataUtils.GRIDDATAXSL);
			}
			return baos;
		} catch (TransformerFactoryConfigurationError | IOException | TransformerException e) {
			throw new XSLTTransformException(
					"Ошибка при выполнении XSLT-преобразования для таблицы: " + e.getMessage(), e);
		}
	}

	/**
	 * Schema full checking feature id
	 * (http://apache.org/xml/features/validation/schema-full-checking).
	 */
	private static final String SCHEMA_FULL_CHECKING_FEATURE_ID =
		"http://apache.org/xml/features/validation/schema-full-checking";

	/**
	 * Honour all schema locations feature id
	 * (http://apache.org/xml/features/honour-all-schemaLocations).
	 */
	private static final String HONOUR_ALL_SCHEMA_LOCATIONS_ID =
		"http://apache.org/xml/features/honour-all-schemaLocations";

	/**
	 * Validate schema annotations feature id
	 * (http://apache.org/xml/features/validate-annotations).
	 */
	private static final String VALIDATE_ANNOTATIONS_ID =
		"http://apache.org/xml/features/validate-annotations";

	/**
	 * Generate synthetic schema annotations feature id
	 * (http://apache.org/xml/features/generate-synthetic-annotations).
	 */
	private static final String GENERATE_SYNTHETIC_ANNOTATIONS_ID =
		"http://apache.org/xml/features/generate-synthetic-annotations";

	/**
	 * Создает стандартную SchemaFactory.
	 * 
	 * @return - SchemaFactory.
	 */
	static SchemaFactory createSchemaFactory() {
		boolean schemaFullChecking = false;
		boolean honourAllSchemaLocations = false;
		boolean validateAnnotations = false;
		boolean generateSyntheticAnnotations = false;

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XML_SCHEMA_V1_1);
		// XMLConstants.W3C_XML_SCHEMA_NS_URI

		try {
			schemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
			schemaFactory.setFeature(HONOUR_ALL_SCHEMA_LOCATIONS_ID, honourAllSchemaLocations);
			schemaFactory.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
			schemaFactory.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS_ID,
					generateSyntheticAnnotations);
		} catch (SAXNotRecognizedException | SAXNotSupportedException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		return schemaFactory;
	}

	/**
	 * Настраивает название класса "правильной" SchemaFactory - с поддержкой
	 * XML_SCHEMA_V1_1.
	 */
	public static void setupSchemaFactory() {
		// System.setProperty("jaxp.debug", "jaxp.debug");
		System.setProperty("javax.xml.validation.SchemaFactory" + ":" + XML_SCHEMA_V1_1,
				"org.apache.xerces.jaxp.validation.XMLSchema11Factory");
	}

	/**
	 * Настраивает "правильный" трансформер XSL - SAXON.
	 */
	public static void setupTransformer() {
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_SAXON,
				"net.sf.saxon.xpath.XPathFactoryImpl");

		System.setProperty("javax.xml.stream.XMLOutputFactory",
				"com.sun.xml.internal.stream.XMLOutputFactoryImpl");

	}

	/**
	 * Выполняет XSD-проверку пользовательского документа.
	 * 
	 * @param doc
	 *            org.w3c.dom.Document для проверки
	 * @param schemaFile
	 *            Имя файла XSD-схемы
	 * 
	 */
	public static void
			xsdValidateUserData(final org.w3c.dom.Document doc, final String schemaFile) {
		XMLValidator validator = new XMLValidator(new UserDataXSDSource());
		validator.validate(new XMLSource(doc, schemaFile));
	}

	/**
	 * Выполняет XSD-проверку пользовательского документа.
	 * 
	 * @param parser
	 *            SAXParser
	 * @param is
	 *            InputStream для проверки
	 * @param schemaFile
	 *            Имя файла XSD-схемы
	 * 
	 */
	public static void xsdValidateUserData(final SAXParser parser, final InputStream is,
			final String schemaFile) {
		XMLValidator validator = new XMLValidator(new UserDataXSDSource());
		validator.validate(new XMLSource(is, parser, schemaFile));
	}

	/**
	 * Функция проверки XML документа, позволяющая повторное чтение из
	 * переданного InputStream. Повторное чтение обеспечивается с помощью
	 * клонирования InputStream.
	 * 
	 * @param stream
	 *            - входной поток с XML документом.
	 * @param schemaFile
	 *            - наименование файла схемы.
	 * @return - входной поток, из которого можно читать.
	 */
	public static InputStream xsdValidateAppDataSafe(final InputStream stream,
			final String schemaFile) {
		StreamConvertor duplicator;
		try {
			duplicator = new StreamConvertor(stream);
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		InputStream stream1 = duplicator.getCopy();
		InputStream stream2 = duplicator.getCopy();

		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(stream1, schemaFile));
		return stream2;
	}

	/**
	 * Выполняет XSD-проверку пользовательского документа.
	 * 
	 * @param is
	 *            InputStream для проверки
	 * @param schemaFile
	 *            Имя файла XSD-схемы
	 * 
	 */
	public static void xsdValidateUserData(final InputStream is, final String schemaFile) {
		XMLValidator validator = new XMLValidator(new UserDataXSDSource());
		validator.validate(new XMLSource(is, schemaFile));
	}

	/**
	 * Создает пустой документ с заданным именем корневого элемента.
	 * 
	 * @param rootTag
	 *            - имя корневого элемента.
	 * @return - документ.
	 */
	public static Document createEmptyDoc(final String rootTag) {
		return createBuilder().getDOMImplementation().createDocument("", rootTag, null);
	}

	/**
	 * Строит строку содержащую тэг с атрибутами для SAX парсера.
	 * 
	 * @param qname
	 *            - имя тэга.
	 * @param attrs
	 *            - атрибуты.
	 * @return - строка.
	 */
	public static String saxTagWithAttrsToString(final String qname, final Attributes attrs) {
		StringBuilder builder = new StringBuilder(" ");
		for (int i = 0; i < attrs.getLength(); i++) {
			String name = attrs.getQName(i);
			String value = attrs.getValue(i);
			builder.append(String.format("%s=\"%s\" ", name, value));
		}
		return "<" + qname + builder.toString() + ">";
	}

	/**
	 * Функция проверки XML документа, переданного посредством DataFile,
	 * позволяющая повторное чтение из переданного InputStream. Повторное чтение
	 * обеспечивается с помощью клонирования InputStream.
	 * 
	 * @param file
	 *            - файл с XML документом.
	 * @param schemaName
	 *            - наименование файла схемы.
	 */
	public static void xsdValidateAppDataSafe(final DataFile<InputStream> file,
			final String schemaName) {
		StreamConvertor duplicator;
		try {
			duplicator = new StreamConvertor(file.getData());
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		InputStream stream1 = duplicator.getCopy();
		file.setData(duplicator.getCopy());

		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(stream1, file.getName(), schemaName));
	}

	/**
	 * Функция, частично заменяющая служебные XML символы на обычные. Важное
	 * замечание: заменяются только XML, но не HTML символы!
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String xmlServiceSymbolsToNormal(final String original) {
		if (original == null) {
			return null;
		}
		String result = original;
		result = result.replace("&amp;", "&");
		result = result.replace("&quot;", "\"");
		result = result.replace("&apos;", "'");
		result = result.replace("&lt;", "<");
		result = result.replace("&gt;", ">");
		return result;
	}

	/**
	 * Функция, частично заменяющая служебные XML символы на обычные. Важное
	 * замечание: заменяются только XML, но не HTML символы!
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String xmlServiceSymbolsToNormalPartial(final String original) {
		if (original == null) {
			return null;
		}
		String result = original;
		result = result.replace("&lt;", "<");
		result = result.replace("&gt;", ">");
		return result;
	}

	/**
	 * Функция, частично заменяющая служебные XML символы на обычные. Важное
	 * замечание: заменяются только XML, но не HTML символы!
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String xmlServiceSymbolsToNormalWithoutLess(final String original) {
		if (original == null) {
			return null;
		}
		String result = original;
		result = result.replace("&amp;", "&");
		result = result.replace("&quot;", "\"");
		result = result.replace("&apos;", "'");
		// result = result.replace("&lt;", "<");
		result = result.replace("&gt;", ">");
		return result;
	}

	/**
	 * Функция, частично заменяющая служебные XML символы на обычные (кроме
	 * двойных кавычек). Важное замечание: заменяются только XML, но не HTML
	 * символы!
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String xmlServiceSymbolsToNormalWithoutDoubleQuotes(final String original) {
		if (original == null) {
			return null;
		}
		String result = original;
		result = result.replace("&amp;", "&");
		// result = result.replace("&quot;", "\"");
		result = result.replace("&apos;", "'");
		result = result.replace("&lt;", "<");
		result = result.replace("&gt;", ">");
		return result;
	}

	/**
	 * Функция, частично заменяющая служебные XML символы на обычные (кроме
	 * двойных кавычек). Важное замечание: заменяются только XML, но не HTML
	 * символы!
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String
			xmlServiceSymbolsToNormalWithoutDoubleQuotesAndLess(final String original) {
		if (original == null) {
			return null;
		}
		String result = original;
		result = result.replace("&amp;", "&");
		// result = result.replace("&quot;", "\"");
		result = result.replace("&apos;", "'");
		// result = result.replace("&lt;", "<");
		result = result.replace("&gt;", ">");
		return result;
	}

	public static Schema createSchemaForStream(final InputStream aData) throws SAXException {
		SchemaFactory schemaFactory = createSchemaFactory();
		return schemaFactory.newSchema(new StreamSource(aData));
	}

	private static String[][] escapeTag = {
			{ "_x0401_", "Ё" }, { "_x0451_", "ё" }, { "_x007e_", "~" }, { "_x0021_", "!" },
			{ "_x0040_", "@" }, { "_x0023_", "#" }, { "_x0024_", "$" }, { "_x0025_", "%" },
			{ "_x005e_", "^" }, { "_x0026_", "&" }, { "_x002a_", "*" }, { "_x0028_", "(" },
			{ "_x0029_", ")" }, { "_x002b_", "+" }, { "_x002d_", "-" }, { "_x003d_", "=" },
			{ "_x007b_", "{" }, { "_x007d_", "}" }, { "_x003a_", ":" }, { "_x0022_", "\"" },
			{ "_x007c_", "|" }, { "_x003b_", ";" }, { "_x0027_", "'" }, { "_x005c_", "\\" },
			{ "_x003c_", "<" }, { "_x003e_", ">" }, { "_x003f_", "?" }, { "_x002c_", "," },
			{ "_x002e_", "." }, { "_x002f_", "/" }, { "_x0060_", "`" }, { "_x0020_", " " },
			{ "_y0051_", "№" }, { "_x2116_", "№" } };

	private static String[][] escapeTagFirstDigit = {
			{ "_x0030_", "0" }, { "_x0031_", "1" }, { "_x0032_", "2" }, { "_x0033_", "3" },
			{ "_x0034_", "4" }, { "_x0035_", "5" }, { "_x0036_", "6" }, { "_x0037_", "7" },
			{ "_x0038_", "8" }, { "_x0039_", "9" } };

	private static String[][] escapeValue = {
			{ "_x0026_", "&" }, { "_x003c_", "<" }, { "_x003e_", ">" }, { "_z0051_", "TV" },
			{ "_z0052_", "tv" } };

	/**
	 * Функция, заменяющая специальные символы в названиях XML тегов на
	 * служебные.
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String escapeTagXml(final String original) {
		String text = original;
		if ((text == null) || text.trim().isEmpty()) {
			return text;
		}
		if (Character.isDigit(text.charAt(0))) {
			for (int i = 0; i < escapeTagFirstDigit.length; i++) {
				if (escapeTagFirstDigit[i][1].equalsIgnoreCase(text.substring(0, 1))) {
					text = escapeTagFirstDigit[i][0] + text.substring(1);
					break;
				}
			}
		}
		for (int i = 0; i < escapeTag.length; i++) {
			while (text.indexOf(escapeTag[i][1]) != -1) {
				text = text.replace(escapeTag[i][1], escapeTag[i][0]);
			}
		}
		return text;
	}

	/**
	 * Функция, заменяющая служебные символы в названиях XML тегов на обычные.
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String unEscapeTagXml(final String original) {
		String text = original;
		if ((text == null) || text.trim().isEmpty()) {
			return text;
		}
		for (int i = 0; i < escapeTag.length; i++) {
			while (text.indexOf(escapeTag[i][0]) != -1) {
				text = text.replace(escapeTag[i][0], escapeTag[i][1]);
			}

			String repl = escapeTag[i][0].toUpperCase().replace("_X", "_x");
			while (text.indexOf(repl) != -1) {
				text = text.replace(repl, escapeTag[i][1]);
			}
		}
		for (int i = 0; i < escapeTagFirstDigit.length; i++) {
			while (text.indexOf(escapeTagFirstDigit[i][0]) != -1) {
				text = text.replace(escapeTagFirstDigit[i][0], escapeTagFirstDigit[i][1]);
			}
		}
		return text;
	}

	/**
	 * Функция, заменяющая специальные символы в содержимом XML тегов на
	 * служебные.
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String escapeValueXml(final String original) {
		String text = original;
		if ((text == null) || text.trim().isEmpty()) {
			return text;
		}
		for (int i = 0; i < escapeValue.length; i++) {
			while (text.indexOf(escapeValue[i][1]) != -1) {
				text = text.replace(escapeValue[i][1], escapeValue[i][0]);
			}
		}
		return text;
	}

	/**
	 * Функция, заменяющая служебные символы в содержимом XML тегов на обычные.
	 * 
	 * @param original
	 *            - исходная строка.
	 */
	public static String unEscapeValueXml(final String original) {
		String text = original;
		if ((text == null) || text.trim().isEmpty()) {
			return text;
		}
		for (int i = 0; i < escapeValue.length; i++) {
			while (text.indexOf(escapeValue[i][0]) != -1) {
				text = text.replace(escapeValue[i][0], escapeValue[i][1]);
			}
		}
		return text;
	}

	/**
	 * Функция, конвертирование строки XML в JSON.
	 * 
	 * @param xml
	 *            строка xml.
	 * @return строка в формате JSON, или пустая строка.
	 */
	@Deprecated
	public static String convertXmlToJson(final String xml) {
		if (xml == null || xml.isEmpty()) {
			return "";
		}
		try {
			SAXParser parser = createSAXParser();
			XmlToJsonSaxHandler handler = new XmlToJsonSaxHandler();
			InputStream in = TextUtils.stringToStream(xml);

			parser.parse(in, handler);
			JsonElement json = handler.getResult();
			if (json != null) {
				return json.toString();
			}
		} catch (SAXException | IOException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Error convert XML to JSON", e);
			}
		}
		return "";
	}
}
