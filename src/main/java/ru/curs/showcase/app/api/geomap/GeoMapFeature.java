package ru.curs.showcase.app.api.geomap;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.ID;

/**
 * Класс ГИС объекта (feature) на карте. Применяется для описания точки и
 * полигона, а также любых других типов объектов, которые появятся в будущем.
 * Для объекта может быть задан либо идентификатор его геометрии - в этом случае
 * определение геометрии зашито в шаблоне, либо явное описание его геометрии.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoMapFeature extends GeoMapObject {

	private static final long serialVersionUID = -3726515877064440608L;

	/**
	 * Идентификатор геометрии объекта.
	 */
	private String geometryId = null;

	/**
	 * Координаты точки для карты.
	 */
	private Double[] pointCoords;

	/**
	 * Координаты полигона на карте. Для будущего использования.
	 */
	private Double[][][] polygonCoords;

	/**
	 * Координаты сложного полигона (MULTIPOLYGON) на карте.
	 */
	private Double[][][][] multiPolygonCoords;

	/**
	 * Координаты линии.
	 */
	private Double[][] lineStringCoords;

	/**
	 * Координаты сложной линии.
	 */
	private Double[][][] multiLineStringCoords;

	/**
	 * Клас CSS стиля, который будет использован для отображения данного
	 * объекта. Если одновременно имеются ненулевые styleClass и style, то будет
	 * использоваться style.
	 */
	private String styleClass = null;

	/**
	 * Набор атрибутов ГИС объекта. Как минимум, содержит набор пар <ID
	 * показателя>:<Значение показателя для объекта>.
	 */
	private Map<String, Double> attrs = new TreeMap<String, Double>();

	public Double[][][] getPolygonCoords() {
		return polygonCoords;
	}

	public void setPolygonCoords(final Double[][][] aPolygonCoordinates) {
		polygonCoords = aPolygonCoordinates;
	}

	public Double[][][][] getMultiPolygonCoords() {
		return multiPolygonCoords;
	}

	public void setMultiPolygonCoords(final Double[][][][] aMultiPolygonCoordinates) {
		multiPolygonCoords = aMultiPolygonCoordinates;
	}

	public Double[] getPointCoords() {
		return pointCoords;
	}

	public void setPointCoords(final Double[] aPointCoordinates) {
		pointCoords = aPointCoordinates;
	}

	public GeoMapFeature() {
		super();
	}

	public GeoMapFeature(final String aId, final String aName) {
		super(aId, aName);
	}

	public Map<String, Double> getAttrs() {
		return attrs;
	}

	public void setAttrs(final Map<String, Double> aAttrs) {
		attrs = aAttrs;
	}

	/**
	 * Устанавливает значение показателя для объекта.
	 * 
	 * @param indId
	 *            - идентификатор показателя.
	 * @param value
	 *            - значение показателя.
	 */
	public void setValue(final String indId, final Double value) {
		attrs.put(indId, value);
	}

	/**
	 * Возвращает значение показателя для переданного объекта.
	 * 
	 * @param ind
	 *            - объект.
	 * @return - значение.
	 */
	public Double getValueForIndicator(final GeoMapIndicator ind) {
		if (ind == null) {
			return null;
		}
		for (String key : attrs.keySet()) {
			if (ind.getId().equals(new ID(key))) {
				return attrs.get(key);
			}
		}
		return null;
	}

	public String getGeometryId() {
		return geometryId;
	}

	public void setGeometryId(final String aGeometryId) {
		geometryId = aGeometryId;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(final String aStyleClass) {
		styleClass = aStyleClass;
	}

	/**
	 * Возвращает Latitude для точки на карте.
	 * 
	 * @return - Latitude.
	 */
	public Double getLat() {
		return pointCoords[1];
	}

	/**
	 * Возвращает longitude для точки на карте.
	 * 
	 * @return - longitude.
	 */
	public Double getLon() {
		return pointCoords[0];
	}

	public Double[][] getLineStringCoords() {
		return lineStringCoords;
	}

	public void setLineStringCoords(final Double[][] aLineStringCoordinates) {
		lineStringCoords = aLineStringCoordinates;
	}

	public Double[][][] getMultiLineStringCoords() {
		return multiLineStringCoords;
	}

	public void setMultiLineStringCoords(final Double[][][] aMultiLineStringCoordinates) {
		multiLineStringCoords = aMultiLineStringCoordinates;
	}
}
