package ru.curs.showcase.app.api.geomap;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Настройки экспорта карты.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class GeoMapExportSettings extends TransferableElement
		implements SerializableElement {

	private static final int DEF_JPEG_QUALITY = 90;

	private static final int DEF_HEIGHT = 1280;

	private static final long serialVersionUID = -1152432389114966851L;

	private Integer height = null;
	private Integer width = DEF_HEIGHT;
	private String backgroundColor = null;
	private Integer jpegQuality = DEF_JPEG_QUALITY;

	private String fileName = "geomap";

	public Integer getHeight() {
		return height;
	}

	public void setHeight(final Integer aHeight) {
		height = aHeight;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(final Integer aWidth) {
		width = aWidth;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(final String aBackgroundColor) {
		backgroundColor = aBackgroundColor;
	}

	public Integer getJpegQuality() {
		return jpegQuality;
	}

	public void setJpegQuality(final Integer aJpegQuality) {
		jpegQuality = aJpegQuality;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String aFileName) {
		fileName = aFileName;
	}
}
