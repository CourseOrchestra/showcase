package ru.curs.showcase.app.api.geomap;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelJSBasedElement;

/**
 * Класс карты - элемента информационной панели. На карте отображаются
 * географические данные. TODO - перенести width и height в GeoMap.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class GeoMap extends DataPanelJSBasedElement {

	private static final long serialVersionUID = 3147374218653835207L;

	/**
	 * Динамические данные Java.
	 */
	private GeoMapData javaDynamicData;

	/**
	 * Признак того, что размеры карты должны подстраиваться под размеры инф.
	 * панели. Данный режим включается, если не заданы размеры карты.
	 */
	private Boolean autoSize = false;

	/**
	 * Ширина карты для отображения.
	 */
	private int width;

	/**
	 * Высота карты для отображения.
	 */
	private int height;

	private GeoMapUISettings uiSettings = new GeoMapUISettings();

	private GeoMapExportSettings exportSettings = new GeoMapExportSettings();

	public GeoMap() {
		super();
		init();
	}

	private void init() {
		javaDynamicData = new GeoMapData(this);
		javaDynamicData.initAutoSize();
	}

	public GeoMap(final DataPanelElementInfo aElementInfo) {
		super(aElementInfo);
		init();
	}

	@Override
	protected GeoMapEventManager initEventManager() {
		return new GeoMapEventManager();
	}

	@Override
	public GeoMapEventManager getEventManager() {
		return (GeoMapEventManager) super.getEventManager();
	}

	@Override
	public GeoMapData getJavaDynamicData() {
		return javaDynamicData;
	}

	public void setJavaDynamicData(final GeoMapData aJavaDynamicData) {
		javaDynamicData = aJavaDynamicData;
	}

	public Boolean getAutoSize() {
		return autoSize;
	}

	public void setAutoSize(final Boolean aAutoSize) {
		autoSize = aAutoSize;
	}

	/**
	 * Функция для определения значения признака autoSize. Должна быть вызвана
	 * до сериализации карты в JSON, например из фабрики.
	 */
	void determineAutoSize() {
		if (javaDynamicData != null) {
			autoSize = javaDynamicData.getAutoSize();
		}
	}

	public GeoMapUISettings getUiSettings() {
		return uiSettings;
	}

	public void setUiSettings(final GeoMapUISettings aUiSettings) {
		uiSettings = aUiSettings;
	}

	public GeoMapExportSettings getExportSettings() {
		return exportSettings;
	}

	public void setExportSettings(final GeoMapExportSettings aExportSettings) {
		exportSettings = aExportSettings;
	}

	public void applyAutoSizeValuesOnClient(final int awidth, final int aheight) {
		setJsDynamicData(getJsDynamicData().replace("\"width\":" + GeoMapData.AUTOSIZE_CONSTANT,
				"\"width\":" + awidth));
		setJsDynamicData(getJsDynamicData().replace("\"height\":" + GeoMapData.AUTOSIZE_CONSTANT,
				"\"height\":" + aheight));
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(final int awidth) {
		this.width = awidth;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(final int aheight) {
		this.height = aheight;
	}
}
