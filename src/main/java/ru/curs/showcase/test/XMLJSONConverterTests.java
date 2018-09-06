package ru.curs.showcase.test;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static ru.curs.showcase.util.XMLJSONConverter.xmlToJson;
import static ru.curs.showcase.util.XMLJSONConverter.jsonToXml;


    public class XMLJSONConverterTests {

        static String json;
        static String xml;

        @BeforeAll
        static void initAll() {
            json = "";
            xml = "";
        }

        @BeforeEach
        void init() {
        }

        /**
         * Тесты, показывающие работостпособность конвертера в нетривиальных случаях.
         */
        @Test
        @DisplayName("not null tests")
        void notNullTests() {
            assertAll(
                    () -> assertNotNull(xmlToJson("<root><a>&#xA;</a></root>")),
                    () -> assertNotNull(xmlToJson("<root><name>Bell в модификации Walsh && mm& &mm &amp; Kliegman</name></root>")),
                    () -> assertNotNull(jsonToXml("{\"gridtoolbar\":{\"item\": None}}")),
                    () -> assertNotNull(jsonToXml("{\"gridtoolbar\":{\"item\": '&nbsp;'}}")),
                    () -> assertNotNull(xmlToJson("<schema>&nbsp;</schema>")),
                    () -> assertNotNull(jsonToXml("{\"a\":'&gt;'}"))
            );
        }

        /**
         * Тест, показывающий работу конвертера в случае сложных атрибутов тэгов xml, в частности, пространств имён.
         * @throws SAXException
         * @throws IOException
         */
        @Test
        @DisplayName("equals test")
        void equalsTest() throws SAXException, IOException {
            assertEquals(xmlToJson("<root xmlns=\"http://share.curs.ru\" "
                    + "xmlns:si=\"http://share.curs.ru/svn\">"
                    + "<si:size si:mmm=\"ok\"/>"
                    + "</root>"), "{\"root\":{\"si:size\":{\"@si:mmm\":\"ok\"},\"@xmlns\":\"http://share.curs.ru\",\"@xmlns:si\":\"http://share.curs.ru/svn\"}}");
        }

        /**
         * Тест, показывающий различные варианты атрибутов json-строки.
         * @throws JSONException
         * @throws TransformerException
         * @throws ParserConfigurationException
         */
        @Test
        @DisplayName("another equals test")
        void complicatedEqualsTest() throws JSONException, TransformerException, ParserConfigurationException {
            assertEquals(jsonToXml("{\"qs\":{\"#sorted\":[{\"q\":[{\"@d\":\"31\",\"#text\":\"1\"},{\"@d\":\"32\",\"#text\":\"2\"},{\"@d\":\"33\",\"#text\":\"4\"}]}]}}"),
                    "<qs>\r\n" +
                    "<q d=\"31\">1</q>\r\n" +
                    "<q d=\"32\">2</q>\r\n" +
                    "<q d=\"33\">4</q>\r\n" +
                    "</qs>");
        }

        /**
         * Тест, показыающий, что атрибуты json могут НЕ начинаться с префикса @.
         * @throws SAXException
         * @throws IOException
         */
        @Test
        @DisplayName("other equals test")
        void otherStartingAttributesTests() throws SAXException, IOException
        {
            assertEquals(xmlToJson("<root><size mmm=\"ok\"/></root>", false),"{\"root\":{\"size\":{\"mmm\":\"ok\"}}}");
        }


        /**
         * Простой тест, показывающий, что указанный фрагмент xml содержится в cконвертированной json-строке.
         * @throws JSONException
         * @throws TransformerException
         * @throws ParserConfigurationException
         */
        @Test
        @DisplayName("truth test")
        void truthTest() throws JSONException, TransformerException, ParserConfigurationException {
            assertTrue(jsonToXml("{\"root\":{\"size\":{\"@mmm\":\"ok\"}}}").contains("<size mmm=\"ok\"/>"));
        }


        /**
         * Простой тест, показывающий, что невалидный json конвертироваться не будет и при этом будет выбрасываеться указанное исключение.
         * @throws JSONException
         */
        @Test
        @DisplayName("throws test")
        void throwsTest() throws JSONException {
            assertThrows(NullPointerException.class, () -> jsonToXml("{\"root\":{\"size\":{\"@mmm\":\"ok\"}}"));
        }

        @AfterEach
        void tearDown() {
        }

        @AfterAll
        static void tearDownAll() {
        }
}
