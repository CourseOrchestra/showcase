package ru.curs.showcase.core.geomap;

/**
 * 
 * Стиль, задаваемый в шаблоне карты. Сейчас используется лишь для проверки.
 * 
 * @author den
 * 
 */
public class GeoMapStyle {
	private Integer strokeWidth;
	private GeoMapStyle point;
	private GeoMapStyle polygon;

	public Integer getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(final Integer aStrokeWidth) {
		strokeWidth = aStrokeWidth;
	}

	public GeoMapStyle getPoint() {
		return point;
	}

	public void setPoint(final GeoMapStyle aPoint) {
		point = aPoint;
	}

	public GeoMapStyle getPolygon() {
		return polygon;
	}

	public void setPolygon(final GeoMapStyle aPolygon) {
		polygon = aPolygon;
	}
}
