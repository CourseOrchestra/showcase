package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.Test;

import ru.curs.showcase.app.api.BrowserType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Общий тестовый класс для мелких базовых объектов.
 * 
 * @author den
 * 
 */
public class BaseObjectsTest extends AbstractTestWithDefaultUserData {

	/**
	 * Проверка работы StreamConvertor.
	 * 
	 * @throws IOException
	 * @see ru.curs.showcase.util.StreamConvertor StreamConvertor
	 */
	@Test
	public void testStreamConvertor() throws IOException {
		InputStream is =
			UserDataUtils.loadUserDataToStream(String.format("%s//%s",
					SettingsFileType.DATAPANEL.getFileDir(), "a.xml"));

		StreamConvertor dup = new StreamConvertor(is);
		String data = XMLUtils.streamToString(dup.getCopy());
		checkForDP(data);

		data = XMLUtils.streamToString(dup.getCopy());
		checkForDP(data);

		ByteArrayOutputStream outStream = dup.getOutputStream();
		checkForDPWithXMLHeader(outStream);

		data = XMLUtils.streamToString(StreamConvertor.outputToInputStream(outStream));
		checkForDP(data);

		outStream = StreamConvertor.inputToOutputStream(dup.getCopy());
		checkForDPWithXMLHeader(outStream);
	}

	private void checkForDPWithXMLHeader(final ByteArrayOutputStream outStream)
			throws UnsupportedEncodingException {
		String data;
		data = outStream.toString(TextUtils.DEF_ENCODING);
		assertTrue(data.toLowerCase().startsWith(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8));
		assertTrue(data.endsWith("</" + GeneralXMLHelper.DP_TAG + ">"));
	}

	private void checkForDP(final String data) {
		assertTrue(data.startsWith("<" + GeneralXMLHelper.DP_TAG));
		assertTrue(data.trim().endsWith("</" + GeneralXMLHelper.DP_TAG + ">"));
	}

	/**
	 * Проверка работы BatchFileProcessor.
	 * 
	 * @see ru.curs.showcase.util.BatchFileProcessor BatchFileProcessor
	 * @see ru.curs.showcase.util.FileUtils#deleteDir FileUtils.deleteDir
	 * @throws IOException
	 */
	@Test
	public void testBatchFileProcessorAndDeleteDir() throws IOException {
		String sourceDir = "userdatas/default/css";
		String destDir = "tmp/css";

		File dir = new File(destDir);
		FileUtils.deleteDir(destDir);
		assertFalse(dir.exists());

		BatchFileProcessor fprocessor =
			new BatchFileProcessor(sourceDir, new RegexFilenameFilter("^[.].*", false));
		fprocessor.process(new CopyFileAction(destDir));

		assertTrue(dir.exists());
		dir = new File(destDir + "/level2");
		assertTrue(dir.exists());
		assertTrue(dir.isDirectory());
		File file = new File(destDir + "/level2/test.css");
		assertTrue(file.exists());
		assertTrue(file.isFile());

		FileUtils.deleteDir(destDir);
	}

	/**
	 * Проверка создания XMLSource.
	 */
	@Test
	public void testXMLSourceCreate() {
		XMLSource source = new XMLSource(FileUtils.loadClassPathResToStream(TEST_XML_FILE), "");
		assertNotNull(source.getInputStream());
		assertNull(source.getDocument());
		assertNull(source.getSaxParser());

		source =
			new XMLSource(FileUtils.loadClassPathResToStream(TEST_XML_FILE),
					XMLUtils.createSAXParser(), "");
		assertNotNull(source.getInputStream());
		assertNull(source.getDocument());
		assertNotNull(source.getSaxParser());

		source = new XMLSource(XMLUtils.createEmptyDoc("testTag"), "testFile", "");
		assertNull(source.getInputStream());
		assertNotNull(source.getDocument());
		assertNull(source.getSaxParser());
	}

