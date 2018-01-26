package ru.curs.showcase.app.api.geomap;

import javax.xml.bind.annotation.*;

/**
 * Формат изображения.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum ImageFormat {
	SVG("application/svg+xml"), PNG("image/png"), JPG("image/jpg");

	private String contentType;

	ImageFormat(final String aName) {
		contentType = aName;
	}

	public String getContentType() {
		return contentType;
	}
}
