package ru.curs.showcase.test.geomap;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.core.AdapterForJS;
import ru.curs.showcase.core.geomap.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Тесты для фабрики карт.
 * 
 * @author den
 * 
 */
public class GeoMapFactoryTest extends AbstractTestWithDefaultUserData {

	private static final String RU_AL_ID = "2";

	/**
	 * Тест на проверку динамических свойств карты, созданной на основе данных
	 * из БД.
	 * 
	 * @throws Exception
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testFromDBDynamicData() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		GeoMapFactory factory = new GeoMapFactory(raw);
		GeoMap map = factory.build();

		final int size = 500;
		GeoMapData data = map.getJavaDynamicData();
		assertEquals(size, data.getHeight().intValue());
		assertEquals(size, data.getWidth().intValue());
		assertEquals(2, data.getLayers().size());
		assertNotNull(data.getLayerById("l1"));
		assertNotNull(data.getLayerById("l2"));
		assertNull(data.getLayerById("l3"));
		assertNotNull(data.getLayerByObjectId("1849"));
		GeoMapLayer layer = data.getLayerByObjectId(RU_AL_ID);
		assertNotNull(layer);
		assertNull(layer.getProjection());
		assertNull(data.getLayerByObjectId("blaaa"));
		assertNull(layer.getHintFormat());
		assertEquals(GeoMapFeatureType.POLYGON, layer.getType());
		final int areasCount = 5;
		assertEquals(areasCount, layer.getFeatures().size());
		assertEquals(2, layer.getIndicators().size());
		GeoMapFeature altay = layer.getObjectById(RU_AL_ID);
		assertEquals("Алтай - регион с показателями", altay.getTooltip());
		assertEquals("#FAEC7B", altay.getStyle());
		assertEquals(altay.getGeometryId(), altay.getStyleClass());
		final int indValue1 = 1000;
		assertNotSame("ind1", layer.getIndicators().get(0).getId().getString());
		assertEquals("ind0", layer.getAttrIdByDBId("ind1").toString());
		assertEquals(false, layer.getIndicators().get(0).getIsMain());
		assertEquals("#2AAA2E", layer.getIndicators().get(0).getStyle());
		assertEquals(indValue1, altay.getValueForIndicator(layer.getIndicators().get(0))
				.doubleValue(), 0);
		final int indValue2 = 10;
		assertEquals(GeoMapLayer.MAIN_IND_NAME, layer.getIndicators().get(1).getId().getString());
		assertEquals(GeoMapLayer.MAIN_IND_NAME, layer.getAttrIdByDBId("ind2").getString());
		assertEquals(layer.getMainIndicator(), layer.getIndicators().get(1));
		assertEquals(true, layer.getIndicators().get(1).getIsMain());
		assertEquals(indValue2, altay.getValueForIndicator(layer.getIndicators().get(1))
				.doubleValue(), 0);
		layer = data.getLayerById("l1");
		GeoMapFeature novgorod = layer.getObjectById("2532");
		assertEquals(String.format("%s - %s (%s) (%s - %s)", layer.getName(), novgorod.getName(),
				novgorod.getId(), novgorod.getLat(), novgorod.getLon()), novgorod.getTooltip());
		assertEquals("TestStyleClass", novgorod.getStyleClass());
	}

	@Test
	public void testApplyAutoSizeValuesOnClient() {
		final int height = 200;
		final int width = 100;
		GeoMap map = new GeoMap();
		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(map);

		assertTrue(map.getJsDynamicData().indexOf("\"width\":" + GeoMapData.AUTOSIZE_CONSTANT) > -1);
		assertTrue(map.getJsDynamicData().indexOf("\"height\":" + GeoMapData.AUTOSIZE_CONSTANT) > -1);
		map.applyAutoSizeValuesOnClient(width, height);
		assertTrue(map.getJsDynamicData().indexOf("\"width\":" + width) > -1);
		assertTrue(map.getJsDynamicData().indexOf("\"height\":" + height) > -1);
	}

	/**
	 * Проверка автоматической установки проекции слоя.
	 */
	@Test
	public void testLayerProjection() {
		GeoMapLayer layer = new GeoMapLayer(GeoMapFeatureType.POINT);
		assertEquals(GeoMapLayer.DEF_POINT_PROJECTION, layer.getProjection());
		layer = new GeoMapLayer(GeoMapFeatureType.POLYGON);
		assertNull(layer.getProjection());
		layer = new GeoMapLayer(GeoMapFeatureType.MULTIPOLYGON);
		assertNull(layer.getProjection());
		layer.setType(GeoMapFeatureType.POINT);
		assertEquals(GeoMapLayer.DEF_POINT_PROJECTION, layer.getProjection());
	}