	/**
	 * Проверка функции детектирования браузера.
	 */
	@Test
	public void testBrowserTypeDetection() {
		final String safariUA =
			"mozilla/5.0 (windows; u; windows nt 6.1; ru-ru) applewebkit/533.20.25 (khtml, like gecko) "
					+ "version/5.0.4 safari/533.20.27";
		final String chromeUA =
			"mozilla/5.0 (windows nt 6.1; wow64) applewebkit/534.24 (khtml, like gecko) chrome/11.0.696.71 safari/534.24";
		final String operaUA = "opera/9.80 (windows nt 6.1; u; ru) presto/2.8.131 version/11.11";
		final String ieUA =
			"mozilla/5.0 (compatible; MSIE 9.0; windows nt 6.1; wow64; trident/5.0)";

		assertEquals(BrowserType.SAFARI, BrowserType.detect(safariUA));
		assertEquals(BrowserType.CHROME, BrowserType.detect(chromeUA));
		assertEquals(BrowserType.OPERA, BrowserType.detect(operaUA));
		assertEquals(BrowserType.FIREFOX, BrowserType.detect(FIREFOX_UA));
		assertEquals(BrowserType.IE, BrowserType.detect(ieUA));

		assertEquals("5.0.4", BrowserType.detectVersion(safariUA));
		assertEquals("11.0.696.71", BrowserType.detectVersion(chromeUA));
		assertEquals("11.11", BrowserType.detectVersion(operaUA));
		assertEquals("4.0.1", BrowserType.detectVersion(FIREFOX_UA));
		assertEquals("9.0", BrowserType.detectVersion(ieUA));
	}

	@Test
	public void testTextDataFile() {
		DataFile<InputStream> file = new DataFile<InputStream>(null, "test.txt");
		assertTrue(file.isTextFile());
		file = new DataFile<InputStream>(null, "test.exe");
		assertFalse(file.isTextFile());
	}

	@Test
	public void testAddParamsToSQLTemplate() {
		Map<Integer, Object> params = new TreeMap<>();
		params.put(1, "first\n");
		params.put(2, 2);
		String value = "{call test_proc (?,?,?)}";

		value = SQLUtils.addParamsToSQLTemplate(value, params);

		assertEquals("test_proc N'first\n',2,null", value);
	}

	@Test
	public void testLogSettings() {
		final int logSize =
			Integer.parseInt(UserDataUtils.getGeneralOptionalProp(LastLogEvents.INTERNAL_LOG_SIZE));
		assertEquals(logSize, LastLogEvents.getMaxRecords());
	}

	@Test
	// !!! corrected
			public
			void testJSONObjectSerializer() {
		ObjectSerializer serializer = new JSONObjectSerializer();
		CompositeContext context = CompositeContext.createCurrent();
		String data = serializer.serialize(context);

		// String expected =
		// "{\n" + "  \"main\": \"current\",\n" +
		// "  \"additional\": \"current\",\n"
		// + "  \"session\": null,\n" + "  \"filter\": null\n" + "}";
		String expected =
			"{\n" + "  \"main\": \"current\",\n" + "  \"additional\": \"current\",\n"
					+ "  \"session\": null,\n" + "  \"filter\": null,\n"
					+ "  \"partialUpdate\": false" + "\n}";

		assertEquals(expected, data);
	}

	@Test
	public void testCopyFile() {
		File test =
			new File(AppInfoSingleton.getAppInfo().getWebAppPath() + "/WEB-INF/classes/ru/"
					+ FileUtils.GENERAL_PROPERTIES);
		if (test.exists()) {
			test.delete();
		}

		assertFalse(test.exists());
		try {
			boolean result =
				FileUtils.copyFile(AppInfoSingleton.getAppInfo().getWebAppPath()
						+ "/WEB-INF/classes/" + FileUtils.GENERAL_PROPERTIES, test.getParent());
			assertTrue(result);
			assertTrue(test.exists());
		} finally {
			if (test.exists()) {
				test.delete();
			}
		}
	}

	@Test
	public void testCopyNonExistsFile() {
		boolean result =
			FileUtils.copyFile("fake.file", AppInfoSingleton.getAppInfo().getWebAppPath());
		assertFalse(result);
	}

	@Test
	public void testExcelFile() {
		ExcelFile excel = new ExcelFile();
		excel.setName("test.xls");

		assertTrue(excel.isTextFile());
		assertFalse(excel.isXMLFile());
	}

}
