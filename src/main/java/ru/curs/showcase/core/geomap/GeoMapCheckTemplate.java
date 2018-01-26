package ru.curs.showcase.core.geomap;

/**
 * Класс, используемый для проверки шаблона карты с помощью GSON.
 * 
 * @author den
 * 
 */
public class GeoMapCheckTemplate {
	private GeoMapStyle[] style;
	private String registerSolutionMap;

	public GeoMapStyle[] getStyle() {
		return style;
	}

	public void setStyle(final GeoMapStyle[] aStyle) {
		style = aStyle;
	}

	public String getRegisterSolutionMap() {
		return registerSolutionMap;
	}

	public void setRegisterSolutionMap(final String aRegisterSolutionMap) {
		registerSolutionMap = aRegisterSolutionMap;
	}
}