	@Test
	// @Ignore
	// !!!
			public
			void testTemplateCheckWrongConnectionFile() throws Exception {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		generateTestTabWithElement(elInfo);
		elInfo.setProcName("geomap_wrong_connfile");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, elInfo);
		GeoMapFactory factory = new GeoMapFactory(raw);
		try {
			factory.build();
			fail();
		} catch (GeoMapWrongTemplateException e) {
			assertEquals(
					"Файл подключения подложки fakeFile001.js для карты из geomap_wrong_connfile не найден",
					e.getMessage());
		}
	}

	@Test
	// @Ignore
	// !!!
			public
			void testTemplateCheckWrongStructure() throws Exception {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		generateTestTabWithElement(elInfo);
		elInfo.setProcName("geomap_wrong_structure");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, elInfo);
		GeoMapFactory factory = new GeoMapFactory(raw);
		try {
			factory.build();
			fail();
		} catch (GeoMapWrongTemplateException e) {
			assertTrue(e.getMessage().startsWith(
					"Шаблон карты в geomap_wrong_structure задан ошибочно"));
		}
	}

	@Test
	// @Ignore
	// !!!
			public
			void testTemplateCheckWrongNums() throws Exception {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		generateTestTabWithElement(elInfo);
		elInfo.setProcName("geomap_wrong_nums");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, elInfo);
		GeoMapFactory factory = new GeoMapFactory(raw);
		try {
			factory.build();
			fail();
		} catch (GeoMapWrongTemplateException e) {
			assertTrue(e.getMessage().startsWith(
					"В шаблоне карты в geomap_wrong_nums вместо числа задана строка"));
		}
	}

	@Test
	@Ignore
	// !!!
	public void testJython() throws Exception {
		CompositeContext context = getTestContext1();
		context.setSession("</" + XMLSessionContextGenerator.SESSION_CONTEXT_TAG + ">");
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		element.setProcName("geomap/GeoMapSimple.py");
		generateTestTabWithElement(element);

		RecordSetElementGateway<CompositeContext> gateway = new RecordSetElementJythonGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		GeoMapFactory factory = new GeoMapFactory(raw);
		GeoMap map = factory.build();

		assertEquals(1, map.getJavaDynamicData().getLayers().size());
		assertEquals(GeoMapFeatureType.POINT, map.getJavaDynamicData().getLayerById("l1")
				.getType());
		assertEquals(0, map.getJavaDynamicData().getLayerById("l1").getIndicators().size());
	}

	/**
	 * Тест, проверяющий формирование карты на основе xml-датасета.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testLoadByXmlDs() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "5", "54");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		GeoMapFactory factory = new GeoMapFactory(raw);
		GeoMap map = factory.build();

		GeoMapData data = map.getJavaDynamicData();
		assertEquals(2, data.getLayers().size());
		assertNotNull(data.getLayerById("l1"));
		assertNotNull(data.getLayerById("l2"));
		assertNotNull(data.getLayerByObjectId("1849"));
		GeoMapLayer layer = data.getLayerByObjectId(RU_AL_ID);
		assertNull(layer.getProjection());
		assertNull(data.getLayerByObjectId("fake"));
		assertNull(layer.getHintFormat());
		assertEquals(GeoMapFeatureType.POLYGON, layer.getType());
		final int areasCount = 83;
		assertEquals(areasCount, layer.getFeatures().size());
		assertEquals(1, layer.getIndicators().size());
		GeoMapFeature altay = layer.getObjectById(RU_AL_ID);
		assertEquals("Республика Алтай - производство", altay.getTooltip());
		assertNull(altay.getStyle());
		assertEquals(altay.getGeometryId(), altay.getStyleClass());
		final double indValue1 = 3.8;
		final double delta = 0.01;
		assertNotSame("ind1", layer.getIndicators().get(0).getId().getString());
		assertEquals("mainInd", layer.getAttrIdByDBId("ind1").toString());
		assertEquals(true, layer.getIndicators().get(0).getIsMain());
		assertEquals("#2AAA2E", layer.getIndicators().get(0).getStyle());
		assertEquals(indValue1, altay.getValueForIndicator(layer.getIndicators().get(0))
				.doubleValue(), delta);
		assertEquals(layer.getMainIndicator(), layer.getIndicators().get(0));
		layer = data.getLayerById("l1");
		GeoMapFeature novgorod = layer.getObjectById("2532");
		assertEquals(String.format("%s - %s (%s) (%s - %s)", layer.getName(), novgorod.getName(),
				novgorod.getId(), novgorod.getLat(), novgorod.getLon()), novgorod.getTooltip());
		assertNull(novgorod.getStyleClass());
	}

}
