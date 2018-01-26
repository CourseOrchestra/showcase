package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.ValidateException;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.core.primelements.navigator.*;
import ru.curs.showcase.core.sp.DBQueryException;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Класс для тестирования фабрики навигаторов.
 * 
 * @author den
 * 
 */
public class NavigatorFactoryTest extends AbstractTestWithDefaultUserData {

	private static final String TEST_ELEMEMNT_NAME = "Вывоз, включая экспорт - Ноябрь";

	/**
	 * Проверка навигатора, созданного обычным конструктором.
	 */
	@Test
	public void testContructor() {
		Navigator nav = new Navigator();
		assertFalse(nav.getHideOnLoad());
		assertNotNull(nav.getGroups());
		assertEquals(0, nav.getGroups().size());
		assertNull(nav.getGroupById("fake"));
		assertNull(nav.getAutoSelectElement());
		assertEquals("300px", nav.getWidth());
	}

	/**
	 * Число элементов в первой группе навигатора.
	 */
	private static final int FIRST_GRP_ELEMENTS_COUNT = 7;

	/**
	 * Тест навигатора, построенного на основе данных из файла XML.
	 * 
	 */
	@Test
	public void testNavigatorFromFile() {
		CompositeContext context =
			new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
		NavigatorFactory factory = new NavigatorFactory(context);
		Navigator nav;
		try (PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.NAVIGATOR)) {
			DataFile<InputStream> file =
				gateway.getRawData(new CompositeContext(), TREE_MULTILEVEL_XML);

			nav = factory.fromStream(file);
		}
		assertEquals("200px", nav.getWidth());
		assertTrue(nav.getHideOnLoad());
		assertEquals(nav.getGroups().get(0).getElements().get(1), nav.getAutoSelectElement());
		final NavigatorGroup firstGroup = nav.getGroupById("1");
		assertEquals("C6CC2BA4-A6ED-4630-8E58-CBBFA9C8C0A9", firstGroup.getElements().get(1)
				.getId().getString());
		assertEquals("1", firstGroup.getId().getString());
		assertEquals("Балансы зерна", firstGroup.getName());
		assertEquals("solutions/default/resources/group_icon_default.png", firstGroup.getImageId());
		assertEquals(FIRST_GRP_ELEMENTS_COUNT, firstGroup.getElements().size());
		assertEquals(0, firstGroup.getElements().get(0).getElements().size());
		assertEquals(1, firstGroup.getElements().get(1).getElements().size());
		assertNotNull(firstGroup.getElements().get(0).getAction());
		NavigatorElement testEl = firstGroup.getElements().get(1);
		assertNotNull(testEl.getAction());
		assertEquals(DataPanelActionType.RELOAD_PANEL, testEl.getAction().getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, testEl.getAction().getNavigatorActionType());
		assertEquals("C6CC2BA4-A6ED-4630-8E58-CBBFA9C8C0A8", testEl.getElements().get(0).getId()
				.getString());
		assertEquals(TEST_ELEMEMNT_NAME, testEl.getElements().get(0).getName());
		Action action = testEl.getElements().get(0).getAction();
		assertNotNull(action);
		DataPanelLink link = action.getDataPanelLink();
		assertNotNull(link);
		assertEquals("test.xml", link.getDataPanelId().getString());
		assertEquals("1", link.getTabId().getString());
		assertNotNull(action.getContext());
		assertEquals(TEST_ELEMEMNT_NAME, action.getContext().getMain());
		assertNull(action.getContext().getAdditional());
		assertEquals(1, link.getElementLinks().size());
		assertEquals("1", link.getElementLinks().get(0).getId().getString());
		assertNotNull(link.getElementLinks().get(0).getContext());
		assertEquals(TEST_ELEMEMNT_NAME, link.getElementLinks().get(0).getContext().getMain());
		assertNull(link.getElementLinks().get(0).getContext().getSession());
		assertEquals("(1=1)", link.getElementLinks().get(0).getContext().getAdditional());
	}

	@Test
	@Ignore
	// !!!
			public
			void testWithSelector() {
		NavigatorSelector selector = new NavigatorSelector();
		CompositeContext context = getTestContext1();
		context.setSession("<" + XMLSessionContextGenerator.SESSION_CONTEXT_TAG + "/>");

		try (PrimElementsGateway gw = selector.getGateway()) {
			DataFile<InputStream> file = gw.getRawData(context, selector.getSourceName());
			NavigatorFactory factory = new NavigatorFactory(context);
			factory.fromStream(file);
		}
	}

	@Test
	// @Ignore
	// !!!
			public
			void testFromDB() {
		CompositeContext context = generateContextWithSessionInfo();
		NavigatorFactory factory = new NavigatorFactory(context);
		try (PrimElementsGateway gateway = new NavigatorDBGateway()) {
			DataFile<InputStream> file = gateway.getRawData(context, "generationtree");
			factory.fromStream(file);
		}
	}

	@Test
	// @Ignore
	// !!!
			public
			void testFromDBWithException() {
		CompositeContext context = generateContextWithSessionInfo();
		try (PrimElementsGateway gateway = new NavigatorDBGateway()) {
			gateway.getRawData(context, "generationtree_re");
			fail();
		} catch (DBQueryException e) {
			GeneralException ge =
				GeneralExceptionFactory.build(e, new DataPanelElementContext(context));
			assertNotNull(ge.getContext().getCompositeContext());
			assertNull(ge.getContext().getElementInfo());

			String mes = GeneralException.generateDetailedInfo(ge);
			assertTrue(mes.contains("просто raiserror"));
			assertTrue(mes.contains("Контекст выполнения:"));
		}
	}

	@Test
	// @Ignore
	// !!!
			public
			void navigatorCanBeCreatedByExecutingMSSQLFile() {
		CompositeContext context = generateContextWithSessionInfo();
		NavigatorFactory factory = new NavigatorFactory(context);
		try (PrimElementsGateway gateway = new NavigatorMSSQLExecGateway()) {
			DataFile<InputStream> file =
				gateway.getRawData(context, "navigator/generate.tree.sql");
			Navigator navigator = factory.fromStream(file);

			assertNotNull(navigator);
			final int groupsCount = 3;
			assertEquals(groupsCount, navigator.getGroups().size());
			assertFalse(navigator.getHideOnLoad());
			assertEquals("199px", navigator.getWidth());
		}
	}

	@Test
	public void codeFromMSSQLFileCanReturnErrorCodeAndMessageInParams() {
		CompositeContext context = generateContextWithSessionInfo();
		try (PrimElementsGateway gateway = new NavigatorMSSQLExecGateway()) {
			gateway.getRawData(context, "navigator/generate.error.sql");
			fail();
		} catch (ValidateException e) {
			assertTrue(e.getLocalizedMessage().contains(
					"Ошибка при построении навигатора: нет данных! (1)"));
		}
	}

}
