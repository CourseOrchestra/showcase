package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.util.XMLJSONConverter;

/**
 * Набор тестов класса XMLJSONConverter.
 * 
 * @author bogatov
 * 
 * 
 */
public class XMLJSONConverterTest {

	@Test
	public void xmlToJson() throws Exception {
		String xml =
			"<elem1 attr1=\"True\" attr0=\"\" attr3=\"Русский текст\" attr4=\"1\" attr5=\"3.14\" attr2=\"Text\">"
					+ "<elem2 sorted=\"True\">5</elem2>"
					+ "<elem3 sorted=\"True\">Снова текст</elem3>"
					+ "<elem4 sorted=\"True\" attr6=\"False\"/>"
					+ "<element5 attr7=\"1\"/><element5 attr8=\"2\"/>"
					+ "<element6 attr9=\"Ещё текст\"/>"
					+ "<element7/>"
					+ "<expr>[question].[current].[105]</expr>" + "</elem1>";
		// String xml =
		// "<not>" + "<expr>[question].[current].[105]</expr>" +
		// "<expr>365.0</expr>" + "</not>";
		String result = XMLJSONConverter.xmlToJson(xml);
		assertNotNull(result);
		assertEquals(
				result.toString(),
				"{\"elem1\":{"
						+ "\"element6\":{\"@attr9\":\"Ещё текст\"},"
						+ "\"element7\":\"\",\"@attr0\":\"\","
						+ "\"element5\":[{\"@attr7\":\"1\"},{\"@attr8\":\"2\"}],"
						+ "\"@attr2\":\"Text\",\"@attr1\":\"True\","
						+ "\"expr\":\"[question].[current].[105]\","
						+ "\"@attr4\":\"1\",\"@attr3\":\"Русский текст\",\"@attr5\":\"3.14\","
						+ "\"#sorted\":[{\"elem2\":\"5\"},{\"elem3\":\"Снова текст\"},{\"elem4\":{\"@attr6\":\"False\"}}]}}");
		// System.out.println(result.toString());
	}

	@Test
	public void jsonToXml() throws Exception {
		String json =
			"{\"elem1\":"
					+ "{\"@attr0\":None,"
					+ "\"@attr1\":True,"
					+ "\"@attr2\":\"Text\","
					+ "\"@attr3\":\"Русский текст\","
					+ "\"@attr4\":1,"
					+ "\"@attr5\":3.14,"
					+ "\"#text\":\"Тоже текст\","
					+ "\"#sorted\":[{\"elem2\":5},"
					+ "{\"element222\":[True, None]},"
					+ "{\"elem3\":\"Снова текст\"},"
					+ "{\"element33\":False},"
					+ "{\"element333\":None},"
					+ "{\"element44\":[{\"@attr74\":1, \"#text\": \"Test2\"},"
					+ " {\"@attr84\":2, \"@attr94\":None, \"@attr64\":\"True\"}]},"
					+ "{\"element444\":[111, 444]},"
					+ "{\"elem4\":{"
					+ "\"@attr6\":False, \"@attr10\":\"False\", \"@attr11\":\"true\", \"@attr12\":\"None\""
					+ "}" + "}]," + "\"element7\":None,"
					+ "\"element5\":[{\"@attr7\":1}, {\"@attr8\":2, \"#text\": \"Test3\"}],"
					+ "\"element555\":[None, False]," + "\"element6\":{\"@attr9\":\"Ещё текст\"},"
					+ "\"element8\":True" + "}" + "}";

		String result = XMLJSONConverter.jsonToXml(json);
		result = result.replaceAll("(\\s){2,}", " ");
		// System.out.println(result);

		assertNotNull(result);
		assertEquals(
				result.toString(),
				"<elem1 attr0=\"\" attr1=\"True\" attr2=\"Text\" attr3=\"Русский текст\" attr4=\"1\" attr5=\"3.14\">"
						+ "Тоже текст"
						+ "<elem2>5</elem2>"
						+ "<element222>True</element222>"
						+ "<element222/>"
						+ "<elem3>Снова текст</elem3>"
						+ "<element33>False</element33>"
						+ "<element333/>"
						+ "<element44 attr74=\"1\">Test2</element44>"
						+ "<element44 attr64=\"True\" attr84=\"2\" attr94=\"\"/>"
						+ "<element444>111</element444>"
						+ "<element444>444</element444>"
						+ "<elem4 attr10=\"False\" attr11=\"true\" attr12=\"\" attr6=\"False\"/>"
						+ "<element8>True</element8>"
						+ "<element6 attr9=\"Ещё текст\"/>"
						+ "<element7/>"
						+ "<element5 attr7=\"1\"/>"
						+ "<element5 attr8=\"2\">Test3</element5>"
						+ "<element555/>"
						+ "<element555>False</element555>" + "</elem1>");

	}
}
