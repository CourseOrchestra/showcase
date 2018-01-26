package ru.curs.showcase.test.geomap;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.Event;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.test.AbstractTest;

/**
 * Модульные тесты в узком смысле этого слова для карты и ее внутренних
 * компонентов.
 * 
 * @author den
 * 
 */
public class GeoMapTest extends AbstractTest {

	/**
	 * Простой тест на создание объекта карты.
	 */
	@Test
	public void testSimpleCreateObj() {
		GeoMap map = new GeoMap();
		assertNotNull(map.getJavaDynamicData());
		assertNull(map.getJsDynamicData());
		map.getJavaDynamicData().addLayer(GeoMapFeatureType.POLYGON);
		assertEquals(GeoMapFeatureType.POLYGON, map.getJavaDynamicData().getLayers().get(0)
				.getType());
		assertNotNull(map.getJavaDynamicData().getLayers().get(0).getFeatures());
		GeoMapLayer layer = map.getJavaDynamicData().addLayer(GeoMapFeatureType.POINT);
		assertEquals(GeoMapFeatureType.POINT, map.getJavaDynamicData().getLayers().get(1)
				.getType());
		assertNotNull(map.getJavaDynamicData().getLayers().get(1).getFeatures());
		assertNull(layer.addPolygon("aa", "bb"));
		final String pointId = "pointId1";
		final String pointName = "Москва";
		assertNotNull(layer.addPoint(pointId, pointName));
		GeoMapFeature feature = layer.getFeatures().get(0);
		assertEquals(pointId, feature.getId().getString());
		assertEquals(pointName, feature.getName());
		final String indId = "12345";
		final String indName = "Надои";
		GeoMapIndicator ind = layer.addIndicator(indId, indName);
		assertNotNull(ind);
		assertEquals(indId, ind.getId().getString());
		assertEquals(indName, layer.getIndicatorById(indId).getName());
		final double value = 1.0;
		feature.setValue(indId, value);
		assertEquals(value, feature.getValueForIndicator(ind).doubleValue(), 0);
		assertEquals(false, ind.getIsMain());

		assertTrue(map.getAutoSize());
		assertEquals(GeoMapData.AUTOSIZE_CONSTANT, map.getJavaDynamicData().getHeight());
		assertEquals(GeoMapData.AUTOSIZE_CONSTANT, map.getJavaDynamicData().getWidth());
	}

	@Test
	public void testGeoMapEvent() {
		Event event = new GeoMapEvent();
		ID id1 = new ID("id1");
		event.setId1(id1);

		GeoMapEvent gEvent = (GeoMapEvent) event;
		assertEquals(id1, gEvent.getObjectId());

		final String objectId = "objectId";
		gEvent.setObjectId(objectId);
		assertEquals(objectId, event.getId1().toString());
		assertNull(event.getId2());
	}

}
