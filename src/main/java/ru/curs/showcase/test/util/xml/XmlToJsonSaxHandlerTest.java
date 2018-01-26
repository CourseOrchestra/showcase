package ru.curs.showcase.test.util.xml;

import static org.junit.Assert.*;

import java.io.InputStream;

import javax.xml.parsers.*;

import org.junit.Test;

import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.XmlToJsonSaxHandler;

import com.google.gson.*;

/**
 * Тест Sax Handler конвертации XML в JSON.
 * 
 * @author bogatov
 * 
 */
public class XmlToJsonSaxHandlerTest {
	private final int arrayCount = 3;

	@Test
	public void test() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		String xml = "<items attr1=\"1\" attr2=\"2\">"
				+ "<item attr1=\"1\">val1</item><item>val2</item><item>val3</item>"
				+ "<element>value</element></items>";
		XmlToJsonSaxHandler handler = new XmlToJsonSaxHandler();
		InputStream in = TextUtils.stringToStream(xml);
		parser.parse(in, handler);
		JsonElement json = handler.getResult();
		assertNotNull(json);
		assertTrue(json.isJsonObject());
		JsonObject obj = (JsonObject) json;
		JsonElement element = obj.get("items");
		assertNotNull(element);
		assertTrue(element.isJsonObject());
		obj = (JsonObject) element;
		assertTrue(obj.get("attr1").isJsonPrimitive());
		assertTrue(obj.get("attr2").isJsonPrimitive());
		assertTrue(obj.get("item").isJsonArray());
		assertTrue(obj.get("element").isJsonPrimitive());
		JsonArray array = (JsonArray) obj.get("item");
		assertTrue(array.size() == arrayCount);
	}
}
