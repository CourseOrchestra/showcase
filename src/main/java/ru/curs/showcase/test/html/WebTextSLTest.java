package ru.curs.showcase.test.html;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;

import org.junit.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.html.webtext.WebTextGetCommand;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.ReflectionUtils;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тест для WebTextDBGateway.
 * 
 * @author den
 * 
 */
public class WebTextSLTest extends AbstractTestWithDefaultUserData {

	/**
	 * Основной тест для проверки работы WebTextDBGateway.
	 */
	@Test
	@Ignore
	// !!!
	public void testGetDataBySP() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "1");

		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();

		assertNotNull(context.getSession());
		assertEquals(0, wt.getEventManager().getEvents().size());
		assertNull(wt.getDefaultAction());
		assertNotNull(wt.getData());
	}

	@Test
	public void testGetDataBySQLScript() throws SAXException, IOException {
		CompositeContext context = new CompositeContext();
		context.setMain(MAIN_CONDITION);
		context.setAdditional(ADD_CONDITION);
		DataPanelElementInfo element = new DataPanelElementInfo("1", DataPanelElementType.WEBTEXT);
		element.setProcName("webtext/test.sql");
		generateTestTabWithElement(element);

		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();

		assertNotNull(wt);
		assertEquals(1, wt.getEventManager().getEvents().size());
		assertNotNull(wt.getDefaultAction());
		assertXMLEqual("<div><a onclick=\"gwtWebTextFunc('7', '1')\" class=\"linkStyle\" >"
				+ ADD_CONDITION + "</a></div>", wt.getData());
	}

	@Test
	public void testGetDataByFile() throws IOException, SAXException {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		final String webtextFile = "3buttons_enh.xml";
		element.setProcName(webtextFile);

		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();

		Document doc = XMLUtils.stringToDocument(wt.getData());
		DocumentBuilder db = XMLUtils.createBuilder();
		InputStream stream = UserDataUtils.loadUserDataToStream("data/webtext/" + webtextFile);
		Document expected = db.parse(stream);

		assertEquals(expected.getDocumentElement().getNodeName(), doc.getDocumentElement()
				.getNodeName());
		assertEquals(expected.getDocumentElement().getChildNodes().getLength(), doc
				.getDocumentElement().getChildNodes().getLength());
		assertEquals(expected.getDocumentElement().getChildNodes().item(0).getNodeName(), doc
				.getDocumentElement().getChildNodes().item(0).getNodeName());
	}

	/**
	 * Тест на выборку событий и действия по умолчанию из БД.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@Test
	@Ignore
	// !!!
	public void testEventsAndDefAction() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "3");
		CompositeContext clonedContext = context.gwtClone();

		WebTextGetCommand command = new WebTextGetCommand(clonedContext, element);
		WebText wt = command.execute();

		assertEquals(1, wt.getEventManager().getEvents().size());
		assertEquals("0", wt.getEventManager().getEvents().get(0).getId1().getString());

		Action action = wt.getEventManager().getEvents().get(0).getAction();
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		action = wt.getDefaultAction();
		assertNotNull(action);
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		assertNotNull(wt.getData());
	}

	private void stdCheckAction(final CompositeContext context, final Action action)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		assertEquals(NavigatorActionType.DO_NOTHING, action.getNavigatorActionType());
		assertTrue(ReflectionUtils.equals(context, action.getContext()));
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertTrue(ReflectionUtils.equals(context, action.getDataPanelLink().getElementLinks()
				.get(0).getContext()));
		assertNull(action.getContext().getSession());
	}

	/**
	 * Тест для случая, когда не задана хранимая процедура, возвращающая данные.
	 */
	@Test
	public void testGetStaticDataByXSLT() {
		DataPanelElementInfo el = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		CompositeContext context = new CompositeContext();
		el.setTransformName("bal_test.xsl");
		generateTestTabWithElement(el);

		WebTextGetCommand command = new WebTextGetCommand(context, el);
		WebText wt = command.execute();

		assertTrue(wt.getData().startsWith("<h3>Здесь находится просто статический текст</h3>"));
		assertTrue(wt.getData().endsWith(
				"<p>Коля у Светы спёр кассеты, а Света у Коли уперла костет</p>"));
	}

	@Test
	@Ignore
	// !!!
	public void testJythonTransform() {
		DataPanelElementInfo el = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		CompositeContext context = getTestContext3();
		el.setTransformName("transform/pas.py");
		generateTestTabWithElement(el);

		WebTextGetCommand command = new WebTextGetCommand(context, el);
		WebText wt = command.execute();
		assertTrue(wt.getData().indexOf("Паспорт региона") > -1);
	}

	@Test
	@Ignore
	// !!!
	public void testSPTransform() {
		DataPanelElementInfo el = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		CompositeContext context = getTestContext3();
		el.setTransformName("webtext_pas_tranform");
		generateTestTabWithElement(el);

		WebTextGetCommand command = new WebTextGetCommand(context, el);
		WebText wt = command.execute();
		assertTrue(wt.getData().indexOf("Паспорт региона") > -1);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testWrongElement1() {
		DataPanelElementInfo element =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);

		WebTextGetCommand command = new WebTextGetCommand(new CompositeContext(), element);
		try {
			command.execute();
			fail();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test
	public void testWrongElement2() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", null);
		element.setProcName("proc");

		WebTextGetCommand command = new WebTextGetCommand(new CompositeContext(), element);
		try {
			command.execute();
			fail();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test
	public void testWrongElement3() {
		WebTextGetCommand command = new WebTextGetCommand(new CompositeContext(), null);
		try {
			command.execute();
			fail();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}
}
